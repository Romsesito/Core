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
import com.artist.demo.service.scoring.ScoringStrategy;
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
    private final List<ScoringStrategy> scoringStrategies;

    @Autowired
    public AssignmentDecisionServiceImpl(ServiceRequestRepository serviceRequestRepository,
            UserRepository userRepository,
            ArtistSkillRepository artistSkillRepository,
            ModelMapper modelMapper,
            List<ScoringStrategy> scoringStrategies) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.artistSkillRepository = artistSkillRepository;
        this.modelMapper = modelMapper;
        this.scoringStrategies = scoringStrategies;
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
            List<ServiceRequest> activeRequestsForArtist = serviceRequestRepository
                    .findByAssignedArtistAndStatusIn(artistEntity, activeStatuses);
            prospect.setCurrentActiveProjects(activeRequestsForArtist.size());

            if (missingEssential.isEmpty()) {
                double score = calculateMatchScore(prospect, artistEntity);
                prospect.setOverallMatchScore(score);
                prospects.add(prospect);
            }
        }
        prospects.sort((p1, p2) -> Double.compare(p2.getOverallMatchScore(), p1.getOverallMatchScore()));

        return prospects;
    }

    private double calculateMatchScore(ArtistAssignmentProspectDTO prospect, User artist) {
        if (!prospect.getMissingEssentialSkills().isEmpty()) {
            return -1; // O un valor que indique que no es un candidato viable
        }

        double totalScore = 0;
        for (ScoringStrategy strategy : scoringStrategies) {
            totalScore += strategy.calculateScore(prospect, artist);
        }
        return Math.max(0, totalScore);
    }

}