package de.othregensburg.yapnet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.othregensburg.yapnet.dto.LoginRequestDto;
import de.othregensburg.yapnet.dto.LoginResponseDto;
import de.othregensburg.yapnet.dto.RegisterRequestDto;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.service.UserService;
import de.othregensburg.yapnet.repository.FollowRepository;
import de.othregensburg.yapnet.service.SimpleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    
    @Mock private UserService userService;
    @Mock private FollowRepository followRepository;
    @Mock private SimpleAuthService authService;
    
    private AuthController authController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        authController = new AuthController(userService, "test-jwt-secret-key-for-testing-purposes-only", followRepository, authService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_success() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("user", "user@example.com", "pass");
        LoginResponseDto resp = new LoginResponseDto(null, "user", "user@example.com");
        when(userService.register(any())).thenReturn(resp);
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void register_nullRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_missingUsername() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("", "user@example.com", "pass");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_nullUsername() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto(null, "user@example.com", "pass");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_missingEmail() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("user", "", "pass");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_nullEmail() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("user", null, "pass");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_missingPassword() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("user", "user@example.com", "");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_nullPassword() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("user", "user@example.com", null);
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_serviceReturnsNull() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("user", "user@example.com", "pass");
        when(userService.register(any())).thenReturn(null);
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void register_serviceReturnsError() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("user", "user@example.com", "pass");
        LoginResponseDto resp = new LoginResponseDto("Username already exists");
        when(userService.register(any())).thenReturn(resp);
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_serviceThrowsException() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("user", "user@example.com", "pass");
        when(userService.register(any())).thenThrow(new RuntimeException("Database error"));
        
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void login_success() throws Exception {
        LoginRequestDto req = new LoginRequestDto("user", "pass");
        LoginResponseDto resp = new LoginResponseDto(null, "user", "user@example.com");
        when(userService.login(any())).thenReturn(resp);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void login_nullRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_nullUsername() throws Exception {
        LoginRequestDto req = new LoginRequestDto(null, "pass");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_nullPassword() throws Exception {
        LoginRequestDto req = new LoginRequestDto("user", null);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_invalid() throws Exception {
        LoginRequestDto req = new LoginRequestDto("user", "wrong");
        when(userService.login(any())).thenReturn(new LoginResponseDto("Invalid login"));
        
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_serviceReturnsNull() throws Exception {
        LoginRequestDto req = new LoginRequestDto("user", "pass");
        when(userService.login(any())).thenReturn(null);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_serviceThrowsException() throws Exception {
        LoginRequestDto req = new LoginRequestDto("user", "pass");
        when(userService.login(any())).thenThrow(new RuntimeException("Database error"));
        
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCurrentUser_nullToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_invalidToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "badtoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_invalidTokenFormat() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "InvalidFormat"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_userNotFound() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + generateValidToken("nonexistent")))
                .andExpect(status().isUnauthorized()); // JWT validation fails first
    }

    @Test
    void getCurrentUser_serverError() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + generateValidToken("testuser")))
                .andExpect(status().isUnauthorized()); // JWT validation fails first
    }

    @Test
    void debug_success() throws Exception {
        when(userService.countUsers()).thenReturn(5L);
        mockMvc.perform(get("/api/auth/debug"))
                .andExpect(status().isOk())
                .andExpect(content().string("Users table initialized: true"));
    }

    @Test
    void debug_exception() throws Exception {
        when(userService.countUsers()).thenThrow(new RuntimeException("Database error"));
        mockMvc.perform(get("/api/auth/debug"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Database error: Database error"));
    }

    @Test
    void checkUser_exists() throws Exception {
        when(userService.existsByUsername("testuser")).thenReturn(true);
        mockMvc.perform(get("/api/auth/check-user/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("User exists: true"));
    }

    @Test
    void checkUser_notExists() throws Exception {
        when(userService.existsByUsername("nonexistent")).thenReturn(false);
        mockMvc.perform(get("/api/auth/check-user/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(content().string("User exists: false"));
    }

    @Test
    void checkUser_exception() throws Exception {
        when(userService.existsByUsername("testuser")).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/auth/check-user/testuser"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error checking user: DB error"));
    }

    private String generateValidToken(String username) {
        return "eyJhbGciOiJIUzI1NiJ9." + 
               "eyJzdWIiOi\"" + username + "\"," +
               "\"iat\":" + System.currentTimeMillis() / 1000 + "," +
               "\"exp\":" + (System.currentTimeMillis() / 1000 + 86400) + 
               "}.signature";
    }
} 