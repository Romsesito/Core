package com.artist.demo.controller;

import com.artist.demo.dto.ServiceRequestCreateDTO;
import com.artist.demo.dto.ServiceRequestDTO;
import com.artist.demo.exception.ForbiddenAccessException;
import com.artist.demo.exception.ResourceNotFoundException;
import com.artist.demo.service.ClientRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    private final ClientRequestService clientRequestService;

    @Autowired
    public ServiceRequestController(ClientRequestService clientRequestService) {
        this.clientRequestService = clientRequestService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceRequest(@Valid @RequestBody ServiceRequestCreateDTO createDTO) {
        try {
            ServiceRequestDTO createdRequest = clientRequestService.createServiceRequest(createDTO);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el pedido de servicio: " + e.getMessage());
        }
    }

    @GetMapping("/my-requests")
    public ResponseEntity<?> getMyServiceRequests(@RequestParam Long clientId) {

        try {
            List<ServiceRequestDTO> requests = clientRequestService.getServiceRequestsByClientId(clientId);
            return ResponseEntity.ok(requests);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{requestId}/my-request")
    public ResponseEntity<?> getMySpecificServiceRequest(@PathVariable Long requestId, @RequestParam Long clientId) {
        try {
            ServiceRequestDTO request = clientRequestService.getServiceRequestByIdAndClientId(requestId, clientId);
            return ResponseEntity.ok(request);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

}