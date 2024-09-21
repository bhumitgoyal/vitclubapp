package com.example.vitclubapp.service

import com.example.vitclubapp.repository.EventRepository
import com.example.vitclubapp.repository.UserRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDateTime
import javax.imageio.ImageIO
import java.util.UUID
import java.time.format.DateTimeFormatter



@Service
class QRCodeService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) {

    fun addAttendanceToExcel(userId: UUID, eventId: Long) {
        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        val event = eventRepository.findById(eventId).orElseThrow { RuntimeException("Event not found") }

        event.attendanceCount += 1
        val fileName = "attendance/event_${eventId}_attendance.xlsx"
        val file = File(fileName)

        val workbook: Workbook = if (file.exists()) {
            try {
                WorkbookFactory.create(FileInputStream(file))
            } catch (e: Exception) {
                println("Corrupted file detected. Recreating the file: ${e.message}")
                file.delete()
                XSSFWorkbook()
            }
        } else {
            XSSFWorkbook() // Create a new workbook if file doesn't exist
        }

        try {
            // Ensure the "Attendance" sheet exists
            val sheet = workbook.getSheet("Attendance") ?: workbook.createSheet("Attendance")
            if (sheet.physicalNumberOfRows == 0) {
                // First row: Event details (Name, Date, Club)
                val eventDetailsRow = sheet.createRow(0)
                eventDetailsRow.createCell(0).setCellValue("Event Name: ${event.name}")
                eventDetailsRow.createCell(1).setCellValue("Date: ${event.startTime.split("T").first()}") // Assuming ISO-8601 date
                eventDetailsRow.createCell(2).setCellValue("Club: ${event.club.name}")

                // Second row: Column headings (Reg No, Name, Date-Time of Attendance)
                val headerRow = sheet.createRow(1)
                headerRow.createCell(0).setCellValue("Reg No")
                headerRow.createCell(1).setCellValue("Name")
                headerRow.createCell(2).setCellValue("Date-Time of Attendance")
            }
            val row = sheet.createRow(sheet.physicalNumberOfRows)
            row.createCell(0).setCellValue(user.registrationNumber)
            row.createCell(1).setCellValue(user.name)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            row.createCell(2).setCellValue(LocalDateTime.now().format(formatter))

            // Write workbook to the file
            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }

            println("Successfully added attendance for user $userId for event $eventId.")
        } catch (e: Exception) {
            println("Failed to write to Excel file: ${e.message}")
            throw RuntimeException("Failed to write to Excel file: ${e.message}", e)
        } finally {
            workbook.close()
        }
    }

    fun saveQRCodeImage(userId: UUID, eventId: Long, image: BufferedImage) {
        try {
            val qrCodeDir = File("qr_codes")
            if (!qrCodeDir.exists()) {
                qrCodeDir.mkdirs()
            }

            val qrCodePath = File(qrCodeDir, "user_${userId}_event_${eventId}.png")
            ImageIO.write(image, "png", qrCodePath)

            println("QR code saved to: ${qrCodePath.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to save QR code image.", e)
        }
    }

    fun scanQRCode(filePath: String): String {
        val bufferedImage = ImageIO.read(File(filePath))
        val source = BufferedImageLuminanceSource(bufferedImage)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val result = QRCodeReader().decode(bitmap)
        return result.text
    }

    fun generateQRCode(userId: UUID, eventId: Long): BufferedImage {
        val event = eventRepository.findById(eventId).orElseThrow { RuntimeException("Event not found") }

        // Optionally, check if the event has ended or if the user is registered
        // Uncomment and modify according to your requirements
        /*
        if (event.endTime.isBefore(LocalDateTime.now())) {
            throw RuntimeException("QR code has expired for this event.")
        }
        if (!event.registeredUsers.any { it.id == userId }) {
            throw RuntimeException("User is not registered for this event.")
        }
        */

        // Generate the QR code content
        val qrCodeContent = "User: $userId, Event: $eventId"
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 250, 250)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        for (x in 0 until width) {
            for (y in 0 until height) {
                image.setRGB(x, y, if (bitMatrix.get(x, y)) Color.BLACK.rgb else Color.WHITE.rgb)
            }
        }

        // Save the QR code image
        saveQRCodeImage(userId, eventId, image)

        return image
    }
}
