package com.artist.demo.dto;

import com.artist.demo.enums.RequestStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestDTO {
    private Long id;
    private UserDTO client;
    private String title;
    private String description;
    private LocalDateTime requestDate;
    private LocalDate desiredCompletionDate;
    private RequestStatus status;
    private UserDTO assignedArtist;
    private BigDecimal estimatedPrice;
    private BigDecimal finalPrice;
    private String ownerNotes;
    private String clientFeedback;
    private LocalDateTime lastUpdateTimestamp;
    private List<RequestSkillRequirementDTO> skillRequirements = new ArrayList<>();
}