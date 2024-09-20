package com.example.vitclubapp.service

import com.example.vitclubapp.model.Event
import com.example.vitclubapp.model.User
import com.example.vitclubapp.repository.UserRepository
import org.springframework.stereotype.Service


@Service
class UserService(private val userRepository: UserRepository) {

    fun registerUser(user: User):User{
      return  userRepository.save(user)
    }

    fun getUserById(id:Long):User?{
        return userRepository.findById(id).orElse(null)

    }

    fun getAllUsers():List<User>{
        return userRepository.findAll()
    }

    fun getUserRegisteredEvents(id:Long):Set<Event>{
        val user = userRepository.findById(id).orElseThrow {RuntimeException("User Not Found")}
        return user.registeredEvents
    }
}