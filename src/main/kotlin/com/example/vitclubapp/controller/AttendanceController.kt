package com.example.vitclubapp.controller

import com.example.vitclubapp.service.QRCodeService
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
@RequestMapping("/attendance")
class AttendanceController(private val qrCodeService: QRCodeService) {
    @GetMapping("/{eventId}/attendance")
    fun getAttendanceForEvent(@PathVariable eventId: Long): ResponseEntity<Resource> {
        // Path to your summary file
        val summaryFilePath = "attendance/event_${eventId}_attendance.xlsx"
        val file = File(summaryFilePath)

        // Check if the file exists
        if (!file.exists()) {
            return ResponseEntity.notFound().build() // Return 404 if file doesn't exist
        }

        // Prepare the file as a resource
        val resource = FileSystemResource(file)

        // Return the file with the appropriate headers
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM) // Set the content type
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${file.name}") // Set the filename in the header
            .body(resource) // Return the file as the response body
    }



    @PostMapping("/add")
    fun addAttendance(
        @RequestParam userId: Long,
        @RequestParam eventId: Long
    ): String {
        return try {
            qrCodeService.addAttendanceToExcel(userId, eventId)
            "Attendance added successfully for user $userId and event $eventId"
        } catch (e: Exception) {
            e.printStackTrace()
            "Failed to add attendance: ${e.message}"
        }
    }
}
