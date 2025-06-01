package com.artist.demo.repository;

import com.artist.demo.entity.RequestSkillRequirement;
import com.artist.demo.entity.ServiceRequest;
import com.artist.demo.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestSkillRequirementRepository extends JpaRepository<RequestSkillRequirement, Long> {
    List<RequestSkillRequirement> findByServiceRequest(ServiceRequest serviceRequest);
    List<RequestSkillRequirement> findByServiceRequestId(Long serviceRequestId);
    Optional<RequestSkillRequirement> findByServiceRequestAndSkill(ServiceRequest serviceRequest, Skill skill);
}