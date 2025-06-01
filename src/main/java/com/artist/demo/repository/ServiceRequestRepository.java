package com.artist.demo.repository;


import com.artist.demo.entity.ServiceRequest;
import com.artist.demo.entity.User;
import com.artist.demo.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByClient(User client);
    List<ServiceRequest> findByClientId(Long clientId);
    List<ServiceRequest> findByAssignedArtist(User assignedArtist);
    List<ServiceRequest> findByAssignedArtistId(Long artistId);
    List<ServiceRequest> findByStatus(RequestStatus status);
    List<ServiceRequest> findByAssignedArtistAndStatusIn(User artist, Collection<RequestStatus> statuses);
    List<ServiceRequest> findByStatusIn(Collection<RequestStatus> statuses);
}