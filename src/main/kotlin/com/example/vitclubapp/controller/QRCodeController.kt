package com.example.vitclubapp.controller

import com.example.vitclubapp.service.QRCodeService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@RestController
@RequestMapping("/qrcode")
class QRCodeController(private val qrCodeService: QRCodeService) {
    @PostMapping("/scan")
    fun scanQRCode(@RequestParam qrCodeFilePath: String): ResponseEntity<String> {
        return try {
            val qrCodeContent = qrCodeService.scanQRCode(qrCodeFilePath)
            val (userId, eventId) = qrCodeContent.split(", ").map { it.split(":")[1].trim() }

            qrCodeService.addAttendanceToExcel(userId.toLong(), eventId.toLong())
            ResponseEntity.ok("User attendance recorded successfully")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body("Error scanning QR code: ${e.message}")
        }
    }


    @GetMapping("/{userId}/{eventId}")
    fun generateQRCode(@PathVariable userId: Long, @PathVariable eventId: Long): ResponseEntity<ByteArray> {
        val image = qrCodeService.generateQRCode(userId, eventId)

        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_PNG

        return ResponseEntity(imageBytes, headers, 200)
    }
}
