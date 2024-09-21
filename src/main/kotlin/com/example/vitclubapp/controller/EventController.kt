package com.example.vitclubapp.controller

import com.example.vitclubapp.model.Event
import com.example.vitclubapp.service.EventService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/events")
class EventController(private val eventService: EventService) {
    @GetMapping("/{id}")
    fun getEventById(@PathVariable id: Long): ResponseEntity<Event> {
        return ResponseEntity.ok(eventService.getEventById(id))
    }

    @PutMapping("/{id}")
    fun updateEvent(@PathVariable id: Long, @RequestBody event: Event): ResponseEntity<Event> {
        return ResponseEntity.ok(eventService.updateEvent(id, event))
    }
    @DeleteMapping("/{id}")
    fun deleteEvent(@PathVariable id: Long): ResponseEntity<String> {
        eventService.deleteEvent(id)
        return ResponseEntity.ok("Event deleted successfully")
    }




    @PostMapping("/{eventId}/register")
    fun registerForEvent(@PathVariable eventId:Long, @RequestParam userId: UUID):ResponseEntity<String>{
        val event = eventService.getEventById(eventId)
        if (event.registeredUsers.size >= event.maxAttendees) {
            return ResponseEntity.badRequest().body("Sorry! Event is full.")
        }
        return try{
            eventService.registerForEvent(eventId,userId)


            val downloadLink = "http://localhost:8080/qrcode/${userId}/${eventId}"


            ResponseEntity.ok("Registered successfully! Download your QR code here: $downloadLink")
        }
        catch (e:RuntimeException){
            ResponseEntity.badRequest().body(e.message)
        }

    }
    @GetMapping
    fun getAllEvents():List<Event>{
        return eventService.getAllEvents()
    }

    @PostMapping
    fun createEvent(@RequestBody event: Event):Event{
        return eventService.createEvent(event)

    }
    @PostMapping("/{eventId}/finish")
    fun finishEvent(@PathVariable eventId: Long) {
        eventService.finishEvent(eventId)
    }

}