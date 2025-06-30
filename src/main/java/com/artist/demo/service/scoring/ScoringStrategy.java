package com.artist.demo.service.scoring;

import com.artist.demo.dto.ArtistAssignmentProspectDTO;
import com.artist.demo.entity.User;

public interface ScoringStrategy {
    double calculateScore(ArtistAssignmentProspectDTO prospect, User artist);
}
