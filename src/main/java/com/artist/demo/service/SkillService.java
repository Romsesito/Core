package com.artist.demo.service;

import com.artist.demo.dto.SkillDTO;
import java.util.List;

public interface SkillService {
    SkillDTO createSkill(SkillDTO skillDTO);
    SkillDTO getSkillById(Long skillId);
    List<SkillDTO> getAllSkills();
    SkillDTO updateSkill(Long skillId, SkillDTO skillDTO);
    void deleteSkill(Long skillId);
}