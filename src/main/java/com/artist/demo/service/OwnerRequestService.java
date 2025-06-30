package com.artist.demo.service;

import com.artist.demo.dto.ServiceRequestDTO;
import com.artist.demo.dto.ServiceRequestStatusUpdateDTO;
import com.artist.demo.enums.RequestStatus;
import java.util.List;

public interface OwnerRequestService {
    List<ServiceRequestDTO> getAllServiceRequestsFiltered(RequestStatus statusFilter);

    ServiceRequestDTO getServiceRequestByIdForOwner(Long requestId);

    ServiceRequestDTO approveServiceRequest(Long requestId);

    ServiceRequestDTO assignArtistToServiceRequest(Long requestId, Long artistId);

    ServiceRequestDTO updateServiceRequestStatusByOwner(Long requestId, ServiceRequestStatusUpdateDTO statusUpdateDTO);
}
