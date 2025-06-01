package com.artist.demo.dto;

import com.artist.demo.enums.SkillLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSkillDTO {
    private Long id;

    @NotNull(message = "El ID de la habilidad (Skill) no puede ser nulo")
    private Long skillId;
    private String skillName;

    @NotNull(message = "El nivel de habilidad no puede ser nulo")
    private SkillLevel level;

    @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
    private Integer yearsExperience;
}