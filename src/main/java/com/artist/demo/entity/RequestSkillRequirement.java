package com.artist.demo.entity;

import com.artist.demo.enums.SkillLevel;
import com.artist.demo.enums.SkillPriority;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"serviceRequest", "skill"})
@Entity
@Table(name = "request_skill_requirement", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"service_request_id", "skill_id"})
})
public class RequestSkillRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id", nullable = false)
    private ServiceRequest serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillLevel requiredLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillPriority priority = SkillPriority.ESSENTIAL;
}