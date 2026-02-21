package de.othregensburg.yapnet.controller;

import de.othregensburg.yapnet.dto.UserDto;
import de.othregensburg.yapnet.dto.UpdateUserProfileDto;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.FollowRepository;
import de.othregensburg.yapnet.service.SimpleAuthService;
import de.othregensburg.yapnet.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final FollowRepository followRepository;
    private final SimpleAuthService authService;

    public UserController(UserService userService, FollowRepository followRepository, SimpleAuthService authService) {
        this.userService = userService;
        this.followRepository = followRepository;
        this.authService = authService;
    }

    // General search with pagination (username and email)
    @GetMapping("/users/search")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam("query") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token) {
        UUID currentUserId = authService.getUserIdFromToken(token);
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userService.searchUsersPaginated(query, pageable);
        
        Page<UserDto> userDtosPage = usersPage.map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setIsFollowed(followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId()));
            return dto;
        });
        
        return ResponseEntity.ok(userDtosPage);
    }

    // Search by username only
    @GetMapping("/users/search/username")
    public ResponseEntity<List<UserDto>> searchUsersByUsername(
            @RequestParam("query") String query,
            @RequestHeader("Authorization") String token) {
        UUID currentUserId = authService.getUserIdFromToken(token);
        List<User> users = userService.searchUsersByUsername(query);
        List<UserDto> userDtos = users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setIsFollowed(followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId()));
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    // Search by email only
    @GetMapping("/users/search/email")
    public ResponseEntity<List<UserDto>> searchUsersByEmail(
            @RequestParam("query") String query,
            @RequestHeader("Authorization") String token) {
        UUID currentUserId = authService.getUserIdFromToken(token);
        List<User> users = userService.searchUsersByEmail(query);
        List<UserDto> userDtos = users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setIsFollowed(followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId()));
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    // Advanced search with multiple criteria
    @GetMapping("/users/search/advanced")
    public ResponseEntity<List<UserDto>> advancedSearch(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @RequestHeader("Authorization") String token) {
        UUID currentUserId = authService.getUserIdFromToken(token);
        List<User> users = userService.advancedSearch(username, email, fullName, enabled);
        List<UserDto> userDtos = users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setIsFollowed(followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId()));
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    // Get user by exact username
    @GetMapping("/users/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {
        UUID currentUserId = authService.getUserIdFromToken(token);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setIsFollowed(followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId()));
        
        return ResponseEntity.ok(dto);
    }

    // Public search for testing (no authentication required)
    @GetMapping("/users/search/public")
    public ResponseEntity<List<UserDto>> searchUsersPublic(@RequestParam("query") String query) {
        List<User> users = userService.searchUsers(query);
        List<UserDto> userDtos = users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setIsFollowed(false); // Default to false for public search
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    // Check if username exists (no auth required)
    @GetMapping("/users/check-username/{username}")
    public ResponseEntity<Map<String, Boolean>> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // Get all users (for admin purposes)
    @GetMapping("/users/all")
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestHeader("Authorization") String token) {
        UUID currentUserId = authService.getUserIdFromToken(token);
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setIsFollowed(followRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId()));
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/users/profile")
    public ResponseEntity<UserDto> getUserProfile(@RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(userService.getUserProfile(username, username));
    }

    @PutMapping("/users/profile")
    public ResponseEntity<UserDto> updateUserProfile(
            @RequestBody UpdateUserProfileDto updateProfileDto,
            @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(userService.updateUserProfile(username, updateProfileDto));
    }
} 

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
} 