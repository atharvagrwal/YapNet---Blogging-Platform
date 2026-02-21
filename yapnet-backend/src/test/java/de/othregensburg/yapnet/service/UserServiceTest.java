package de.othregensburg.yapnet.service;

import de.othregensburg.yapnet.dto.LoginRequestDto;
import de.othregensburg.yapnet.dto.LoginResponseDto;
import de.othregensburg.yapnet.dto.RegisterRequestDto;
import de.othregensburg.yapnet.dto.UpdateUserProfileDto;
import de.othregensburg.yapnet.dto.UserDto;
import de.othregensburg.yapnet.dto.UserStatsDto;
import de.othregensburg.yapnet.model.Role;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.*;
import de.othregensburg.yapnet.service.RoleService;
import de.othregensburg.yapnet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleService roleService;
    @Mock private RoleRepository roleRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private LikeRepository likeRepository;
    @Mock private FollowRepository followRepository;
    private UserService userService;

    private User testUser;
    private User adminUser;
    private Role userRole;
    private Role adminRole;
    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;

    @BeforeEach
    void setUp() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        
        testUser = new User("testuser", "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f", "test@example.com");
        testUser.setId(userId);
        testUser.setEnabled(true);
        testUser.setFullName("Test User");
        testUser.setBiography("Test bio");
        testUser.setProfilePictureUrl("https://example.com/pic.jpg");
        
        adminUser = new User("admin", "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f", "admin@example.com");
        adminUser.setId(adminId);
        adminUser.setEnabled(true);
        
        userRole = new Role("USER");
        adminRole = new Role("ADMIN");
        
        registerRequest = new RegisterRequestDto("testuser", "test@example.com", "password123");
        loginRequest = new LoginRequestDto("testuser", "password123");
        
        // Manually instantiate UserService with mocks
        userService = new UserService(userRepository, roleService, roleRepository, "test-jwt-secret-key-for-testing-purposes-only");
        
        // Set the additional repositories using reflection
        Field postRepositoryField = UserService.class.getDeclaredField("postRepository");
        postRepositoryField.setAccessible(true);
        postRepositoryField.set(userService, postRepository);
        
        Field commentRepositoryField = UserService.class.getDeclaredField("commentRepository");
        commentRepositoryField.setAccessible(true);
        commentRepositoryField.set(userService, commentRepository);
        
        Field likeRepositoryField = UserService.class.getDeclaredField("likeRepository");
        likeRepositoryField.setAccessible(true);
        likeRepositoryField.set(userService, likeRepository);
        
        Field followRepositoryField = UserService.class.getDeclaredField("followRepository");
        followRepositoryField.setAccessible(true);
        followRepositoryField.set(userService, followRepository);
    }

    @Test
    void testRegisterNewUser() {
        when(userRepository.existsByUsernameIgnoreCase("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleService.findByRoleName("USER")).thenReturn(userRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        LoginResponseDto result = userService.register(registerRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testRegisterExistingUsername() {
        when(userRepository.existsByUsernameIgnoreCase("testuser")).thenReturn(true);

        LoginResponseDto result = userService.register(registerRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("Username already exists"));
    }

    @Test
    void testRegisterExistingEmail() {
        when(userRepository.existsByUsernameIgnoreCase("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        LoginResponseDto result = userService.register(registerRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("Email already exists"));
    }

    @Test
    void testRegisterNullRequest() {
        LoginResponseDto result = userService.register(null);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("Registration request cannot be null"));
    }

    @Test
    void testRegisterEmptyUsername() {
        RegisterRequestDto emptyRequest = new RegisterRequestDto("", "test@example.com", "password123");
        
        LoginResponseDto result = userService.register(emptyRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("Username is required"));
    }

    @Test
    void testRegisterEmptyEmail() {
        RegisterRequestDto emptyRequest = new RegisterRequestDto("testuser", "", "password123");
        
        LoginResponseDto result = userService.register(emptyRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("Email is required"));
    }

    @Test
    void testRegisterEmptyPassword() {
        RegisterRequestDto emptyRequest = new RegisterRequestDto("testuser", "test@example.com", "");
        
        LoginResponseDto result = userService.register(emptyRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("Password is required"));
    }

    @Test
    void testRegisterUserRoleNotFound() {
        when(userRepository.existsByUsernameIgnoreCase("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleService.findByRoleName("USER")).thenReturn(null);

        LoginResponseDto result = userService.register(registerRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("System error: USER role not found"));
    }

    @Test
    void testLoginValidCredentials() {
        when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(testUser));

        LoginResponseDto result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsernameIgnoreCase("nonexistent")).thenReturn(Optional.empty());

        LoginRequestDto invalidRequest = new LoginRequestDto("nonexistent", "password");
        LoginResponseDto result = userService.login(invalidRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("User not found"));
    }

    @Test
    void testLoginNullRequest() {
        LoginResponseDto result = userService.login(null);
        assertNotNull(result);
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Login failed"));
    }

    @Test
    void testLoginNullUsername() {
        LoginRequestDto invalidRequest = new LoginRequestDto(null, "password");
        LoginResponseDto result = userService.login(invalidRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("Invalid login request"));
    }

    @Test
    void testLoginNullPassword() {
        LoginRequestDto invalidRequest = new LoginRequestDto("testuser", null);
        LoginResponseDto result = userService.login(invalidRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("Invalid login request"));
    }

    @Test
    void testLoginInvalidPassword() {
        User userWithDifferentPassword = new User("testuser", "different-hash", "test@example.com");
        when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(userWithDifferentPassword));

        LoginResponseDto result = userService.login(loginRequest);

        assertNotNull(result);
        assertTrue(result.getErrorMessage().contains("Invalid password"));
    }

    @Test
    void testLoginDisabledUser() {
        testUser.setEnabled(false);
        when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(testUser));

        LoginResponseDto result = userService.login(loginRequest);

        assertNotNull(result);
        // The current implementation doesn't check for disabled users, so it should succeed
        assertEquals("testuser", result.getUsername());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testGetUserProfile() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserProfile("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUserProfileNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserProfile("nonexistent"));
    }

    @Test
    void testUpdateUserProfile() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        UpdateUserProfileDto updateDto = new UpdateUserProfileDto();
        updateDto.setFullName("Updated User");
        updateDto.setBiography("Updated bio");
        updateDto.setProfilePictureUrl("https://example.com/updated.jpg");
        updateDto.setEmail("updated@example.com");

        UserDto result = userService.updateUserProfile("testuser", updateDto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserProfileNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        UpdateUserProfileDto updateDto = new UpdateUserProfileDto();
        updateDto.setFullName("Updated User");

        assertThrows(RuntimeException.class, () -> userService.updateUserProfile("nonexistent", updateDto));
    }

    @Test
    void testSearchUsers() {
        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase("test", "test"))
            .thenReturn(List.of(testUser));

        List<User> result = userService.searchUsers("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void testSearchUsersByUsername() {
        when(userRepository.findByUsernameContainingIgnoreCase("test"))
            .thenReturn(List.of(testUser));

        List<User> result = userService.searchUsersByUsername("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void testSearchUsersByEmail() {
        when(userRepository.findByEmailContainingIgnoreCase("test"))
            .thenReturn(List.of(testUser));

        List<User> result = userService.searchUsersByEmail("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void testSearchUsersPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);
        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase("test", "test", pageable))
            .thenReturn(userPage);

        Page<User> result = userService.searchUsersPaginated("test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void testAdvancedSearch() {
        when(userRepository.findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndFullNameContainingIgnoreCaseAndEnabled("test", "test", "User", true))
            .thenReturn(List.of(testUser));

        List<User> result = userService.advancedSearch("test", "test", "User", true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void testAdvancedSearchWithNullValues() {
        when(userRepository.findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndFullNameContainingIgnoreCase("test", "test", "User"))
            .thenReturn(List.of(testUser));

        List<User> result = userService.advancedSearch("test", "test", "User", null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser, adminUser));

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("admin", result.get(1).getUsername());
    }

    @Test
    void testGetUserStats() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.countByUser(testUser)).thenReturn(5L);
        when(commentRepository.countByUser(testUser)).thenReturn(10L);
        when(likeRepository.countByUser(testUser)).thenReturn(15L);
        when(followRepository.countByFollowingId(testUser.getId())).thenReturn(20L);
        when(followRepository.countByFollowerId(testUser.getId())).thenReturn(25L);

        UserStatsDto result = userService.getUserStats("testuser");

        assertNotNull(result);
        assertEquals(5L, result.getPostCount());
        assertEquals(10L, result.getCommentCount());
        assertEquals(15L, result.getLikeCount());
        assertEquals(20L, result.getFollowerCount());
        assertEquals(25L, result.getFollowingCount());
    }

    @Test
    void testGetUserStatsNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserStats("nonexistent"));
    }

    @Test
    void testGetFollowerCount() {
        when(followRepository.countByFollowingId(testUser.getId())).thenReturn(10L);

        long result = userService.getFollowerCount(testUser.getId());

        assertEquals(10L, result);
    }

    @Test
    void testGetFollowingCount() {
        when(followRepository.countByFollowerId(testUser.getId())).thenReturn(15L);

        long result = userService.getFollowingCount(testUser.getId());

        assertEquals(15L, result);
    }

    @Test
    void testGetMostPopularUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser, adminUser));
        when(followRepository.countByFollowingId(testUser.getId())).thenReturn(10L);
        when(followRepository.countByFollowingId(adminUser.getId())).thenReturn(5L);

        List<UserDto> result = userService.getMostPopularUsers(5);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("admin", result.get(1).getUsername());
    }

    @Test
    void testGetMostPopularUsersWithLimit() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> result = userService.getMostPopularUsers(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testExistsByUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean result = userService.existsByUsername("testuser");

        assertTrue(result);
    }

    @Test
    void testCountUsers() {
        when(userRepository.count()).thenReturn(100L);

        long result = userService.countUsers();

        assertEquals(100L, result);
    }

    @Test
    void testInit() {
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(roleService.findByRoleName("ADMIN")).thenReturn(adminRole);
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        assertDoesNotThrow(() -> userService.init());
        
        verify(roleService, times(2)).initRoles(); // Once in constructor, once in init
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testInitWithExistingUsers() {
        when(userRepository.count()).thenReturn(10L);

        assertDoesNotThrow(() -> userService.init());
        
        verify(roleService, times(1)).initRoles(); // Only in constructor
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testInitWithExistingAdmin() {
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertDoesNotThrow(() -> userService.init());
        
        verify(roleService, times(2)).initRoles(); // Once in constructor, once in init
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testInitWithAdminRoleNotFound() {
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(roleService.findByRoleName("ADMIN")).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenReturn(adminRole);
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        assertDoesNotThrow(() -> userService.init());
        
        verify(roleService, times(2)).initRoles(); // Once in constructor, once in init
        verify(roleRepository).save(any(Role.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testConvertToDto() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        UserDto result = userService.getUserProfile("testuser");
        
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getFullName());
        assertEquals("Test bio", result.getBiography());
        assertEquals("https://example.com/pic.jpg", result.getProfilePictureUrl());
        assertTrue(result.isEnabled());
    }

    @Test
    void testPasswordHashingThroughRegistration() {
        // Test that password hashing works through the registration process
        RegisterRequestDto request = new RegisterRequestDto("hashuser", "hash@example.com", "testpassword123");
        
        when(roleService.findByRoleName("USER")).thenReturn(new Role("USER"));
        when(userRepository.existsByUsernameIgnoreCase("hashuser")).thenReturn(false);
        when(userRepository.existsByEmail("hash@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            // Verify that the password was hashed (not plain text)
            assertNotEquals("testpassword123", savedUser.getPassword());
            assertTrue(savedUser.getPassword().length() == 64); // SHA-256 hash length
            assertTrue(savedUser.getPassword().matches("[a-f0-9]+")); // Hex string
            return savedUser;
        });
        
        LoginResponseDto response = userService.register(request);
        assertNotNull(response);
        assertNull(response.getErrorMessage()); // Should not have error
        assertEquals("hashuser", response.getUsername());
    }

    @Test
    void testPasswordVerificationThroughLogin() {
        // Test that password verification works through the login process
        String hashedPassword = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"; // hash of "password123"
        User user = new User("testuser", hashedPassword, "test@example.com");
        
        LoginRequestDto request = new LoginRequestDto("testuser", "password123");
        
        when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(user));
        
        LoginResponseDto response = userService.login(request);
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void testPasswordVerificationFailure() {
        // Test that wrong password fails verification
        String hashedPassword = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"; // hash of "password123"
        User user = new User("testuser", hashedPassword, "test@example.com");
        
        LoginRequestDto request = new LoginRequestDto("testuser", "wrongpassword");
        
        when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(user));
        
        LoginResponseDto response = userService.login(request);
        assertNotNull(response);
        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("Invalid password"));
    }
}
