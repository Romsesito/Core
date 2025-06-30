package com.artist.demo.service;

import com.artist.demo.dto.ServiceRequestDTO;
import com.artist.demo.dto.ServiceRequestStatusUpdateDTO;
import com.artist.demo.enums.RequestStatus;
import java.util.List;

public interface ArtistRequestService {
    List<ServiceRequestDTO> getServiceRequestsByArtistId(Long artistId, RequestStatus statusFilter);

    ServiceRequestDTO updateServiceRequestStatusByArtist(Long requestId, Long artistId,
            ServiceRequestStatusUpdateDTO statusUpdateDTO);
}
