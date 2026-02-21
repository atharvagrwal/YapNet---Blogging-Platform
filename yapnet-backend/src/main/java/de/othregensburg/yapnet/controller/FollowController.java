package de.othregensburg.yapnet.controller;

import de.othregensburg.yapnet.dto.FollowDto;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.service.FollowService;
import de.othregensburg.yapnet.service.SimpleAuthService;
import de.othregensburg.yapnet.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);
    private final FollowService followService;
    private final SimpleAuthService authService;
    private final UserRepository userRepository;

    public FollowController(FollowService followService, SimpleAuthService authService, UserRepository userRepository) {
        this.followService = followService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<FollowDto> toggleFollow(@RequestBody FollowDto followDto, @RequestHeader("Authorization") String token) {
        try {
            if (followDto == null || followDto.getFollowingId() == null) {
                FollowDto errorDto = new FollowDto();
                errorDto.setErrorMessage("Missing followingId");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
            }
            String username = authService.extractUsername(token);
            if (username == null) {
                FollowDto errorDto = new FollowDto();
                errorDto.setErrorMessage("Not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
            }
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                FollowDto errorDto = new FollowDto();
                errorDto.setErrorMessage("User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
            }
            logger.debug("Attempting to toggle follow for user: {} following: {}", 
                    user.getUsername(), followDto.getFollowingId());
            FollowDto result = followService.toggleFollow(followDto.getFollowingId(), user);
            logger.debug("Follow action result: {}", result);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid operation: {}", e.getMessage());
            FollowDto errorDto = new FollowDto();
            errorDto.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        } catch (Exception e) {
            logger.error("Error in follow toggle: {}", e.getMessage(), e);
            FollowDto errorDto = new FollowDto();
            errorDto.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
        }
    }

    @DeleteMapping("/{followerId}/{followingId}")
    public ResponseEntity<Void> deleteFollow(@PathVariable UUID followerId,
                                            @PathVariable UUID followingId) {
        followService.deleteFollow(followerId, followingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<Long> getFollowerCount(@PathVariable UUID userId) {
        return followService.getFollowerCount(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<Long> getFollowingCount(@PathVariable UUID userId) {
        return followService.getFollowingCount(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

