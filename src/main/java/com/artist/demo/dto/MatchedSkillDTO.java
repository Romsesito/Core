package com.artist.demo.dto;

import com.artist.demo.enums.SkillLevel;
import com.artist.demo.enums.SkillPriority;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchedSkillDTO {
    private Long skillId;
    private String skillName;
    private SkillLevel requiredLevel;
    private SkillPriority priority;
    private SkillLevel artistLevel;
    private boolean isPerfectMatch;
    private boolean isPartialMatch;

}