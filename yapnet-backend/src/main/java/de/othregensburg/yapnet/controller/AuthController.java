package de.othregensburg.yapnet.controller;

import de.othregensburg.yapnet.dto.LoginRequestDto;
import de.othregensburg.yapnet.dto.LoginResponseDto;
import de.othregensburg.yapnet.dto.RegisterRequestDto;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import de.othregensburg.yapnet.dto.UserDto;
import java.util.stream.Collectors;
import java.util.List;
import de.othregensburg.yapnet.repository.FollowRepository;
import de.othregensburg.yapnet.service.SimpleAuthService;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private static final long JWT_EXPIRATION = 86400000; // 24 hours
    private final String jwtSecret;
    private final Key jwtKey;
    private final FollowRepository followRepository;
    private final SimpleAuthService authService;

    public AuthController(UserService userService, @Value("${jwt.secret}") String jwtSecret, FollowRepository followRepository, SimpleAuthService authService) {
        this.userService = userService;
        this.jwtSecret = jwtSecret;
        this.jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.followRepository = followRepository;
        this.authService = authService;
    }

    private String generateJwtToken(String username) {
        try {
            // Create key from secret
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            System.err.println("Error generating JWT token: " + e.getMessage());
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDto> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponseDto("Invalid token format"));
            }

            String jwt = token.substring(7); // Remove "Bearer " prefix
            System.out.println("Validating token: " + jwt);
            System.out.println("JWT key: " + jwtKey);

            Claims claims;
            try {
                claims = Jwts.parser()
                        .verifyWith((SecretKey) jwtKey)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();
            } catch (Exception e) {
                System.err.println("Error getting current user: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponseDto("Invalid token"));
            }

            System.out.println("Token validation successful");
            System.out.println("Claims: " + claims);
            System.out.println("Subject: " + claims.getSubject());

            String username = claims.getSubject();
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponseDto("Invalid token"));
            }

            Optional<User> optionalUser = userService.findByUsername(username);
            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new LoginResponseDto("User not found"));
            }

            User user = optionalUser.get();
            return ResponseEntity.ok(new LoginResponseDto(null, user.getUsername(), user.getEmail()));
        } catch (Exception e) {
            System.err.println("Unexpected error in getCurrentUser: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponseDto("Server error: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@RequestBody RegisterRequestDto registerRequest) {
        try {
            System.out.println("Register request received");
            System.out.println("Request body: " + registerRequest);
            
            // Validate request
            if (registerRequest == null) {
                System.err.println("Registration request is null");
                return ResponseEntity.badRequest()
                    .body(new LoginResponseDto("Registration request cannot be null"));
            }
            
            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                System.err.println("Username is empty or null");
                return ResponseEntity.badRequest()
                    .body(new LoginResponseDto("Username is required"));
            }
            
            if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
                System.err.println("Email is empty or null");
                return ResponseEntity.badRequest()
                    .body(new LoginResponseDto("Email is required"));
            }
            
            if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
                System.err.println("Password is empty or null");
                return ResponseEntity.badRequest()
                    .body(new LoginResponseDto("Password is required"));
            }
            
            // Log request data
            System.out.println("Registering user: " + registerRequest.getUsername());
            System.out.println("Email: " + registerRequest.getEmail());
            System.out.println("Password length: " + registerRequest.getPassword().length());
            
            LoginResponseDto response = userService.register(registerRequest);
            
            // Log response
            if (response == null) {
                System.err.println("User service returned null response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponseDto("Registration failed: null response from service"));
            }
            
            if (response.getErrorMessage() != null && !response.getErrorMessage().isEmpty()) {
                System.err.println("Registration failed: " + response.getErrorMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
            }
            
            String token = generateJwtToken(registerRequest.getUsername());
            response.setToken(token);
            
            System.out.println("Registration successful");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new LoginResponseDto(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error during registration: " + e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.err.println("Stack trace: " + sw.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new LoginResponseDto("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            System.out.println("Login attempt for username: " + loginRequest.getUsername());
            System.out.println("Password length: " + (loginRequest.getPassword() != null ? loginRequest.getPassword().length() : 0));
            
            if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                System.err.println("Invalid login request: " + loginRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponseDto("Invalid login request"));
            }

            LoginResponseDto response = userService.login(loginRequest);
            if (response == null || response.getErrorMessage() != null) {
                System.err.println("Login failed: " + (response != null ? response.getErrorMessage() : "null response"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response != null ? response : new LoginResponseDto("Invalid username or password"));
            }
            
            System.out.println("Login service response: " + response);
            System.out.println("Username from response: " + response.getUsername());
            System.out.println("Email from response: " + response.getEmail());
            System.out.println("Error message: " + response.getErrorMessage());
            
            String token = generateJwtToken(response.getUsername());
            System.out.println("Generated JWT token for user: " + response.getUsername());
            
            LoginResponseDto finalResponse = new LoginResponseDto(token, response.getUsername(), response.getEmail());
            System.out.println("Final response to send: " + finalResponse);
            
            return ResponseEntity.ok(finalResponse);
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponseDto("Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        try {
            // Check if users table exists and has data
            long userCount = userService.countUsers();
            return ResponseEntity.ok("Users table initialized: " + (userCount > 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error: " + e.getMessage());
        }
    }

    @GetMapping("/check-user/{username}")
    public ResponseEntity<String> checkUser(@PathVariable String username) {
        try {
            boolean exists = userService.existsByUsername(username);
            return ResponseEntity.ok("User exists: " + exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking user: " + e.getMessage());
        }
    }
}
