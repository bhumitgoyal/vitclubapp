package com.example.vitclubapp.config

import com.example.vitclubapp.model.Club
import com.example.vitclubapp.model.Event
import com.example.vitclubapp.model.User
import com.example.vitclubapp.model.UserRole
import com.example.vitclubapp.repository.ClubRepository
import com.example.vitclubapp.repository.EventRepository
import com.example.vitclubapp.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val clubRepository: ClubRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Creating a user with the role 'ADMIN' from the UserRole enum
        val adminUser = User(
            name = "John Doe",
            registrationNumber = "123456",
            email = "john@example.com",
            phoneNumber = "1234567890",
            role = UserRole.ADMIN // Using the enum for the user role
        )
        userRepository.save(adminUser)

        // Creating a club with the user as the admin
        val club = Club(
            name = "PLA",
            cluborchap = "Club",
            admin = adminUser, // Associating the club with the user as the admin
            description = "Dance Club of VIT"
        )
        clubRepository.save(club)

        // Creating an event associated with the club
        val event = Event(
            name = "Club Meeting",
            clubName = club.name, // Using the club's name
            organizerClub = "PLA", // Organizer club name
            location = "Room 101",
            startTime = LocalDateTime.now().plusHours(1),
            endTime = LocalDateTime.now().plusHours(2),
            club = club // Associating the event with the club
        )
        eventRepository.save(event)

        // Register the admin user for the event as well (optional)
        event.registeredUsers.add(adminUser)
        adminUser.registeredEvents.add(event)
        eventRepository.save(event)
    }
}
