package com.example.vitclubapp.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,

    var clubName: String?,

    var organizerClub: String,

    var location: String,

    var startTime: LocalDateTime,

    var endTime: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    var club: Club,

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
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
