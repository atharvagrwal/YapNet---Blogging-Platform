package de.othregensburg.yapnet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.othregensburg.yapnet.dto.UpdateUserProfileDto;
import de.othregensburg.yapnet.dto.UserDto;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.FollowRepository;
import de.othregensburg.yapnet.service.SimpleAuthService;
import de.othregensburg.yapnet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock private UserService userService;
    @Mock private FollowRepository followRepository;
    @Mock private SimpleAuthService authService;
    private UserController userController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userController = new UserController(userService, followRepository, authService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new de.othregensburg.yapnet.controller.GlobalExceptionHandler())
            .build();
    }

    @Test
    void searchUsers_success() throws Exception {
        User user = new User("user", "password123", "user@example.com");
        user.setId(userId);
        Page<User> page = new PageImpl<>(List.of(user));
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.searchUsersPaginated(anyString(), any())).thenReturn(page);
        when(followRepository.existsByFollowerIdAndFollowingId(any(), any())).thenReturn(false);
        mockMvc.perform(get("/api/users/search").param("query", "user").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void searchUsers_withPagination() throws Exception {
        User user = new User("user", "password123", "user@example.com");
        user.setId(userId);
        Page<User> page = new PageImpl<>(List.of(user));
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.searchUsersPaginated(anyString(), any())).thenReturn(page);
        when(followRepository.existsByFollowerIdAndFollowingId(any(), any())).thenReturn(true);
        mockMvc.perform(get("/api/users/search")
                .param("query", "user")
                .param("page", "1")
                .param("size", "20")
                .header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void searchUsersByUsername_success() throws Exception {
        User user = new User("user", "password123", "user@example.com");
        user.setId(userId);
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.searchUsersByUsername(anyString())).thenReturn(List.of(user));
        when(followRepository.existsByFollowerIdAndFollowingId(any(), any())).thenReturn(false);
        mockMvc.perform(get("/api/users/search/username").param("query", "user").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void searchUsersByUsername_emptyResult() throws Exception {
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.searchUsersByUsername(anyString())).thenReturn(List.of());
        mockMvc.perform(get("/api/users/search/username").param("query", "nonexistent").header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchUsersByEmail_success() throws Exception {
        User user = new User("user", "password123", "user@example.com");
        user.setId(userId);
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.searchUsersByEmail(anyString())).thenReturn(List.of(user));
        when(followRepository.existsByFollowerIdAndFollowingId(any(), any())).thenReturn(true);
        mockMvc.perform(get("/api/users/search/email").param("query", "user@example.com").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void searchUsersByEmail_emptyResult() throws Exception {
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.searchUsersByEmail(anyString())).thenReturn(List.of());
        mockMvc.perform(get("/api/users/search/email").param("query", "nonexistent@example.com").header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void advancedSearch_success() throws Exception {
        User user = new User("user", "password123", "user@example.com");
        user.setId(userId);
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.advancedSearch(anyString(), anyString(), anyString(), any())).thenReturn(List.of(user));
        when(followRepository.existsByFollowerIdAndFollowingId(any(), any())).thenReturn(false);
        mockMvc.perform(get("/api/users/search/advanced")
                .param("username", "user")
                .param("email", "user@example.com")
                .param("fullName", "Test User")
                .param("enabled", "true")
                .header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void advancedSearch_withPartialCriteria() throws Exception {
        User user = new User("user", "password123", "user@example.com");
        user.setId(userId);
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.advancedSearch(anyString(), eq(null), eq(null), eq(null))).thenReturn(List.of(user));
        when(followRepository.existsByFollowerIdAndFollowingId(any(), any())).thenReturn(false);
        mockMvc.perform(get("/api/users/search/advanced")
                .param("username", "user")
                .header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserByUsername_success() throws Exception {
        User user = new User("user", "password123", "user@example.com");
        user.setId(userId);
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(followRepository.existsByFollowerIdAndFollowingId(any(), any())).thenReturn(false);
        mockMvc.perform(get("/api/users/username/user").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserByUsername_notFound() throws Exception {
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/users/username/user").header("Authorization", "token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchUsersPublic_success() throws Exception {
        User user = new User("user", "password123", "user@example.com");
        user.setId(userId);
        when(userService.searchUsers(anyString())).thenReturn(List.of(user));
        mockMvc.perform(get("/api/users/search/public").param("query", "user"))
                .andExpect(status().isOk());
    }

    @Test
    void searchUsersPublic_emptyResult() throws Exception {
        when(userService.searchUsers(anyString())).thenReturn(List.of());
        mockMvc.perform(get("/api/users/search/public").param("query", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void checkUsernameExists_true() throws Exception {
        when(userService.existsByUsername(anyString())).thenReturn(true);
        mockMvc.perform(get("/api/users/check-username/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    void checkUsernameExists_false() throws Exception {
        when(userService.existsByUsername(anyString())).thenReturn(false);
        mockMvc.perform(get("/api/users/check-username/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    void getAllUsers_success() throws Exception {
        User user1 = new User("user1", "password123", "user1@example.com");
        User user2 = new User("user2", "password123", "user2@example.com");
        user1.setId(userId);
        user2.setId(UUID.randomUUID());
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));
        when(followRepository.existsByFollowerIdAndFollowingId(any(), any())).thenReturn(false);
        mockMvc.perform(get("/api/users/all").header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    void getAllUsers_emptyResult() throws Exception {
        when(authService.getUserIdFromToken(anyString())).thenReturn(userId);
        when(userService.getAllUsers()).thenReturn(List.of());
        mockMvc.perform(get("/api/users/all").header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getUserProfile_success() throws Exception {
        UserDto dto = new UserDto();
        dto.setUsername("user");
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(userService.getUserProfile(anyString(), anyString())).thenReturn(dto);
        mockMvc.perform(get("/api/users/profile").header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void updateUserProfile_success() throws Exception {
        UpdateUserProfileDto updateDto = new UpdateUserProfileDto();
        updateDto.setFullName("Test User");
        UserDto dto = new UserDto();
        dto.setUsername("user");
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(userService.updateUserProfile(anyString(), any())).thenReturn(dto);
        mockMvc.perform(put("/api/users/profile").header("Authorization", "token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void updateUserProfile_withAllFields() throws Exception {
        UpdateUserProfileDto updateDto = new UpdateUserProfileDto();
        updateDto.setFullName("Test User");
        updateDto.setBiography("Test bio");
        updateDto.setProfilePictureUrl("https://example.com/pic.jpg");
        UserDto dto = new UserDto();
        dto.setUsername("user");
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(userService.updateUserProfile(anyString(), any())).thenReturn(dto);
        mockMvc.perform(put("/api/users/profile").header("Authorization", "token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserProfile_emptyBody() throws Exception {
        UpdateUserProfileDto updateDto = new UpdateUserProfileDto();
        UserDto dto = new UserDto();
        dto.setUsername("user");
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(userService.updateUserProfile(anyString(), any())).thenReturn(dto);
        mockMvc.perform(put("/api/users/profile").header("Authorization", "token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }
} 