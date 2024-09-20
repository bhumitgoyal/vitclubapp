package com.example.vitclubapp.repository

import com.example.vitclubapp.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository:JpaRepository<User,Long> {
    fun findByEmail(email:String):User?
}