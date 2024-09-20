package com.example.vitclubapp.controller

import com.example.vitclubapp.model.Club
import com.example.vitclubapp.service.ClubService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/clubs")
class ClubController (private val clubService: ClubService){

    @PostMapping("/{clubId}/register")
    fun addMember(@PathVariable clubId:Long, @RequestParam userId:Long):ResponseEntity<String>{
        return try{
            clubService.addMember(userId,clubId)
            ResponseEntity.ok("Registered for club successfully")
        }
        catch (e:RuntimeException){
            ResponseEntity.badRequest().body(e.message)
        }
    }


    @GetMapping
    fun getAllClubs():List<Club>{
        return clubService.getAllClubs()
    }

    @PostMapping
    fun createClub(@RequestBody club: Club):Club{
        return clubService.createClub(club)
    }
}