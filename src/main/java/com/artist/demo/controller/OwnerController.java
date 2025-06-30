package com.artist.demo.controller;

import com.artist.demo.dto.ArtistAssignmentProspectDTO;
import com.artist.demo.dto.ServiceRequestDTO;
import com.artist.demo.dto.ServiceRequestStatusUpdateDTO;
import com.artist.demo.enums.RequestStatus;
import com.artist.demo.service.AssignmentDecisionService;
import com.artist.demo.service.OwnerRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    private final OwnerRequestService ownerRequestService;
    private final AssignmentDecisionService assignmentDecisionService;

    @Autowired
    public OwnerController(OwnerRequestService ownerRequestService,
            AssignmentDecisionService assignmentDecisionService) {
        this.ownerRequestService = ownerRequestService;
        this.assignmentDecisionService = assignmentDecisionService;
    }

    @GetMapping("/service-requests")
    public ResponseEntity<List<ServiceRequestDTO>> getAllServiceRequests(
            @RequestParam(required = false) RequestStatus status) {
        List<ServiceRequestDTO> requests = ownerRequestService.getAllServiceRequestsFiltered(status);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/service-requests/{requestId}")
    public ResponseEntity<ServiceRequestDTO> getServiceRequestById(@PathVariable Long requestId) {
        ServiceRequestDTO request = ownerRequestService.getServiceRequestByIdForOwner(requestId);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/service-requests/{requestId}/approve")
    public ResponseEntity<?> approveServiceRequest(@PathVariable Long requestId) {
        try {
            ServiceRequestDTO updatedRequest = ownerRequestService.approveServiceRequest(requestId);
            return ResponseEntity.ok(updatedRequest);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/service-requests/{requestId}/assignment-prospects")
    public ResponseEntity<List<ArtistAssignmentProspectDTO>> getAssignmentProspects(@PathVariable Long requestId) {
        List<ArtistAssignmentProspectDTO> prospects = assignmentDecisionService
                .findPotentialArtistsForRequest(requestId);
        return ResponseEntity.ok(prospects);
    }

    @PutMapping("/service-requests/{requestId}/assign/{artistId}")
    public ResponseEntity<?> assignArtistToRequest(@PathVariable Long requestId, @PathVariable Long artistId) {
        try {
            ServiceRequestDTO updatedRequest = ownerRequestService.assignArtistToServiceRequest(requestId, artistId);
            return ResponseEntity.ok(updatedRequest);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("/service-requests/{requestId}/status")
    public ResponseEntity<ServiceRequestDTO> updateRequestStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody ServiceRequestStatusUpdateDTO statusUpdateDTO) {
        ServiceRequestDTO updatedRequest = ownerRequestService.updateServiceRequestStatusByOwner(requestId,
                statusUpdateDTO);
        return ResponseEntity.ok(updatedRequest);
    }
}