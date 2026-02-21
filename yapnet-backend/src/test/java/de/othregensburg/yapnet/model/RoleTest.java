package de.othregensburg.yapnet.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

class RoleTest {

    private Role role;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        role = new Role("TEST_ROLE");
        
        user1 = new User("user1", "password123", "user1@example.com");
        user2 = new User("user2", "password123", "user2@example.com");
    }

    @Test
    void testDefaultConstructor() {
        Role defaultRole = new Role();
        assertNotNull(defaultRole);
        assertNull(defaultRole.getId());
        assertNull(defaultRole.getName());
        assertNotNull(defaultRole.getUsers());
        assertEquals(0, defaultRole.getUsers().size());
    }

    @Test
    void testParameterizedConstructor() {
        Role newRole = new Role("ADMIN");
        assertEquals("ADMIN", newRole.getName());
        assertNull(newRole.getId());
        assertNotNull(newRole.getUsers());
    }

    @Test
    void testGetters() {
        // Test ID
        assertNull(role.getId());

        // Test name
        assertEquals("TEST_ROLE", role.getName());

        // Test users
        assertNotNull(role.getUsers());
        assertEquals(0, role.getUsers().size());
    }

    @Test
    void testEquals() {
        Role role1 = new Role("ROLE1");
        Role role2 = new Role("ROLE2");
        Role role3 = new Role("ROLE1");
        
        // Test equality by name
        assertEquals(role1, role3);
        
        // Test self equality
        assertEquals(role1, role1);
        
        // Test null
        assertNotEquals(role1, null);
        
        // Test different class
        assertNotEquals(role1, "string");
        
        // Test different role
        assertNotEquals(role1, role2);
    }

    @Test
    void testHashCode() {
        Role role1 = new Role("ROLE1");
        Role role2 = new Role("ROLE1");
        
        // Same name should have same hash code
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testToString() {
        Role role = new Role("TEST_ROLE");
        String toString = role.toString();
        
        // Should not be null and should contain class name
        assertNotNull(toString);
        assertTrue(toString.contains("Role"));
    }
} 