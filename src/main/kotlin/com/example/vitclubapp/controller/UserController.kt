package com.example.vitclubapp.controller

import com.example.vitclubapp.model.Event
import com.example.vitclubapp.model.User
import com.example.vitclubapp.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun registerUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(userService.registerUser(user))
    }

    @GetMapping
    fun getAllUsers():ResponseEntity<List<User>>{
        return ResponseEntity.ok(userService.getAllUsers())

    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id:Long):ResponseEntity<User>{
        val user = userService.getUserById(id)
        return if (user!=null) ResponseEntity.ok(user) else ResponseEntity.notFound().build()
    }

    @GetMapping("/{id}/events")
    fun getUserRegisteredEvents(@PathVariable id:Long):ResponseEntity<Set<Event>>{
        val events = userService.getUserRegisteredEvents(id)
        return ResponseEntity.ok(events)
    }



}