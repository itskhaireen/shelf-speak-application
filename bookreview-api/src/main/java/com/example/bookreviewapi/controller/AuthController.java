package com.example.bookreviewapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookreviewapi.dto.UserDTO;
import com.example.bookreviewapi.mapper.UserMapper;
import com.example.bookreviewapi.model.User;
import com.example.bookreviewapi.service.UserService;
import com.example.bookreviewapi.util.JwtUtil;

import jakarta.validation.Valid;

import com.example.bookreviewapi.dto.CreateUserDTO;
import com.example.bookreviewapi.dto.LoginDTO;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid CreateUserDTO createUserDTO) {
        User user = UserMapper.toEntity(createUserDTO);
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(UserMapper.toDTO(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDTO.getUsernameOrEmail(),
                loginDTO.getPassword()
            )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateToken(authentication);
        return ResponseEntity.ok(Map.of("token", token));
    }
}