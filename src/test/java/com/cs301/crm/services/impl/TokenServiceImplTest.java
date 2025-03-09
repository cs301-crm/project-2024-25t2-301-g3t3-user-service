package com.cs301.crm.services.impl;

import com.cs301.crm.models.RefreshToken;
import com.cs301.crm.models.UserEntity;
import com.cs301.crm.repositories.TokenRepository;
import com.cs301.crm.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    void createRefreshToken_ValidUsername_Success() {
        // Arrange
        String username = "testUser";
        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(tokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Act
        String token = tokenService.createRefreshToken(username);

        // Assert
        assertNotNull(token);
        verify(userRepository).findByUsername(username);
        verify(tokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void validateRefreshToken_ValidToken_ReturnsRefreshToken() {
        // Arrange
        UUID tokenId = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenId);

        when(tokenRepository.findByTokenAndExpiresAtAfter(
                eq(tokenId), any(OffsetDateTime.class)))
                .thenReturn(Optional.of(refreshToken));

        // Act
        RefreshToken result = tokenService.validateRefreshToken(tokenId);

        // Assert
        assertNotNull(result);
        assertEquals(tokenId, result.getToken());
        verify(tokenRepository).findByTokenAndExpiresAtAfter(
                eq(tokenId), any(OffsetDateTime.class));
    }
}