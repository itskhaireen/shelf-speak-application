package com.example.bookreviewapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookreviewapi.model.User;
import com.example.bookreviewapi.model.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);    
    
    Optional<User> findByRole(UserRole role);
}