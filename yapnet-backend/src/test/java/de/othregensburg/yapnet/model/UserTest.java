package de.othregensburg.yapnet.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class UserTest {

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password123", "test@example.com");
        role = new Role("USER");
    }

    @Test
    void testUserConstructorWithValidData() {
        User validUser = new User("validuser", "password123", "valid@example.com");
        
        assertEquals("validuser", validUser.getUsername());
        assertEquals("password123", validUser.getPassword());
        assertEquals("valid@example.com", validUser.getEmail());
        assertTrue(validUser.isEnabled());
        assertNotNull(validUser.getRoles());
        assertEquals(0, validUser.getRoles().size());
    }

    @Test
    void testUserConstructorWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User(null, "password123", "test@example.com");
        });
    }

    @Test
    void testUserConstructorWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("", "password123", "test@example.com");
        });
    }

    @Test
    void testUserConstructorWithWhitespaceUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("   ", "password123", "test@example.com");
        });
    }

    @Test
    void testUserConstructorWithNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("testuser", null, "test@example.com");
        });
    }

    @Test
    void testUserConstructorWithShortPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("testuser", "12345", "test@example.com");
        });
    }

    @Test
    void testUserConstructorWithNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("testuser", "password123", null);
        });
    }

    @Test
    void testUserConstructorWithInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("testuser", "password123", "invalid-email");
        });
    }

    @Test
    void testUserConstructorTrimsUsernameAndEmail() {
        User trimmedUser = new User("  testuser  ", "password123", "  TEST@EXAMPLE.COM  ");
        
        assertEquals("testuser", trimmedUser.getUsername());
        assertEquals("test@example.com", trimmedUser.getEmail());
    }

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        user.setId(id);
        assertEquals(id, user.getId());

        user.setUsername("newusername");
        assertEquals("newusername", user.getUsername());

        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());

        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());

        user.setFullName("John Doe");
        assertEquals("John Doe", user.getFullName());

        user.setBiography("Test biography");
        assertEquals("Test biography", user.getBiography());

        user.setProfilePictureUrl("http://example.com/pic.jpg");
        assertEquals("http://example.com/pic.jpg", user.getProfilePictureUrl());

        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void testAddRole() {
        assertTrue(user.getRoles().isEmpty());
        
        user.addRole(role);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void testAddNullRole() {
        int initialSize = user.getRoles().size();
        user.addRole(null);
        assertEquals(initialSize, user.getRoles().size());
    }

    @Test
    void testSetRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        
        user.setRoles(roles);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void testMultipleRoles() {
        Role adminRole = new Role("ADMIN");
        user.addRole(role);
        user.addRole(adminRole);
        
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(role));
        assertTrue(user.getRoles().contains(adminRole));
    }
} 