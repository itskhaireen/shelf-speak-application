package com.example.bookreviewapi.service;

import com.example.bookreviewapi.model.User;

public interface UserService {

    User registerUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    User getUserByIdOrThrow(Long id);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
}