package com.example.vitclubapp.model

data class LoginResponse(
    val token: String,
    val userId: Long,
    val role: String
)