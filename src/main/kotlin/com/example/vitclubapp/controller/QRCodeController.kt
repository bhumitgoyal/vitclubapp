package com.example.vitclubapp.controller

import com.example.vitclubapp.service.QRCodeService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@RestController
@RequestMapping("/qrcode")
class QRCodeController(private val qrCodeService: QRCodeService) {

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
