package com.skybrisk.bookstore.service;

import com.skybrisk.bookstore.dto.AuthenticationResponse;
import com.skybrisk.bookstore.dto.LoginRequest;
import com.skybrisk.bookstore.dto.RegisterRequest;
import com.skybrisk.bookstore.entity.Role;
import com.skybrisk.bookstore.entity.User;
import com.skybrisk.bookstore.repository.UserRepository;
import com.skybrisk.bookstore.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .role(Role.CUSTOMER)
                .build();

        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");

        AuthenticationResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAuthenticate_Success() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mock-jwt-token");

        AuthenticationResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testAuthenticate_UserNotFound() {
        LoginRequest request = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("password")
                .build();

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.authenticate(request));
    }
}
