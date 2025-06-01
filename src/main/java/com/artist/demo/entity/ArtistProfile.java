package com.artist.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "user")
@Entity
@Table(name = "artist_profile")
public class ArtistProfile {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String portfolioUrl;

    private String availabilityNotes;

    @Column(nullable = false)
    private Integer maxConcurrentProjects = 3;


    @Version
    private Long version; // o Integer, o Timestamp
}