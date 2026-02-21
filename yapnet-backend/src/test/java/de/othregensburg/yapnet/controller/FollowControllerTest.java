package de.othregensburg.yapnet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.othregensburg.yapnet.dto.FollowDto;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.service.FollowService;
import de.othregensburg.yapnet.service.SimpleAuthService;
import de.othregensburg.yapnet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {

    @Mock
    private FollowService followService;

    @Mock
    private SimpleAuthService authService;

    @Mock
    private UserRepository userRepository;

    private FollowController followController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final UUID followerId = UUID.randomUUID();
    private final UUID followingId = UUID.randomUUID();
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        followController = new FollowController(followService, authService, userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(followController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void toggleFollow_success() throws Exception {
        FollowDto followDto = new FollowDto();
        followDto.setFollowingId(followingId);

        User user = new User(username, "password123", "test@example.com");
        user.setId(followerId);

        FollowDto resultDto = new FollowDto();
        resultDto.setFollowingId(followingId);

        when(authService.extractUsername(anyString())).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(followService.toggleFollow(followingId, user)).thenReturn(resultDto);

        mockMvc.perform(post("/api/follow")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDto))
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followingId").exists());
    }

    @Test
    void toggleFollow_missingFollowingId() throws Exception {
        FollowDto followDto = new FollowDto();
        // followingId is null

        mockMvc.perform(post("/api/follow")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDto))
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Missing followingId"));
    }

    @Test
    void toggleFollow_nullRequest() throws Exception {
        mockMvc.perform(post("/api/follow")
                .contentType(APPLICATION_JSON)
                .content("null")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void toggleFollow_notAuthenticated() throws Exception {
        FollowDto followDto = new FollowDto();
        followDto.setFollowingId(followingId);

        when(authService.extractUsername(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/follow")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDto))
                .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage").value("Not authenticated"));
    }

    @Test
    void toggleFollow_userNotFound() throws Exception {
        FollowDto followDto = new FollowDto();
        followDto.setFollowingId(followingId);

        when(authService.extractUsername(anyString())).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/follow")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDto))
                .header("Authorization", "Bearer token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage").value("User not found"));
    }

    @Test
    void toggleFollow_illegalArgumentException() throws Exception {
        FollowDto followDto = new FollowDto();
        followDto.setFollowingId(followingId);

        User user = new User(username, "password123", "test@example.com");
        user.setId(followerId);

        when(authService.extractUsername(anyString())).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(followService.toggleFollow(followingId, user))
                .thenThrow(new IllegalArgumentException("Cannot follow yourself"));

        mockMvc.perform(post("/api/follow")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDto))
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Cannot follow yourself"));
    }

    @Test
    void deleteFollow_success() throws Exception {
        doNothing().when(followService).deleteFollow(followerId, followingId);

        mockMvc.perform(delete("/api/follow/{followerId}/{followingId}", followerId, followingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getFollowerCount_success() throws Exception {
        long followerCount = 42L;
        when(followService.getFollowerCount(followerId)).thenReturn(Optional.of(followerCount));

        mockMvc.perform(get("/api/follow/followers/{userId}", followerId))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    @Test
    void getFollowerCount_notFound() throws Exception {
        when(followService.getFollowerCount(followerId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/follow/followers/{userId}", followerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFollowingCount_success() throws Exception {
        long followingCount = 15L;
        when(followService.getFollowingCount(followerId)).thenReturn(Optional.of(followingCount));

        mockMvc.perform(get("/api/follow/following/{userId}", followerId))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void getFollowingCount_notFound() throws Exception {
        when(followService.getFollowingCount(followerId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/follow/following/{userId}", followerId))
                .andExpect(status().isNotFound());
    }
} 