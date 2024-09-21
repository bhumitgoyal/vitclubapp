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
        return try{
            eventService.registerForEvent(eventId,userId)
            ResponseEntity.ok("Registered Successfully")
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