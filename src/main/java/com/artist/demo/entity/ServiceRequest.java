package com.artist.demo.entity;


import com.artist.demo.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"client", "assignedArtist", "skillRequirements"})
@Entity
@Table(name = "service_request")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_user_id", nullable = false)
    private User client;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestDate;

    private LocalDate desiredCompletionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING_APPROVAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_artist_user_id")
    private User assignedArtist;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(columnDefinition = "TEXT")
    private String ownerNotes;

    @Column(columnDefinition = "TEXT")
    private String clientFeedback;

    @UpdateTimestamp
    private LocalDateTime lastUpdateTimestamp;

    @OneToMany(mappedBy = "serviceRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RequestSkillRequirement> skillRequirements = new HashSet<>();
}