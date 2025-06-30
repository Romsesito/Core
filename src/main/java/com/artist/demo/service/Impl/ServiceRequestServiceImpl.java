package com.artist.demo.service.Impl;

import com.artist.demo.dto.RequestSkillRequirementDTO;
import com.artist.demo.dto.ServiceRequestCreateDTO;
import com.artist.demo.dto.ServiceRequestDTO;
import com.artist.demo.entity.RequestSkillRequirement;
import com.artist.demo.entity.ServiceRequest;
import com.artist.demo.entity.Skill;
import com.artist.demo.entity.User;
import com.artist.demo.enums.RequestStatus;
import com.artist.demo.exception.ForbiddenAccessException;
import com.artist.demo.exception.ResourceNotFoundException;
import com.artist.demo.repository.RequestSkillRequirementRepository;
import com.artist.demo.repository.ServiceRequestRepository;
import com.artist.demo.repository.SkillRepository;
import com.artist.demo.repository.UserRepository;
import com.artist.demo.service.ArtistRequestService;
import com.artist.demo.service.ClientRequestService;
import com.artist.demo.service.OwnerRequestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.artist.demo.dto.ServiceRequestStatusUpdateDTO;
import com.artist.demo.enums.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceRequestServiceImpl implements ClientRequestService, OwnerRequestService, ArtistRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RequestSkillRequirementRepository requestSkillRequirementRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ServiceRequestServiceImpl(ServiceRequestRepository serviceRequestRepository,
            UserRepository userRepository,
            SkillRepository skillRepository,
            RequestSkillRequirementRepository requestSkillRequirementRepository,
            ModelMapper modelMapper) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.requestSkillRequirementRepository = requestSkillRequirementRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ServiceRequestDTO createServiceRequest(ServiceRequestCreateDTO createDTO) {
        User client = userRepository.findById(createDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", createDTO.getClientId().toString()));

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setClient(client);
        serviceRequest.setTitle(createDTO.getTitle());
        serviceRequest.setDescription(createDTO.getDescription());
        serviceRequest.setDesiredCompletionDate(createDTO.getDesiredCompletionDate());
        serviceRequest.setStatus(RequestStatus.PENDING_APPROVAL);

        ServiceRequest savedRequest = serviceRequestRepository.save(serviceRequest);

        if (createDTO.getSkillRequirements() != null && !createDTO.getSkillRequirements().isEmpty()) {
            Set<RequestSkillRequirement> requirements = new HashSet<>();
            for (RequestSkillRequirementDTO reqDto : createDTO.getSkillRequirements()) {
                Skill skill = skillRepository.findById(reqDto.getSkillId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Habilidad", "id", reqDto.getSkillId().toString()));

                RequestSkillRequirement requirement = new RequestSkillRequirement();
                requirement.setServiceRequest(savedRequest);
                requirement.setSkill(skill);
                requirement.setRequiredLevel(reqDto.getRequiredLevel());
                requirement.setPriority(reqDto.getPriority());
                requirements.add(requestSkillRequirementRepository.save(requirement));
            }
            savedRequest.setSkillRequirements(requirements);

        }

        return modelMapper.map(savedRequest, ServiceRequestDTO.class);
    }

    @Override
    public List<ServiceRequestDTO> getServiceRequestsByClientId(Long clientId) {
        if (!userRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Cliente", "id", clientId.toString());
        }
        List<ServiceRequest> requests = serviceRequestRepository.findByClientId(clientId);
        return requests.stream()
                .map(request -> modelMapper.map(request, ServiceRequestDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceRequestDTO getServiceRequestByIdAndClientId(Long requestId, Long clientId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de Servicio", "id", requestId.toString()));

        if (!request.getClient().getId().equals(clientId)) {
            // Podrías lanzar una excepción diferente si el pedido existe pero no pertenece
            // al cliente
            throw new ForbiddenAccessException("No tienes permiso para ver este pedido de servicio.");
        }
        return modelMapper.map(request, ServiceRequestDTO.class);
    }

    @Override
    public List<ServiceRequestDTO> getAllServiceRequestsFiltered(RequestStatus statusFilter) {
        List<ServiceRequest> requests;
        if (statusFilter != null) {
            requests = serviceRequestRepository.findByStatus(statusFilter);
        } else {
            requests = serviceRequestRepository.findAll(); // Considera paginación para grandes volúmenes
        }
        return requests.stream()
                .map(request -> modelMapper.map(request, ServiceRequestDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceRequestDTO getServiceRequestByIdForOwner(Long requestId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de Servicio", "id", requestId.toString()));
        return modelMapper.map(request, ServiceRequestDTO.class);
    }

    @Override
    public ServiceRequestDTO approveServiceRequest(Long requestId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de Servicio", "id", requestId.toString()));

        if (request.getStatus() != RequestStatus.PENDING_APPROVAL) {
            throw new IllegalStateException(
                    "Solo se pueden aprobar pedidos que están pendientes de aprobación. Estado actual: "
                            + request.getStatus());
        }
        request.setStatus(RequestStatus.PENDING_ASSIGNMENT);
        ServiceRequest updatedRequest = serviceRequestRepository.save(request);
        return modelMapper.map(updatedRequest, ServiceRequestDTO.class);
    }

    @Override
    public ServiceRequestDTO assignArtistToServiceRequest(Long requestId, Long artistId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de Servicio", "id", requestId.toString()));
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", "id", artistId.toString()));

        if (artist.getRole() != Role.ARTIST) {
            throw new IllegalArgumentException("El usuario seleccionado no es un artista.");
        }

        if (request.getStatus() != RequestStatus.PENDING_ASSIGNMENT) {
            throw new IllegalStateException(
                    "Solo se pueden asignar artistas a pedidos que están pendientes de asignación. Estado actual: "
                            + request.getStatus());
        }

        request.setAssignedArtist(artist);
        request.setStatus(RequestStatus.ASSIGNED);
        ServiceRequest updatedRequest = serviceRequestRepository.save(request);
        return modelMapper.map(updatedRequest, ServiceRequestDTO.class);
    }

    @Override
    public ServiceRequestDTO updateServiceRequestStatusByOwner(Long requestId,
            ServiceRequestStatusUpdateDTO statusUpdateDTO) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de Servicio", "id", requestId.toString()));

        request.setStatus(statusUpdateDTO.getNewStatus());
        ServiceRequest updatedRequest = serviceRequestRepository.save(request);
        return modelMapper.map(updatedRequest, ServiceRequestDTO.class);
    }

    @Override
    public List<ServiceRequestDTO> getServiceRequestsByArtistId(Long artistId, RequestStatus statusFilter) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", "id", artistId.toString()));
        if (artist.getRole() != Role.ARTIST) {
            throw new IllegalArgumentException("El usuario especificado no es un artista.");
        }

        List<ServiceRequest> requests;
        List<RequestStatus> statusesToFilter = statusFilter != null ? List.of(statusFilter)
                : List.of(RequestStatus.ASSIGNED, RequestStatus.IN_PROGRESS, RequestStatus.WAITING_CLIENT_REVIEW);

        if (statusFilter != null) {
            requests = serviceRequestRepository.findByAssignedArtistAndStatusIn(artist, List.of(statusFilter));
        } else {

            requests = serviceRequestRepository.findByAssignedArtistAndStatusIn(artist,
                    List.of(RequestStatus.ASSIGNED, RequestStatus.IN_PROGRESS, RequestStatus.WAITING_CLIENT_REVIEW));
        }

        return requests.stream()
                .map(request -> modelMapper.map(request, ServiceRequestDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceRequestDTO updateServiceRequestStatusByArtist(Long requestId, Long artistId,
            ServiceRequestStatusUpdateDTO statusUpdateDTO) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de Servicio", "id", requestId.toString()));

        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artista", "id", artistId.toString()));

        if (request.getAssignedArtist() == null || !request.getAssignedArtist().getId().equals(artist.getId())) {
            throw new ForbiddenAccessException("No tienes permiso para actualizar este pedido de servicio.");
        }

        // Lógica de validación de transición de estado
        RequestStatus currentStatus = request.getStatus();
        RequestStatus newStatus = statusUpdateDTO.getNewStatus();

        boolean isValidTransition = switch (currentStatus) {
            case ASSIGNED -> newStatus == RequestStatus.IN_PROGRESS;
            case IN_PROGRESS -> newStatus == RequestStatus.WAITING_CLIENT_REVIEW;
            default -> false;
        };

        if (!isValidTransition) {
            throw new IllegalStateException(
                    "La transición del estado '" + currentStatus + "' a '" + newStatus + "' no está permitida.");
        }

        request.setStatus(newStatus);
        ServiceRequest updatedRequest = serviceRequestRepository.save(request);
        return modelMapper.map(updatedRequest, ServiceRequestDTO.class);
    }
}
// Implementaciones para Artista vendrán después...

// Implementaciones para Owner y Artist vendrán después...
