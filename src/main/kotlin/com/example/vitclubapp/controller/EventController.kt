package com.example.vitclubapp.controller

import com.example.vitclubapp.model.Event
import com.example.vitclubapp.service.EventService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/events")
class EventController(private val eventService: EventService) {

    @PostMapping("/{eventId}/register")
    fun registerForEvent(@PathVariable eventId:Long, @RequestParam userId:Long):ResponseEntity<String>{
        return try{
            eventService.registerForEvent(eventId,userId)
            ResponseEntity.ok("Registered Successfully")
        }
        catch (e:RuntimeException){
            ResponseEntity.ok(e.message)
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

}