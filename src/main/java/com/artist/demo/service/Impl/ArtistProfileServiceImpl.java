package com.artist.demo.service.Impl;

import com.artist.demo.dto.ArtistProfileDTO;
import com.artist.demo.dto.ArtistSkillDTO;
import com.artist.demo.entity.ArtistProfile;
import com.artist.demo.entity.ArtistSkill;
import com.artist.demo.entity.Skill;
import com.artist.demo.entity.User;
import com.artist.demo.enums.Role;
import com.artist.demo.exception.ForbiddenAccessException;
import com.artist.demo.exception.ResourceNotFoundException;
import com.artist.demo.repository.ArtistProfileRepository;
import com.artist.demo.repository.ArtistSkillRepository;
import com.artist.demo.repository.SkillRepository;
import com.artist.demo.repository.UserRepository;
import com.artist.demo.service.ArtistProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArtistProfileServiceImpl implements ArtistProfileService {

    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final SkillRepository skillRepository;
    private final ArtistSkillRepository artistSkillRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ArtistProfileServiceImpl(UserRepository userRepository,
                                    ArtistProfileRepository artistProfileRepository,
                                    SkillRepository skillRepository,
                                    ArtistSkillRepository artistSkillRepository,
                                    ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.skillRepository = skillRepository;
        this.artistSkillRepository = artistSkillRepository;
        this.modelMapper = modelMapper;
    }

    private User findArtistUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId.toString()));
        if (user.getRole() != Role.ARTIST) {
            throw new IllegalArgumentException("El usuario con id " + userId + " no es un artista.");
        }
        return user;
    }

    @Override
    public ArtistProfileDTO getArtistProfileByUserId(Long userId) {
        User artistUser = findArtistUserById(userId);
        ArtistProfile profile = artistProfileRepository.findById(artistUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Artista", "userId", userId.toString()));
        return modelMapper.map(profile, ArtistProfileDTO.class);
    }

    @Override
    public ArtistProfileDTO createOrUpdateArtistProfile(Long userId, ArtistProfileDTO profileDTO) {
        User artistUser = findArtistUserById(userId);

        ArtistProfile profile = artistProfileRepository.findById(artistUser.getId())
                .orElseGet(() -> {
                    ArtistProfile newProfile = new ArtistProfile();
                    newProfile.setUser(artistUser);
                    newProfile.setId(artistUser.getId());
                    return newProfile;
                });

        profile.setBio(profileDTO.getBio());
        profile.setPortfolioUrl(profileDTO.getPortfolioUrl());
        profile.setAvailabilityNotes(profileDTO.getAvailabilityNotes());
        if (profileDTO.getMaxConcurrentProjects() != null) {
            profile.setMaxConcurrentProjects(profileDTO.getMaxConcurrentProjects());
        }


        ArtistProfile savedProfile = artistProfileRepository.save(profile);
        ArtistProfileDTO resultDTO = modelMapper.map(savedProfile, ArtistProfileDTO.class);
        resultDTO.setUserId(savedProfile.getUser().getId());
        return resultDTO;
    }

    @Override
    public ArtistSkillDTO addSkillToArtist(Long userId, ArtistSkillDTO artistSkillDTO) {
        User artistUser = findArtistUserById(userId);
        Skill skill = skillRepository.findById(artistSkillDTO.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Habilidad (Skill)", "id", artistSkillDTO.getSkillId().toString()));

        if (artistSkillRepository.existsByArtistAndSkill(artistUser, skill)) {
            throw new IllegalArgumentException("El artista ya posee esta habilidad.");
        }

        ArtistSkill artistSkill = new ArtistSkill();
        artistSkill.setArtist(artistUser);
        artistSkill.setSkill(skill);
        artistSkill.setLevel(artistSkillDTO.getLevel());
        artistSkill.setYearsExperience(artistSkillDTO.getYearsExperience() != null ? artistSkillDTO.getYearsExperience() : 0);

        ArtistSkill savedArtistSkill = artistSkillRepository.save(artistSkill);

        ArtistSkillDTO resultDTO = modelMapper.map(savedArtistSkill, ArtistSkillDTO.class);
        resultDTO.setSkillId(savedArtistSkill.getSkill().getId());
        resultDTO.setSkillName(savedArtistSkill.getSkill().getName());
        return resultDTO;
    }

    @Override
    public List<ArtistSkillDTO> getSkillsByArtistId(Long userId) {
        User artistUser = findArtistUserById(userId);
        List<ArtistSkill> artistSkills = artistSkillRepository.findByArtistId(artistUser.getId());
        return artistSkills.stream()
                .map(as -> {
                    ArtistSkillDTO dto = modelMapper.map(as, ArtistSkillDTO.class);
                    dto.setSkillId(as.getSkill().getId());
                    dto.setSkillName(as.getSkill().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ArtistSkillDTO updateArtistSkill(Long userId, Long artistSkillId, ArtistSkillDTO artistSkillDTO) {
        User artistUser = findArtistUserById(userId);
        ArtistSkill existingArtistSkill = artistSkillRepository.findById(artistSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Habilidad de Artista (ArtistSkill)", "id", artistSkillId.toString()));

        if (!existingArtistSkill.getArtist().getId().equals(artistUser.getId())) {
            throw new ForbiddenAccessException("No tienes permiso para modificar esta habilidad de artista.");
        }

        if (artistSkillDTO.getSkillId() != null && !artistSkillDTO.getSkillId().equals(existingArtistSkill.getSkill().getId())) {
            throw new IllegalArgumentException("No se puede cambiar la habilidad base de una entrada existente. Elimine y añada una nueva si es necesario.");
        }


        existingArtistSkill.setLevel(artistSkillDTO.getLevel());
        existingArtistSkill.setYearsExperience(artistSkillDTO.getYearsExperience() != null ? artistSkillDTO.getYearsExperience() : existingArtistSkill.getYearsExperience());

        ArtistSkill updatedArtistSkill = artistSkillRepository.save(existingArtistSkill);

        ArtistSkillDTO resultDTO = modelMapper.map(updatedArtistSkill, ArtistSkillDTO.class);
        resultDTO.setSkillId(updatedArtistSkill.getSkill().getId());
        resultDTO.setSkillName(updatedArtistSkill.getSkill().getName());
        return resultDTO;
    }

    @Override
    public void removeSkillFromArtist(Long userId, Long artistSkillId) {
        User artistUser = findArtistUserById(userId);
        ArtistSkill artistSkill = artistSkillRepository.findById(artistSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Habilidad de Artista (ArtistSkill)", "id", artistSkillId.toString()));

        if (!artistSkill.getArtist().getId().equals(artistUser.getId())) {
            throw new ForbiddenAccessException("No tienes permiso para eliminar esta habilidad de artista.");
        }
        artistSkillRepository.delete(artistSkill);
    }
}