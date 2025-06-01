package com.artist.demo.controller;

import com.artist.demo.dto.SkillDTO;
import com.artist.demo.exception.ResourceNotFoundException;
import com.artist.demo.exception.SkillNameAlreadyExistsException;
import com.artist.demo.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    public ResponseEntity<?> createSkill(@Valid @RequestBody SkillDTO skillDTO) {
        try {
            SkillDTO createdSkill = skillService.createSkill(skillDTO);
            return new ResponseEntity<>(createdSkill, HttpStatus.CREATED);
        } catch (SkillNameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }

    @GetMapping("/{skillId}")
    public ResponseEntity<SkillDTO> getSkillById(@PathVariable Long skillId) {
        SkillDTO skillDTO = skillService.getSkillById(skillId);
        return ResponseEntity.ok(skillDTO);
    }

    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAllSkills() {
        List<SkillDTO> skills = skillService.getAllSkills();
        return ResponseEntity.ok(skills);
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<?> updateSkill(@PathVariable Long skillId, @Valid @RequestBody SkillDTO skillDTO) {
        try {
            SkillDTO updatedSkill = skillService.updateSkill(skillId, skillDTO);
            return ResponseEntity.ok(updatedSkill);
        } catch (SkillNameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long skillId) {
        try {
            skillService.deleteSkill(skillId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }
}