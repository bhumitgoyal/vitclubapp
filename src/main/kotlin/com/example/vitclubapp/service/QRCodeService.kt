package com.example.vitclubapp.service

import com.example.vitclubapp.repository.EventRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.image.BufferedImage
import java.time.LocalDateTime


@Service
class QRCodeService(private val eventRepository: EventRepository) {

    fun generateQRCode(userId: Long, eventId: Long): BufferedImage {
        val event = eventRepository.findById(eventId).orElseThrow { RuntimeException("Event not found") }

        // Check if the event has ended
        if (event.endTime.isBefore(LocalDateTime.now())) {
            throw RuntimeException("QR code has expired for this event.")
        }

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
        return image
    }
}
