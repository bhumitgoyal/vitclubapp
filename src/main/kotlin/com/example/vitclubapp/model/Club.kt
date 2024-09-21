package com.example.vitclubapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
data class Club(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    @ManyToMany
    @JoinTable(
        name = "club_members",
        joinColumns = [JoinColumn(name = "club_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var members: MutableSet<User> = mutableSetOf(),

    val description: String,

    val cluborchap: String,

    @OneToMany(mappedBy = "club", cascade = [CascadeType.ALL])
    @JsonIgnore
    var events: MutableSet<Event> = mutableSetOf(),

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    var admin: User
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Club) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
