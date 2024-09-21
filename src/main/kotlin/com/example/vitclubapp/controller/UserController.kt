package com.example.vitclubapp.controller

import com.example.vitclubapp.model.Event
import com.example.vitclubapp.model.LoginRequest
import com.example.vitclubapp.model.LoginResponse
import com.example.vitclubapp.model.User
import com.example.vitclubapp.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


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

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<String> {
        userService.deleteUser(id)
        return ResponseEntity.ok("User deleted successfully")
    }


    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        // Fetch the user and the token
        val (user, token) = userService.login(loginRequest)

        // Create and return the login response
        val loginResponse = LoginResponse(token, user.id, user.role.toString())
        return ResponseEntity.ok(loginResponse)
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