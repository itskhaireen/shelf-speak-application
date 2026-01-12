package com.example.bookreviewapi.dto;

import com.example.bookreviewapi.model.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
} 