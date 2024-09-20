package com.example.vitclubapp.repository

import com.example.vitclubapp.model.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository :JpaRepository<Event,Long>