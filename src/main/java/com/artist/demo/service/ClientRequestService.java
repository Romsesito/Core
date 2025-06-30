package com.artist.demo.service;

import com.artist.demo.dto.ServiceRequestCreateDTO;
import com.artist.demo.dto.ServiceRequestDTO;
import java.util.List;

public interface ClientRequestService {
    ServiceRequestDTO createServiceRequest(ServiceRequestCreateDTO createDTO);

    List<ServiceRequestDTO> getServiceRequestsByClientId(Long clientId);

    ServiceRequestDTO getServiceRequestByIdAndClientId(Long requestId, Long clientId);
}
