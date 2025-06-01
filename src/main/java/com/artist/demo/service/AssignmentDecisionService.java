package com.artist.demo.service;

import com.artist.demo.dto.ArtistAssignmentProspectDTO;
import java.util.List;

public interface AssignmentDecisionService {
    List<ArtistAssignmentProspectDTO> findPotentialArtistsForRequest(Long requestId);
}