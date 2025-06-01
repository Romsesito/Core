package com.artist.demo.service.Impl;

import com.artist.demo.dto.ArtistAssignmentProspectDTO;
import com.artist.demo.dto.MatchedSkillDTO;
import com.artist.demo.dto.RequestSkillRequirementDTO;
import com.artist.demo.dto.UserDTO;
import com.artist.demo.entity.*;
import com.artist.demo.enums.RequestStatus;
import com.artist.demo.enums.Role;
import com.artist.demo.exception.ResourceNotFoundException;
import com.artist.demo.repository.ArtistSkillRepository;
import com.artist.demo.repository.ServiceRequestRepository;
import com.artist.demo.repository.UserRepository;
import com.artist.demo.service.AssignmentDecisionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AssignmentDecisionServiceImpl implements AssignmentDecisionService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final ArtistSkillRepository artistSkillRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AssignmentDecisionServiceImpl(ServiceRequestRepository serviceRequestRepository,
                                         UserRepository userRepository,
                                         ArtistSkillRepository artistSkillRepository,
                                         ModelMapper modelMapper) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.artistSkillRepository = artistSkillRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ArtistAssignmentProspectDTO> findPotentialArtistsForRequest(Long requestId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de Servicio", "id", requestId.toString()));

        if (request.getStatus() != RequestStatus.PENDING_ASSIGNMENT) {
            return List.of();
        }

        Set<RequestSkillRequirement> requiredSkills = request.getSkillRequirements();
        List<User> allArtists = userRepository.findAllByRole(Role.ARTIST);
        List<ArtistAssignmentProspectDTO> prospects = new ArrayList<>();

        for (User artistEntity : allArtists) {
            UserDTO artistInfo = modelMapper.map(artistEntity, UserDTO.class);
            ArtistAssignmentProspectDTO prospect = new ArtistAssignmentProspectDTO();
            prospect.setArtistInfo(artistInfo);


            List<ArtistSkill> artistActualSkills = artistSkillRepository.findByArtistId(artistEntity.getId());
            Map<Long, ArtistSkill> artistSkillMap = artistActualSkills.stream()
                    .collect(Collectors.toMap(as -> as.getSkill().getId(), as -> as));

            List<MatchedSkillDTO> skillMatches = new ArrayList<>();
            List<RequestSkillRequirementDTO> missingEssential = new ArrayList<>();
            List<RequestSkillRequirementDTO> missingDesirable = new ArrayList<>();
            int essentialSkillsMet = 0;

            for (RequestSkillRequirement req : requiredSkills) {
                ArtistSkill artistSkill = artistSkillMap.get(req.getSkill().getId());
                MatchedSkillDTO match = new MatchedSkillDTO();
                match.setSkillId(req.getSkill().getId());
                match.setSkillName(req.getSkill().getName());
                match.setRequiredLevel(req.getRequiredLevel());
                match.setPriority(req.getPriority());

                if (artistSkill != null) {
                    match.setArtistLevel(artistSkill.getLevel());
                    boolean meetsLevel = artistSkill.getLevel().ordinal() >= req.getRequiredLevel().ordinal();
                    match.setPerfectMatch(meetsLevel);
                    match.setPartialMatch(!meetsLevel);
                    if (meetsLevel && req.getPriority() == com.artist.demo.enums.SkillPriority.ESSENTIAL) {
                        essentialSkillsMet++;
                    }
                } else {
                    match.setArtistLevel(null);
                    match.setPerfectMatch(false);
                    match.setPartialMatch(false);
                    if (req.getPriority() == com.artist.demo.enums.SkillPriority.ESSENTIAL) {
                        missingEssential.add(modelMapper.map(req, RequestSkillRequirementDTO.class));
                    } else {
                        missingDesirable.add(modelMapper.map(req, RequestSkillRequirementDTO.class));
                    }
                }
                skillMatches.add(match);
            }
            prospect.setSkillMatches(skillMatches);
            prospect.setMissingEssentialSkills(missingEssential);
            prospect.setMissingDesirableSkills(missingDesirable);

            List<RequestStatus> activeStatuses = List.of(RequestStatus.ASSIGNED, RequestStatus.IN_PROGRESS);
            List<ServiceRequest> activeRequestsForArtist = serviceRequestRepository.findByAssignedArtistAndStatusIn(artistEntity, activeStatuses);
            prospect.setCurrentActiveProjects(activeRequestsForArtist.size());

            if (missingEssential.isEmpty()) {
                double score = calculateMatchScore(skillMatches, missingDesirable.size(), prospect.getCurrentActiveProjects(), artistEntity.getArtistProfile() != null ? artistEntity.getArtistProfile().getMaxConcurrentProjects() : 3);
                prospect.setOverallMatchScore(score);
                prospects.add(prospect);
            }
        }
        prospects.sort((p1, p2) -> Double.compare(p2.getOverallMatchScore(), p1.getOverallMatchScore()));

        return prospects;
    }

    private double calculateMatchScore(List<MatchedSkillDTO> skillMatches, int missingDesirableCount, int currentLoad, int maxLoad) {
        double score = 0;
        int essentialMet = 0;
        int totalEssential = 0;
        int desirableMet = 0;
        int totalDesirable = 0;

        for (MatchedSkillDTO match : skillMatches) {
            if (match.getPriority() == com.artist.demo.enums.SkillPriority.ESSENTIAL) {
                totalEssential++;
                if (match.isPerfectMatch()) {
                    essentialMet++;
                    score += 10;
                    score += (match.getArtistLevel().ordinal() - match.getRequiredLevel().ordinal()) * 2;
                } else if (match.isPartialMatch()) {
                    score += 2;
                }
            } else if (match.getPriority() == com.artist.demo.enums.SkillPriority.DESIRABLE) {
                totalDesirable++;
                if (match.isPerfectMatch()) {
                    desirableMet++;
                    score += 5;
                    score += (match.getArtistLevel().ordinal() - match.getRequiredLevel().ordinal());
                } else if (match.isPartialMatch()) {
                    score += 1;
                }
            }
        }

        if (totalEssential > 0 && essentialMet < totalEssential) {
            return -1000;
        }

        score -= missingDesirableCount * 2;

        if (currentLoad >= maxLoad) {
            score -= 10;
        } else if (currentLoad > 0) {
            score -= (double) currentLoad / maxLoad * 5;
        }
        return Math.max(0, score);
    }
}