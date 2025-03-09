package com.cs301.crm.services.impl;

import com.cs301.crm.dtos.requests.LoginRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.utils.JwtUtil;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_ValidCredentials_ReturnsToken() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
            "testUser",
            "password"
        );
        UserDetails userDetails = User.withUsername("testUser")
            .password("password")
            .authorities(Collections.emptyList())
            .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            Collections.emptyList()
        );

        when(authenticationManager.authenticate(any())).thenReturn(
            authentication
        );
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(
            "test-token"
        );

        // Act
        GenericResponseDTO response = authService.login(loginRequest);

        // Assert
        assertTrue(response.success());
        assertEquals("test-token", response.message());
        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO(
            "testUser",
            "wrongPassword"
        );
        when(authenticationManager.authenticate(any())).thenThrow(
            new BadCredentialsException("Invalid credentials")
        );

        // Act & Assert
        assertThrows(BadCredentialsException.class, () ->
            authService.login(loginRequest)
        );
    }
}
