package com.example.vitclubapp.controller

import com.example.vitclubapp.model.Club
import com.example.vitclubapp.service.ClubService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clubs")
class ClubController (private val clubService: ClubService){
    @GetMapping("/{id}")
    fun getClubById(@PathVariable id: Long): ResponseEntity<Club> {
        return ResponseEntity.ok(clubService.getClubById(id))
    }

    @PutMapping("/{id}")
    fun updateClub(@PathVariable id: Long, @RequestBody club: Club): ResponseEntity<Club> {
        return ResponseEntity.ok(clubService.updateClub(id, club))
    }
    @DeleteMapping("/{id}")
    fun deleteClub(@PathVariable id: Long): ResponseEntity<String> {
        clubService.deleteClub(id)
        return ResponseEntity.ok("Club deleted successfully")
    }


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