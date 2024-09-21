package com.example.vitclubapp.service

import com.example.vitclubapp.exception.ResourceNotFoundException
import com.example.vitclubapp.model.Event
import com.example.vitclubapp.model.LoginRequest
import com.example.vitclubapp.model.User
import com.example.vitclubapp.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class UserService(private val userRepository: UserRepository) {
    fun login(loginRequest: LoginRequest): String {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw RuntimeException("User not found")

        // Assuming you have a method to validate the password
        if (!validatePassword(loginRequest.password, user.password)) {
            throw RuntimeException("Invalid password")
        }

        // Generate and return a token
        return generateToken(user)
    }

    // Placeholder for actual password validation
    private fun validatePassword(inputPassword: String, storedPassword: String): Boolean {
        // Implement your password validation logic (e.g., hash comparison)
        return inputPassword == storedPassword // This should be replaced with proper hashing
    }

    // Placeholder for token generation
    private fun generateToken(user: User): String {
        // Implement JWT or token generation logic here
        return "generatedToken"
    }
    fun deleteUser(id: UUID) {
        userRepository.deleteById(id)
    }



    fun registerUser(user: User): User {
        return userRepository.save(user)
    }

    fun getUserById(id: UUID): User {
        return userRepository.findById(id).orElseThrow { ResourceNotFoundException("User not found") }
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun getUserRegisteredEvents(id: UUID): Set<Event> {
        val user = userRepository.findById(id).orElseThrow { ResourceNotFoundException("User Not Found") }
        return user.registeredEvents
    }
}
