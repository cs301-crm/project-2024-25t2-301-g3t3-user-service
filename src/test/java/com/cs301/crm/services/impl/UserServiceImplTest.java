package com.cs301.crm.services.impl;

import com.cs301.crm.dtos.requests.CreateUserRequestDTO;
import com.cs301.crm.dtos.requests.ResetPasswordRequestDTO;
import com.cs301.crm.dtos.responses.GenericResponseDTO;
import com.cs301.crm.mappers.UserEntityMapper;
import com.cs301.crm.models.UserEntity;
import com.cs301.crm.models.UserRole;
import com.cs301.crm.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ValidRequest_Success() {
        // Arrange
        CreateUserRequestDTO request = new CreateUserRequestDTO(
                "John", "Doe", "johndoe", "john@example.com",
                "password", "AGENT"
        );

        UserEntity mappedEntity = new UserEntity();
        mappedEntity.setFirstName("John");
        mappedEntity.setLastName("Doe");
        mappedEntity.setUsername("johndoe");
        mappedEntity.setEmail("john@example.com");
        mappedEntity.setPassword("password");
        mappedEntity.setUserRole(UserRole.AGENT);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(mappedEntity);

        // Act
        GenericResponseDTO response = userService.createUser(request);

        // Assert
        assertTrue(response.success());
        assertEquals("User saved successfully", response.message());
        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void resetPassword_ValidRequest_Success() {
        // Arrange
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO(
                "johndoe", "oldPassword", "newPassword"
        );
        UserEntity user = new UserEntity();
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUsername("johndoe"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword"))
                .thenReturn(true);
        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedNewPassword");

        // Act
        GenericResponseDTO response = userService.resetPassword(request);

        // Assert
        assertTrue(response.success());
        assertEquals("User password updated successfully", response.message());
        verify(userRepository).findByUsername("johndoe");
        verify(passwordEncoder).matches("oldPassword", "encodedOldPassword");
        verify(passwordEncoder).encode("newPassword");
    }
}