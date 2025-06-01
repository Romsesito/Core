package com.artist.demo.controller;

import com.artist.demo.dto.ArtistProfileDTO;
import com.artist.demo.dto.ArtistSkillDTO;
import com.artist.demo.dto.ServiceRequestDTO;
import com.artist.demo.dto.ServiceRequestStatusUpdateDTO;
import com.artist.demo.dto.UserDTO;
import com.artist.demo.enums.RequestStatus;
import com.artist.demo.enums.Role;
import com.artist.demo.exception.ForbiddenAccessException;
import com.artist.demo.exception.ResourceNotFoundException;
import com.artist.demo.service.ArtistProfileService;
import com.artist.demo.service.ServiceRequestService;
import com.artist.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistProfileService artistProfileService;
    private final UserService userService;
    private final ServiceRequestService serviceRequestService;

    @Autowired
    public ArtistController(ArtistProfileService artistProfileService,
                            UserService userService,
                            ServiceRequestService serviceRequestService) {
        this.artistProfileService = artistProfileService;
        this.userService = userService;
        this.serviceRequestService = serviceRequestService;
    }


    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllArtists() {

        List<UserDTO> artists = userService.findUsersByRole(Role.ARTIST);
        return ResponseEntity.ok(artists);
    }


    @GetMapping("/{artistId}/profile")
    public ResponseEntity<ArtistProfileDTO> getArtistProfile(@PathVariable Long artistId) {

        ArtistProfileDTO profile = artistProfileService.getArtistProfileByUserId(artistId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{artistId}/profile")
    public ResponseEntity<ArtistProfileDTO> updateArtistProfile(
            @PathVariable Long artistId,
            @Valid @RequestBody ArtistProfileDTO artistProfileDTO) {

        ArtistProfileDTO updatedProfile = artistProfileService.createOrUpdateArtistProfile(artistId, artistProfileDTO); // Usando createOrUpdate
        return ResponseEntity.ok(updatedProfile);
    }


    @PostMapping("/{artistId}/skills")
    public ResponseEntity<ArtistSkillDTO> addSkillToArtist(
            @PathVariable Long artistId,
            @Valid @RequestBody ArtistSkillDTO artistSkillDTO) {

        ArtistSkillDTO createdArtistSkill = artistProfileService.addSkillToArtist(artistId, artistSkillDTO);
        return new ResponseEntity<>(createdArtistSkill, HttpStatus.CREATED);
    }

    @GetMapping("/{artistId}/skills")
    public ResponseEntity<List<ArtistSkillDTO>> getArtistSkills(@PathVariable Long artistId) {
        List<ArtistSkillDTO> skills = artistProfileService.getSkillsByArtistId(artistId);
        return ResponseEntity.ok(skills);
    }

    @PutMapping("/{artistId}/skills/{artistSkillId}")
    public ResponseEntity<ArtistSkillDTO> updateArtistSkill(
            @PathVariable Long artistId,
            @PathVariable Long artistSkillId,
            @Valid @RequestBody ArtistSkillDTO artistSkillDTO) {
        ArtistSkillDTO updatedArtistSkill = artistProfileService.updateArtistSkill(artistId, artistSkillId, artistSkillDTO);
        return ResponseEntity.ok(updatedArtistSkill);
    }

    @DeleteMapping("/{artistId}/skills/{artistSkillId}")
    public ResponseEntity<Void> removeSkillFromArtist(
            @PathVariable Long artistId,
            @PathVariable Long artistSkillId) {
        artistProfileService.removeSkillFromArtist(artistId, artistSkillId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{artistId}/assigned-requests")
    public ResponseEntity<?> getAssignedRequests(
            @PathVariable Long artistId,
            @RequestParam(required = false) RequestStatus status) {

        try {
            List<ServiceRequestDTO> requests = serviceRequestService.getServiceRequestsByArtistId(artistId, status);
            return ResponseEntity.ok(requests);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) { // Si el ID no es de un artista
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{artistId}/service-requests/{requestId}/status")
    public ResponseEntity<?> updateServiceRequestStatusByArtist(
            @PathVariable Long artistId,
            @PathVariable Long requestId,
            @Valid @RequestBody ServiceRequestStatusUpdateDTO statusUpdateDTO) {

        try {
            ServiceRequestDTO updatedRequest = serviceRequestService.updateServiceRequestStatusByArtist(requestId, artistId, statusUpdateDTO);
            return ResponseEntity.ok(updatedRequest);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}