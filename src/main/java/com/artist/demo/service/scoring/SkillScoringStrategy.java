package com.artist.demo.service.scoring;

import com.artist.demo.dto.ArtistAssignmentProspectDTO;
import com.artist.demo.dto.MatchedSkillDTO;
import com.artist.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class SkillScoringStrategy implements ScoringStrategy {
    @Override
    public double calculateScore(ArtistAssignmentProspectDTO prospect, User artist) {
        double score = 0;
        for (MatchedSkillDTO match : prospect.getSkillMatches()) {
            // ... Lógica de puntuación por habilidad ...
            // Ejemplo simplificado:
            if (match.isPerfectMatch()) {
                score += 10;
            } else if (match.isPartialMatch()) {
                score += 2;
            }
        }
        score -= prospect.getMissingDesirableSkills().size() * 2.0;
        return score;
    }
}
