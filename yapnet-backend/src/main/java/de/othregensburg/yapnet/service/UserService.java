package de.othregensburg.yapnet.service;

import de.othregensburg.yapnet.dto.LoginRequestDto;
import de.othregensburg.yapnet.dto.LoginResponseDto;
import de.othregensburg.yapnet.dto.RegisterRequestDto;
import de.othregensburg.yapnet.dto.UpdateUserProfileDto;
import de.othregensburg.yapnet.dto.UserDto;
import de.othregensburg.yapnet.dto.UserStatsDto;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.model.Role;
import de.othregensburg.yapnet.repository.UserRepository;
import de.othregensburg.yapnet.repository.RoleRepository;
import de.othregensburg.yapnet.repository.PostRepository;
import de.othregensburg.yapnet.repository.CommentRepository;
import de.othregensburg.yapnet.repository.LikeRepository;
import de.othregensburg.yapnet.repository.FollowRepository;
import de.othregensburg.yapnet.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.io.StringWriter;
import java.io.PrintWriter;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final String jwtSecret;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, RoleRepository roleRepository, @Value("${jwt.secret}") String jwtSecret) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.jwtSecret = jwtSecret;
        roleService.initRoles();
    }

    @Transactional
    public void init() {
        try {
            System.out.println("Initializing database...");
            System.out.println("Checking for existing users...");
            
            // Check if any users exist
            long userCount = countUsers();
            System.out.println("Existing users count: " + userCount);
            
            if (userCount == 0) {
                System.out.println("No users found, initializing database...");
                
                // Initialize roles
                roleService.initRoles();
                
                // Create admin user if it doesn't exist
                if (!existsByUsername("admin")) {
                    System.out.println("Creating admin user...");
                    User admin = new User("admin", hashPassword("admin123"), "admin@example.com");
                    admin.setEnabled(true);
                    
                    // Get ADMIN role
                    Role adminRole = roleService.findByRoleName("ADMIN");
                    if (adminRole == null) {
                        System.err.println("ADMIN role not found in database");
                        adminRole = new Role("ADMIN");
                        adminRole = roleRepository.save(adminRole);
                        if (adminRole == null) {
                            throw new RuntimeException("Failed to create ADMIN role");
                        }
                        System.out.println("Created ADMIN role with ID: " + adminRole.getId());
                    }
                    admin.getRoles().add(adminRole);
                    
                    // Save admin user
                    User savedAdmin = userRepository.save(admin);
                    if (savedAdmin == null) {
                        throw new RuntimeException("Failed to save admin user");
                    }
                    System.out.println("Admin user created successfully with ID: " + savedAdmin.getId());
                }
            } else {
                System.out.println("Users already exist in database");
            }
            
            System.out.println("Database initialization completed successfully");
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.err.println("Stack trace: " + sw.toString());
            throw e;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String hashed = hexString.toString();
            System.out.println("Hashed password: " + hashed);
            return hashed;
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage(), e);
        }
    }

    private boolean verifyPassword(String inputPassword, String storedHash) {
        String inputHash = hashPassword(inputPassword);
        return inputHash.equals(storedHash);
    }

    @Transactional
    public LoginResponseDto register(RegisterRequestDto registerRequest) {
        try {
            System.out.println("Starting registration process...");
            System.out.println("Request data: " + registerRequest);
            
            // Validate request
            if (registerRequest == null) {
                System.err.println("Registration request is null");
                return new LoginResponseDto("Registration request cannot be null");
            }

            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                System.err.println("Username is empty or null");
                return new LoginResponseDto("Username is required");
            }

            if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
                System.err.println("Email is empty or null");
                return new LoginResponseDto("Email is required");
            }

            if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
                System.err.println("Password is empty or null");
                return new LoginResponseDto("Password is required");
            }

            System.out.println("Registering user: " + registerRequest.getUsername());
            System.out.println("Email: " + registerRequest.getEmail());
            System.out.println("Password length: " + registerRequest.getPassword().length());
            
            // Use case-insensitive username check
            if (userRepository.existsByUsernameIgnoreCase(registerRequest.getUsername())) {
                System.err.println("Username already exists: " + registerRequest.getUsername());
                return new LoginResponseDto("Username already exists");
            }

            // Check if email exists
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                System.err.println("Email already exists: " + registerRequest.getEmail());
                return new LoginResponseDto("Email already exists");
            }

            // Get USER role
            Role userRole = roleService.findByRoleName("USER");
            if (userRole == null) {
                System.err.println("USER role not found");
                return new LoginResponseDto("System error: USER role not found");
            }

            // Create user object
            User user = new User(registerRequest.getUsername(), 
                               hashPassword(registerRequest.getPassword()),
                               registerRequest.getEmail());
            user.setEnabled(true);
            user.getRoles().add(userRole);

            // Save user
            User savedUser = userRepository.save(user);
            if (savedUser == null) {
                System.err.println("Failed to save user");
                return new LoginResponseDto("Failed to save user");
            }
            
            System.out.println("User registered successfully: " + user.getUsername());
            return new LoginResponseDto(null, user.getUsername(), user.getEmail());
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.err.println("Stack trace: " + sw.toString());
            return new LoginResponseDto("Registration failed: " + e.getMessage());
        }
    }

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        try {
            System.out.println("Login attempt for username: " + loginRequest.getUsername());
            System.out.println("Password length: " + (loginRequest.getPassword() != null ? loginRequest.getPassword().length() : 0));
            System.out.println("Input password: " + loginRequest.getPassword());
            
            if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                System.err.println("Invalid login request: " + loginRequest);
                return new LoginResponseDto("Invalid login request");
            }

            // Use case-insensitive username search
            Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(loginRequest.getUsername());
            if (!optionalUser.isPresent()) {
                System.err.println("User not found in database for username: " + loginRequest.getUsername());
                return new LoginResponseDto(null, null, null, "User not found");
            }
            
            User user = optionalUser.get();
            System.out.println("Found user: " + user.getUsername() + ", Email: " + user.getEmail());
            System.out.println("Stored password hash: " + user.getPassword());
            System.out.println("User enabled: " + user.isEnabled());
            
            String inputHash = hashPassword(loginRequest.getPassword());
            System.out.println("Input password hash: " + inputHash);
            System.out.println("Stored password hash: " + user.getPassword());
            System.out.println("Password verification result: " + verifyPassword(loginRequest.getPassword(), user.getPassword()));
            
            if (!verifyPassword(loginRequest.getPassword(), user.getPassword())) {
                System.err.println("Password mismatch for user: " + user.getUsername());
                System.err.println("Input hash: " + inputHash);
                System.err.println("Stored hash: " + user.getPassword());
                return new LoginResponseDto(null, null, null, "Invalid password");
            }
            
            String token = generateJwtToken(user.getUsername());
            System.out.println("Login successful for user: " + user.getUsername());
            return new LoginResponseDto(token, user.getUsername(), user.getEmail());
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            return new LoginResponseDto(null, null, null, "Login failed: " + e.getMessage());
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public long countUsers() {
        return userRepository.count();
    }

    public List<User> searchUsers(String term) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
    }

    public List<User> searchUsersByUsername(String term) {
        return userRepository.findByUsernameContainingIgnoreCase(term);
    }

    public List<User> searchUsersByEmail(String term) {
        return userRepository.findByEmailContainingIgnoreCase(term);
    }

    public Page<User> searchUsersPaginated(String term, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term, pageable);
    }

    public List<User> advancedSearch(String username, String email, String fullName, Boolean enabled) {
        if (username != null && email != null && fullName != null && enabled != null) {
            return userRepository.findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndFullNameContainingIgnoreCaseAndEnabled(username, email, fullName, enabled);
        } else if (username != null && email != null && fullName != null) {
            return userRepository.findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndFullNameContainingIgnoreCase(username, email, fullName);
        } else if (username != null && email != null) {
            return userRepository.findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCase(username, email);
        } else if (username != null) {
            return userRepository.findByUsernameContainingIgnoreCase(username);
        } else if (email != null) {
            return userRepository.findByEmailContainingIgnoreCase(email);
        } else if (fullName != null) {
            return userRepository.findByFullNameContainingIgnoreCase(fullName);
        } else if (enabled != null) {
            return userRepository.findByEnabled(enabled);
        } else {
            return userRepository.findAll();
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    public UserDto getUserProfile(String username, String currentUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User currentUser = currentUsername != null ? 
            userRepository.findByUsername(currentUsername).orElse(null) : null;
        return convertToDto(user, currentUser);
    }

    public UserDto updateUserProfile(String username, UpdateUserProfileDto updateProfileDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (updateProfileDto.getFullName() != null) {
            user.setFullName(updateProfileDto.getFullName());
        }
        if (updateProfileDto.getBiography() != null) {
            user.setBiography(updateProfileDto.getBiography());
        }
        if (updateProfileDto.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(updateProfileDto.getProfilePictureUrl());
        }
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserStatsDto getUserStats(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        long postCount = postRepository.countByUser(user);
        long commentCount = commentRepository.countByUser(user);
        long likeCount = likeRepository.countByUser(user);
        long followerCount = followRepository.countByFollowingId(user.getId());
        long followingCount = followRepository.countByFollowerId(user.getId());
        return new UserStatsDto(postCount, commentCount, likeCount, followerCount, followingCount);
    }

    public long getFollowerCount(UUID userId) {
        return followRepository.countByFollowingId(userId);
    }

    public long getFollowingCount(UUID userId) {
        return followRepository.countByFollowerId(userId);
    }

    public List<UserDto> getMostPopularUsers(int limit) {
        List<User> users = userRepository.findAll();
        return users.stream()
            .sorted((u1, u2) -> Long.compare(
                followRepository.countByFollowingId(u2.getId()),
                followRepository.countByFollowingId(u1.getId())
            ))
            .limit(limit)
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<UserDto> getMostPopularUsers(int limit, String currentUsername) {
        List<User> users = userRepository.findAll();
        final User currentUser = currentUsername != null ? 
            userRepository.findByUsername(currentUsername).orElse(null) : null;
        
        return users.stream()
            .sorted((u1, u2) -> Long.compare(
                followRepository.countByFollowingId(u2.getId()),
                followRepository.countByFollowingId(u1.getId())
            ))
            .limit(limit)
            .map(user -> convertToDto(user, currentUser))
            .collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setBiography(user.getBiography());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setEnabled(user.isEnabled());
        dto.setFollowerCount(followRepository.countByFollowingId(user.getId()));
        return dto;
    }

    private UserDto convertToDto(User user, User currentUser) {
        UserDto dto = convertToDto(user);
        if (currentUser != null && !currentUser.getId().equals(user.getId())) {
            // Check if current user is following this user
            boolean isFollowed = followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), user.getId());
            dto.setIsFollowed(isFollowed);
        }
        return dto;
    }

    private String generateJwtToken(String username) {
        try {
            // Create key from secret
            byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                    .signWith(key)
                .compact();
        } catch (Exception e) {
            System.err.println("Error generating JWT token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }
}
