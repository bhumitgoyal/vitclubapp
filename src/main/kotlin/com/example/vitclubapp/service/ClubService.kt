package com.example.vitclubapp.service

import com.example.vitclubapp.exception.ResourceNotFoundException
import com.example.vitclubapp.model.Club
import com.example.vitclubapp.model.User
import com.example.vitclubapp.repository.ClubRepository
import com.example.vitclubapp.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ClubService(private val clubRepository: ClubRepository, private val userRepository: UserRepository) {

    fun createClub(club: Club): Club {
        return clubRepository.save(club)
    }

    fun getAllClubs(): List<Club> {
        return clubRepository.findAll()
    }

    fun addMember(userId: Long, clubId: Long) {
        val club = clubRepository.findById(clubId).orElseThrow { ResourceNotFoundException("Club not found") }
        val user = userRepository.findById(userId).orElseThrow { ResourceNotFoundException("User not found") }

        if (club.members.contains(user)) {
            throw RuntimeException("User is already registered for this club.")
        }

        club.members.add(user)
        clubRepository.save(club)
    }
}
