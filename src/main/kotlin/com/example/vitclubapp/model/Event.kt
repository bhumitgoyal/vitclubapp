package com.example.vitclubapp.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    val clubName: String,  // Optional if you want to keep the club's name

    val organizerClub: String,

    val location: String,

    val startTime: LocalDateTime,

    val endTime: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "club_id")
    var club: Club, // Correct relationship mapping

    @ManyToMany
    @JoinTable(
        name = "event_users",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var registeredUsers: MutableSet<User> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false
        return id == other.id // Using `==` for equality check
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
