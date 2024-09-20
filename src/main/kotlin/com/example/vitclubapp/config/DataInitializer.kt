package com.example.vitclubapp.config

import com.example.vitclubapp.model.Event
import com.example.vitclubapp.model.User
import com.example.vitclubapp.repository.EventRepository
import com.example.vitclubapp.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val user1 = User(name = "John Doe", registrationNumber = "123456", email = "john@example.com", phoneNumber = "1234567890", role = "Student")
        userRepository.save(user1)

        val event1 = Event(name = "Club Meeting", clubName = "Tech Club", organizerClub= "Alice", location = "Room 101", startTime = LocalDateTime.now().plusHours(1), endTime = LocalDateTime.now().plusHours(2))
        eventRepository.save(event1)
    }
}
