package de.othregensburg.yapnet.service;

import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    private SimpleAuthService authService;
    private static final String JWT_SECRET = "test-jwt-secret-key-for-testing-purposes-only-32-chars";

    @BeforeEach
    void setUp() {
        authService = new SimpleAuthService(userRepository, JWT_SECRET);
    }

    @Test
    void testGenerateTokenWithValidUser() {
        User user = new User("testUser", "password", "test@example.com");
        String token = authService.generateToken(user);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT tokens have 3 parts separated by dots
    }

    @Test
    void testExtractUsernameWithValidToken() {
        User user = new User("testUser", "password", "test@example.com");
        String token = authService.generateToken(user);
        
        String extractedUsername = authService.extractUsername("Bearer " + token);
        assertEquals("testUser", extractedUsername);
    }

    @Test
    void testExtractUsernameWithInvalidToken() {
        String extractedUsername = authService.extractUsername("Bearer invalid.token.here");
        assertNull(extractedUsername);
    }

    @Test
    void testExtractUsernameWithNullToken() {
        String extractedUsername = authService.extractUsername(null);
        assertNull(extractedUsername);
    }

    @Test
    void testExtractUsernameWithoutBearerPrefix() {
        User user = new User("testUser", "password", "test@example.com");
        String token = authService.generateToken(user);
        
        String extractedUsername = authService.extractUsername(token);
        assertNull(extractedUsername);
    }

    @Test
    void testValidateTokenWithValidToken() {
        User user = new User("testUser", "password", "test@example.com");
        String token = authService.generateToken(user);
        
        boolean isValid = authService.validateToken("Bearer " + token);
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        boolean isValid = authService.validateToken("Bearer invalid.token.here");
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithNullToken() {
        boolean isValid = authService.validateToken(null);
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithoutBearerPrefix() {
        User user = new User("testUser", "password", "test@example.com");
        String token = authService.generateToken(user);
        
        boolean isValid = authService.validateToken(token);
        assertFalse(isValid);
    }

    @Test
    void testGetUserIdFromTokenWithValidToken() {
        User user = new User("testUser", "password", "test@example.com");
        user.setId(UUID.randomUUID());
        String token = authService.generateToken(user);
        
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        
        UUID userId = authService.getUserIdFromToken("Bearer " + token);
        assertEquals(user.getId(), userId);
    }

    @Test
    void testGetUserIdFromTokenWithUserNotFound() {
        User user = new User("testUser", "password", "test@example.com");
        String token = authService.generateToken(user);
        
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            authService.getUserIdFromToken("Bearer " + token);
        });
    }

    @Test
    void testTokenExpiration() {
        User user = new User("testUser", "password", "test@example.com");
        String token = authService.generateToken(user);
        
        // Token should be valid immediately after generation
        boolean isValid = authService.validateToken("Bearer " + token);
        assertTrue(isValid);
    }

    @Test
    void testGetCurrentUser() {
        User user = new User("testUser", "password", "test@example.com");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        
        User currentUser = authService.getCurrentUser();
        assertEquals("testUser", currentUser.getUsername());
    }

    @Test
    void testGetCurrentUserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> {
            authService.getCurrentUser();
        });
    }
} 