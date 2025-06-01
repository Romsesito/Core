package com.artist.demo.dto;

import com.artist.demo.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestStatusUpdateDTO {
    @NotNull(message = "El nuevo estado no puede ser nulo")
    private RequestStatus newStatus;

}