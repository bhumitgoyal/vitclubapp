package com.example.vitclubapp.repository

import com.example.vitclubapp.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository:JpaRepository<User,UUID> {
    fun findByEmail(email:String):User?
}