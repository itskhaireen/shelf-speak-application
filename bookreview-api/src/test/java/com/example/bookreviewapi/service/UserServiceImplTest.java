package com.example.bookreviewapi.service;

import com.example.bookreviewapi.model.User;
import com.example.bookreviewapi.model.UserRole;
import com.example.bookreviewapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_shouldSaveUserWithEncodedPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(UserRole.USER);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.registerUser(user);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(UserRole.USER, savedUser.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void findByUsername_shouldReturnUser() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        User found = userService.findByUsername("testuser");
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }

    @Test
    void findByEmail_shouldReturnUser() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        User found = userService.findByEmail("test@example.com");
        assertNotNull(found);
        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    void getUserByIdOrThrow_shouldReturnUser() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User found = userService.getUserByIdOrThrow(1L);
        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void existsByUsername_shouldReturnTrueIfExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertTrue(userService.existsByUsername("testuser"));
    }

    @Test
    void existsByEmail_shouldReturnTrueIfExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertTrue(userService.existsByEmail("test@example.com"));
    }
} 