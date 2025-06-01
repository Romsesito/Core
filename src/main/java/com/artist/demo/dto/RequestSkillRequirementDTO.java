package com.artist.demo.dto;

import com.artist.demo.enums.SkillLevel;
import com.artist.demo.enums.SkillPriority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestSkillRequirementDTO {
    private Long id;

    @NotNull(message = "El ID de la habilidad (Skill) no puede ser nulo")
    private Long skillId;
    private String skillName;

    @NotNull(message = "El nivel de habilidad requerido no puede ser nulo")
    private SkillLevel requiredLevel;

    @NotNull(message = "La prioridad de la habilidad no puede ser nula")
    private SkillPriority priority = SkillPriority.ESSENTIAL;
}