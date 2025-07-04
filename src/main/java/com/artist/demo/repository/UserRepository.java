package com.artist.demo.repository;

import com.artist.demo.entity.User;
import com.artist.demo.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}