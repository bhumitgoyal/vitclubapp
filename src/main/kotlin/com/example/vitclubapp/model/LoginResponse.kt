package com.example.vitclubapp.model
import java.util.UUID

data class LoginResponse(
    val token: String,
    val userId: UUID,
    val role: String
)