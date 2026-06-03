package com.microchat.user_service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Hitta användare via användarnamn – används av Auth Service
    Optional<User> findByUsername(String username);
}