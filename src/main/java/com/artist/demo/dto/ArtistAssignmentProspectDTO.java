package com.artist.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistAssignmentProspectDTO {
    private UserDTO artistInfo;

    private List<MatchedSkillDTO> skillMatches = new ArrayList<>();
    private List<RequestSkillRequirementDTO> missingEssentialSkills = new ArrayList<>();
    private List<RequestSkillRequirementDTO> missingDesirableSkills = new ArrayList<>();
    private int currentActiveProjects;
    private Double overallMatchScore;
}