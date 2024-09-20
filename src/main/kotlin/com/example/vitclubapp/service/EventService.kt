package com.example.vitclubapp.service

import com.example.vitclubapp.model.Event
import com.example.vitclubapp.repository.EventRepository
import com.example.vitclubapp.repository.UserRepository
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val qrCodeService: QRCodeService
) {

    fun createEvent(event: Event): Event {
        return eventRepository.save(event)
    }

    fun getAllEvents(): List<Event> {
        return eventRepository.findAll()
    }

    fun registerForEvent(eventId: Long, userId: Long) {
        val event = eventRepository.findById(eventId).orElseThrow { RuntimeException("Event not found!") }
        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found!") }

        if (event.registeredUsers.contains(user)) {
            throw RuntimeException("User already registered for the event")
        }

        event.registeredUsers.add(user)
        user.registeredEvents.add(event)

        eventRepository.save(event)
        val qrCodeImage = qrCodeService.generateQRCode(userId, eventId)
        qrCodeService.saveQRCodeImage(userId, eventId, qrCodeImage)
    }

    fun finishEvent(eventId: Long) {
        val event = eventRepository.findById(eventId).orElseThrow { RuntimeException("Event not found") }

        // Path to the attendance Excel file
        val attendanceFilePath = "attendance/event_${eventId}_attendance.xlsx"
        val attendanceFile = File(attendanceFilePath)

        // Create a new Excel workbook for the summary
        val summaryFilePath = "attendance/event_${eventId}_summary.xlsx"
        val summaryWorkbook = XSSFWorkbook()
        val summarySheet = summaryWorkbook.createSheet("Event Summary")

        // Write event details header
        val headerRow = summarySheet.createRow(0)
        val headers = listOf("Event Name", "Organizer Club", "Location", "Start Time", "End Time", "Total Registered", "Total Attended")
        headers.forEachIndexed { index, header -> headerRow.createCell(index).setCellValue(header) }

        // Fill in event details
        val summaryRow = summarySheet.createRow(1)
        summaryRow.createCell(0).setCellValue(event.name) // String
        summaryRow.createCell(1).setCellValue(event.organizerClub) // String
        summaryRow.createCell(2).setCellValue(event.location) // String
        summaryRow.createCell(3).setCellValue(event.startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) // String
        summaryRow.createCell(4).setCellValue(event.endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) // String
        summaryRow.createCell(5).setCellValue(event.registeredUsers.size.toDouble()) // Numeric

        // Read attendance details
        if (attendanceFile.exists()) {
            val attendanceWorkbook = WorkbookFactory.create(FileInputStream(attendanceFile))
            val attendanceSheet = attendanceWorkbook.getSheetAt(0)

            // Calculate total attended based on the number of filled rows
            val totalAttended = attendanceSheet.physicalNumberOfRows
            summaryRow.createCell(6).setCellValue(totalAttended.toDouble()) // Numeric

            // Copy attendees into the summary file
            for (i in 0 until totalAttended) {
                val attendeeRow = attendanceSheet.getRow(i)
                val newRow = summarySheet.createRow(i + 2) // Start after the summary row
                newRow.createCell(0).setCellValue(attendeeRow.getCell(0).stringCellValue) // Registration number
                newRow.createCell(1).setCellValue(attendeeRow.getCell(1).stringCellValue) // Attendance time
            }

            attendanceWorkbook.close()
        } else {
            summaryRow.createCell(6).setCellValue(0.0) // No attendees, numeric zero
        }

        // Write summary workbook to file
        FileOutputStream(summaryFilePath).use { outputStream ->
            summaryWorkbook.write(outputStream)
        }

        summaryWorkbook.close()
        println("Event finished and summary created: $summaryFilePath")
    }

}
