package com.example.vitclubapp.repository

import com.example.vitclubapp.model.Club
import org.springframework.data.jpa.repository.JpaRepository

interface ClubRepository:JpaRepository<Club,Long>