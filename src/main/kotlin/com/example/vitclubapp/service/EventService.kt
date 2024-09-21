package com.example.vitclubapp.service

import com.example.vitclubapp.exception.ResourceNotFoundException
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
    fun getEventById(id: Long): Event {
        return eventRepository.findById(id).orElseThrow { ResourceNotFoundException("Event not found") }
    }
    fun deleteEvent(id: Long) {
        eventRepository.deleteById(id)
    }fun updateEvent(id: Long, updatedEvent: Event): Event {
        // Fetch the existing event
        val event = getEventById(id)

        // Update the fields with the values from updatedEvent
        event.name = updatedEvent.name
        event.clubName = updatedEvent.clubName
        event.organizerClub = updatedEvent.organizerClub
        event.location = updatedEvent.location
        event.startTime = updatedEvent.startTime
        event.endTime = updatedEvent.endTime
        // If there are other fields, update them similarly

        // Save the updated event
        return eventRepository.save(event)
    }

    fun createEvent(event: Event): Event {
        return eventRepository.save(event)
    }

    fun getAllEvents(): List<Event> {
        return eventRepository.findAll()
    }

    fun registerForEvent(eventId: Long, userId: Long) {
        val event = eventRepository.findById(eventId)
            .orElseThrow { RuntimeException("Event not found!") }
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found!") }

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
        val event = eventRepository.findById(eventId)
            .orElseThrow { RuntimeException("Event not found") }

        // Path for the attendance and summary files
        val attendanceFilePath = "attendance/event_${eventId}_attendance.xlsx"
        val summaryFilePath = "attendance/event_${eventId}_summary.xlsx"

        // Create new workbook for the summary
        XSSFWorkbook().use { summaryWorkbook ->
            val summarySheet = summaryWorkbook.createSheet("Event Summary")

            // Write headers for event details
            val headerRow = summarySheet.createRow(0)
            val headers = listOf(
                "Event Name", "Organizer Club", "Location", "Start Time",
                "End Time", "Total Registered", "Total Attended"
            )
            headers.forEachIndexed { index, header ->
                headerRow.createCell(index).setCellValue(header)
            }

            // Fill event details
            val summaryRow = summarySheet.createRow(1)
            summaryRow.createCell(0).setCellValue(event.name)
            summaryRow.createCell(1).setCellValue(event.organizerClub)
            summaryRow.createCell(2).setCellValue(event.location)
            summaryRow.createCell(3).setCellValue(event.startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            summaryRow.createCell(4).setCellValue(event.endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            summaryRow.createCell(5).setCellValue(event.registeredUsers.size.toDouble())

            val totalAttended = readAttendanceAndAddToSummary(attendanceFilePath, summarySheet)
            summaryRow.createCell(6).setCellValue(totalAttended.toDouble())

            // Write the summary to the file
            FileOutputStream(summaryFilePath).use { outputStream ->
                summaryWorkbook.write(outputStream)
            }

            println("Event finished and summary created: $summaryFilePath")
        }
    }

    /**
     * Reads the attendance Excel file and adds the attendees' data to the summary sheet.
     * Returns the number of attendees.
     */
    private fun readAttendanceAndAddToSummary(attendanceFilePath: String, summarySheet: org.apache.poi.ss.usermodel.Sheet): Int {
        val attendanceFile = File(attendanceFilePath)
        if (!attendanceFile.exists()) {
            return 0
        }

        FileInputStream(attendanceFile).use { inputStream ->
            val attendanceWorkbook = WorkbookFactory.create(inputStream)
            val attendanceSheet = attendanceWorkbook.getSheetAt(0)
            val totalAttended = attendanceSheet.physicalNumberOfRows

            // Copy attendees to the summary
            for (i in 0 until totalAttended) {
                val attendeeRow = attendanceSheet.getRow(i)
                val newRow = summarySheet.createRow(i + 2) // Start after the summary row
                newRow.createCell(0).setCellValue(attendeeRow.getCell(0).stringCellValue) // Registration number
                newRow.createCell(1).setCellValue(attendeeRow.getCell(1).stringCellValue) // Attendance time
            }
            attendanceWorkbook.close()
            return totalAttended
        }
    }
}
