package com.example.bookreviewapi.mapper;

import com.example.bookreviewapi.dto.CreateUserDTO;
import com.example.bookreviewapi.dto.UserDTO;
import com.example.bookreviewapi.model.User;

public class UserMapper {

    // Map CreateUserDTO to User entity (for registration)
    public static User toEntity(CreateUserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Note: This will be hashed in the service layer
        user.setRole(com.example.bookreviewapi.model.UserRole.USER); // Default role
        return user;
    }

    // Map User entity to UserDTO (for responses)
    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
} 