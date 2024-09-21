package com.example.vitclubapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*
import java.util.UUID

@Entity
@Table(name = "app_user")
data class User (
    @Id
    val id: UUID = UUID.randomUUID(),

    val name: String,

    val registrationNumber: String,

    val email: String,

    val phoneNumber: String,

    @Enumerated(EnumType.STRING)
    val role: UserRole,

    val password: String,  // Add this line for the password

    @ManyToMany(mappedBy = "registeredUsers", fetch = FetchType.LAZY)
    @JsonIgnore
    var registeredEvents: MutableSet<Event> = mutableSetOf(),

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    @JsonIgnore
    var clubs: MutableSet<Club> = mutableSetOf()
    )
    {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is User) return false
            return id == other.id
        }

        override fun hashCode(): Int {
            return Objects.hash(id)
        }
    }

