package com.example.vitclubapp.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.Objects

@Entity
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long=0,
    val name:String,
    val clubName:String,
    val organizerClub:String,
    val location:String,
    val startTime:LocalDateTime,
    val endTime:LocalDateTime,

    @ManyToMany
    @JoinTable(
        name="event_users",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name="user_id")]
    )
    var registeredUsers:MutableSet<User> = mutableSetOf()
){
    override fun equals(other: Any?): Boolean {
        if(this===other) return true
        if(other !is Event)return false
        return id===other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}