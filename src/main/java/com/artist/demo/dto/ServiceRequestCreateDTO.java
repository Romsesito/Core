package com.artist.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestCreateDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clientId;

    @NotBlank(message = "El título del pedido no puede estar vacío")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String title;

    @NotBlank(message = "La descripción del pedido no puede estar vacía")
    @Size(min = 10, max = 5000, message = "La descripción debe tener entre 10 y 5000 caracteres")
    private String description;

    private LocalDate desiredCompletionDate;

    @Valid
    private List<RequestSkillRequirementDTO> skillRequirements = new ArrayList<>();
}