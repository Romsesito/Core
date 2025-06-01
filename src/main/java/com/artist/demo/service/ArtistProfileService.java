package com.artist.demo.service;

import com.artist.demo.dto.ArtistProfileDTO;
import com.artist.demo.dto.ArtistSkillDTO;

import java.util.List;

public interface ArtistProfileService {


    ArtistProfileDTO getArtistProfileByUserId(Long userId);
    ArtistProfileDTO createOrUpdateArtistProfile(Long userId, ArtistProfileDTO profileDTO); // Combina crear y actualizar


    ArtistSkillDTO addSkillToArtist(Long userId, ArtistSkillDTO artistSkillDTO);
    List<ArtistSkillDTO> getSkillsByArtistId(Long userId);
    ArtistSkillDTO updateArtistSkill(Long userId, Long artistSkillId, ArtistSkillDTO artistSkillDTO);
    void removeSkillFromArtist(Long userId, Long artistSkillId);
}