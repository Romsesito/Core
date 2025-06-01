package com.artist.demo.service.Impl;

import com.artist.demo.dto.SkillDTO;
import com.artist.demo.entity.Skill;
import com.artist.demo.exception.ResourceNotFoundException;
import com.artist.demo.exception.SkillNameAlreadyExistsException;
import com.artist.demo.repository.SkillRepository;
import com.artist.demo.service.SkillService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SkillServiceImpl(SkillRepository skillRepository, ModelMapper modelMapper) {
        this.skillRepository = skillRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public SkillDTO createSkill(SkillDTO skillDTO) {
        if (skillRepository.existsByName(skillDTO.getName())) {
            throw new SkillNameAlreadyExistsException("La habilidad con el nombre '" + skillDTO.getName() + "' ya existe.");
        }
        Skill skill = modelMapper.map(skillDTO, Skill.class);
        Skill savedSkill = skillRepository.save(skill);
        return modelMapper.map(savedSkill, SkillDTO.class);
    }

    @Override
    public SkillDTO getSkillById(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Habilidad", "id", skillId.toString()));
        return modelMapper.map(skill, SkillDTO.class);
    }

    @Override
    public List<SkillDTO> getAllSkills() {
        List<Skill> skills = skillRepository.findAll();
        return skills.stream()
                .map(skill -> modelMapper.map(skill, SkillDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public SkillDTO updateSkill(Long skillId, SkillDTO skillDTO) {
        Skill existingSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Habilidad", "id", skillId.toString()));

        // Verifica si el nuevo nombre ya existe en OTRA habilidad diferente a la actual
        skillRepository.findByName(skillDTO.getName()).ifPresent(skillWithSameName -> {
            if (!skillWithSameName.getId().equals(skillId)) {
                throw new SkillNameAlreadyExistsException("Otra habilidad con el nombre '" + skillDTO.getName() + "' ya existe.");
            }
        });

        existingSkill.setName(skillDTO.getName());
        existingSkill.setDescription(skillDTO.getDescription());
        Skill updatedSkill = skillRepository.save(existingSkill);
        return modelMapper.map(updatedSkill, SkillDTO.class);
    }

    @Override
    public void deleteSkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new ResourceNotFoundException("Habilidad", "id", skillId.toString());
        }

        skillRepository.deleteById(skillId);
    }
}