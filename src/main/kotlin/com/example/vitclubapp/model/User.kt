package com.example.vitclubapp.model


import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.Objects


@Entity
@Table(name = "app_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name:String,
    val registrationNumber:String,
    val email:String,
    val phoneNumber:String,
    val role:String,

    @ManyToMany(mappedBy = "registeredUsers", fetch = FetchType.LAZY)
    @JsonIgnore
    var registeredEvents: MutableSet<Event> = mutableSetOf()
){
    override fun equals(other: Any?):Boolean{
        if(this===other) return true
        if(other !is User) return false
        return id===other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}