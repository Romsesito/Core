package com.artist.demo.repository;


import com.artist.demo.entity.ArtistSkill;
import com.artist.demo.entity.User;
import com.artist.demo.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistSkillRepository extends JpaRepository<ArtistSkill, Long> {
    List<ArtistSkill> findByArtist(User artist);
    List<ArtistSkill> findByArtistId(Long artistId);
    Optional<ArtistSkill> findByArtistAndSkill(User artist, Skill skill);
    boolean existsByArtistAndSkill(User artist, Skill skill);
    void deleteByArtistAndSkill(User artist, Skill skill); // Podría ser útil
}