package com.artist.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistProfileDTO {
    private Long userId;

    @Size(max = 2000, message = "La biografía no puede exceder los 2000 caracteres")
    private String bio;

    @Size(max = 255, message = "La URL del portfolio no puede exceder los 255 caracteres")

    private String portfolioUrl;

    @Size(max = 500, message = "Las notas de disponibilidad no pueden exceder los 500 caracteres")
    private String availabilityNotes;

    @Min(value = 0, message = "El máximo de proyectos concurrentes no puede ser negativo")
    @Max(value = 100, message = "El máximo de proyectos concurrentes es demasiado alto")
    private Integer maxConcurrentProjects;
}