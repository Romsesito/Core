package com.artist.demo.service;

import com.artist.demo.dto.ServiceRequestCreateDTO;
import com.artist.demo.dto.ServiceRequestDTO;
import com.artist.demo.dto.ServiceRequestStatusUpdateDTO;
import com.artist.demo.enums.RequestStatus;

import java.util.List;

public interface ServiceRequestService {


    ServiceRequestDTO createServiceRequest(ServiceRequestCreateDTO createDTO);
    List<ServiceRequestDTO> getServiceRequestsByClientId(Long clientId);
    ServiceRequestDTO getServiceRequestByIdAndClientId(Long requestId, Long clientId);


    List<ServiceRequestDTO> getAllServiceRequestsFiltered(RequestStatus statusFilter);
    ServiceRequestDTO getServiceRequestByIdForOwner(Long requestId);
    ServiceRequestDTO approveServiceRequest(Long requestId);
    ServiceRequestDTO assignArtistToServiceRequest(Long requestId, Long artistId);
    ServiceRequestDTO updateServiceRequestStatusByOwner(Long requestId, ServiceRequestStatusUpdateDTO statusUpdateDTO);



    List<ServiceRequestDTO> getServiceRequestsByArtistId(Long artistId, RequestStatus statusFilter);
    ServiceRequestDTO updateServiceRequestStatusByArtist(Long requestId, Long artistId, ServiceRequestStatusUpdateDTO statusUpdateDTO);
}