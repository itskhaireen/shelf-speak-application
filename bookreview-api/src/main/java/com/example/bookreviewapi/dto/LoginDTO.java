package com.example.bookreviewapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Data transfer object for user login")
public class LoginDTO {

    @Schema(description = "Username or email", example = "john_doe", required = true)
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @Schema(description = "Password", example = "password123", required = true)
    @NotBlank(message = "Password is required")
    private String password;
} 