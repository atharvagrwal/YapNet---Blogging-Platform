package de.othregensburg.yapnet.service;

import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

/**
 * Simple authentication service for handling JWT tokens and user authentication.
 * This service provides basic JWT token generation, validation, and user extraction.
 * 
 * Note: This is a simplified implementation for educational purposes.
 * In a production environment, you'd want more robust security measures.
 */
@Service
public class SimpleAuthService {
    
    // Logger for debugging and monitoring authentication events
    private static final Logger logger = LoggerFactory.getLogger(SimpleAuthService.class);
    
    // JWT configuration - token expires after 24 hours
    private static final long JWT_EXPIRATION = 86400000; // 24 hours in milliseconds
    
    // Dependencies injected through constructor
    private final Key jwtKey;
    private final String jwtSecret;
    private final UserRepository userRepository;

    /**
     * Constructor that initializes the JWT key and dependencies.
     * The JWT secret is injected from application properties.
     * 
     * @param userRepository Repository for user data access
     * @param jwtSecret Secret key for JWT signing (from application.properties)
     */
    public SimpleAuthService(UserRepository userRepository, @Value("${jwt.secret}") String jwtSecret) {
        this.userRepository = userRepository;
        this.jwtSecret = jwtSecret;
        // Create a cryptographic key from the secret for JWT signing
        this.jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        logger.info("SimpleAuthService initialized with JWT secret of length: {}", jwtSecret.length());
    }

    /**
     * Generates a JWT token for a given user.
     * The token contains the username, issue time, and expiration time.
     * 
     * @param user The user for whom to generate the token
     * @return JWT token string
     */
    public String generateToken(User user) {
        logger.debug("Generating JWT token for user: {}", user.getUsername());
        
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(jwtKey)
                .compact();
    }

    /**
     * Gets the current authenticated user from the security context.
     * This method currently returns a hardcoded user for testing purposes.
     * 
     * TODO: In a real implementation, this should extract the user from Spring Security context
     * 
     * @return Current authenticated user
     * @throws UsernameNotFoundException if user is not found
     */
    public User getCurrentUser() {
        // TODO: Replace with actual security context extraction
        // For now, we'll use a simple approach - this should be improved in production
        String username = getUsername();
        logger.debug("Getting current user for username: {}", username);
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Extracts the username from the current security context.
     * This is a placeholder implementation that should be replaced with proper security context handling.
     * 
     * @return Current username (hardcoded for now)
     */
    private String getUsername() {
        // TODO: Replace with actual username from Spring Security context
        // This is a temporary implementation for development/testing
        return "testUser";
    }

    /**
     * Extracts the username from a JWT token.
     * Handles the "Bearer " prefix and validates the token structure.
     * 
     * @param token JWT token with optional "Bearer " prefix
     * @return Username from token, or null if token is invalid
     */
    public String extractUsername(String token) {
        try {
            // Check if token exists and has proper format
            if (token == null || !token.startsWith("Bearer ")) {
                logger.debug("Invalid token format or null token provided");
                return null;
            }
            
            // Remove "Bearer " prefix to get the actual JWT
            String jwt = token.substring(7);
            logger.debug("Extracting username from JWT token");
            
            // Parse and verify the JWT token
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) jwtKey)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            
            String username = claims.getSubject();
            logger.debug("Successfully extracted username: {}", username);
            return username;
            
        } catch (Exception e) {
            logger.error("Error parsing JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validates whether a JWT token is valid and not expired.
     * 
     * @param token JWT token with optional "Bearer " prefix
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Check if token exists and has proper format
            if (token == null || !token.startsWith("Bearer ")) {
                logger.debug("Invalid token format or null token provided for validation");
                return false;
            }
            
            // Remove "Bearer " prefix
            String jwt = token.substring(7);
            logger.debug("Validating JWT token");
            
            // Parse and verify the JWT token
            Jwts.parser().verifyWith((SecretKey) jwtKey).build().parseSignedClaims(jwt);
            
            logger.debug("Token validation successful");
            return true;
            
        } catch (Exception e) {
            logger.error("Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the user ID from a JWT token by first extracting the username,
     * then looking up the user in the database.
     * 
     * @param token JWT token with optional "Bearer " prefix
     * @return User ID as UUID
     * @throws RuntimeException if user is not found in database
     */
    public UUID getUserIdFromToken(String token) {
        String username = extractUsername(token);
        if (username == null) {
            logger.error("Could not extract username from token");
            throw new RuntimeException("Invalid token - could not extract username");
        }
        
        logger.debug("Looking up user ID for username: {}", username);
        return userRepository.findByUsername(username)
            .map(user -> {
                logger.debug("Found user ID: {} for username: {}", user.getId(), username);
                return user.getId();
            })
            .orElseThrow(() -> {
                logger.error("User not found for username: {}", username);
                return new RuntimeException("User not found for token");
            });
    }
}
