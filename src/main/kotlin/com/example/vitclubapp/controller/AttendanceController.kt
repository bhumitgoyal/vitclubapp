package com.example.vitclubapp.controller

import com.example.vitclubapp.service.QRCodeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/attendance")
class AttendanceController(private val qrCodeService: QRCodeService) {

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
