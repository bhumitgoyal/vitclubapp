package com.example.vitclubapp.service

import com.example.vitclubapp.model.Event
import com.example.vitclubapp.repository.EventRepository
import com.example.vitclubapp.repository.UserRepository
import org.springframework.stereotype.Service


@Service
class EventService(private val eventRepository: EventRepository,
    private val userRepository: UserRepository)
{

    fun createEvent(event: Event){
        eventRepository.save(event)

    }

    fun getAllEvents():List<Event>{
        return eventRepository.findAll()
    }

    fun registerForEvent(eventId:Long, userId:Long){
        val event = eventRepository.findById(eventId).orElseThrow {RuntimeException("Event not found!")}
        val user = userRepository.findById(userId).orElseThrow{RuntimeException("User not found!")}

        if(event.registeredUsers.contains(user)){
            throw RuntimeException("User already registered for the event")
        }

        event.registeredUsers.add(user)
        user.registeredEvents.add(event)

        eventRepository.save(event)
    }
}