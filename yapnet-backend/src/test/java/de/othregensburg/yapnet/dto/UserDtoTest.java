package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class UserDtoTest {

    private UserDto userDto;
    private UUID testId;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        testId = UUID.randomUUID();
    }

    @Test
    void testUserDtoCreation() {
        assertNotNull(userDto);
    }

    @Test
    void testSetAndGetId() {
        userDto.setId(testId);
        assertEquals(testId, userDto.getId());
    }

    @Test
    void testSetAndGetUsername() {
        String username = "testuser";
        userDto.setUsername(username);
        assertEquals(username, userDto.getUsername());
    }

    @Test
    void testSetAndGetEmail() {
        String email = "test@example.com";
        userDto.setEmail(email);
        assertEquals(email, userDto.getEmail());
    }

    @Test
    void testSetAndGetFullName() {
        String fullName = "Test User";
        userDto.setFullName(fullName);
        assertEquals(fullName, userDto.getFullName());
    }

    @Test
    void testSetAndGetBiography() {
        String bio = "This is a test biography";
        userDto.setBiography(bio);
        assertEquals(bio, userDto.getBiography());
    }

    @Test
    void testSetAndGetProfilePictureUrl() {
        String url = "https://example.com/profile.jpg";
        userDto.setProfilePictureUrl(url);
        assertEquals(url, userDto.getProfilePictureUrl());
    }

    @Test
    void testSetAndGetEnabled() {
        userDto.setEnabled(true);
        assertTrue(userDto.isEnabled());
        
        userDto.setEnabled(false);
        assertFalse(userDto.isEnabled());
    }

    @Test
    void testSetAndGetIsFollowed() {
        userDto.setIsFollowed(true);
        assertTrue(userDto.isFollowed());
        
        userDto.setIsFollowed(false);
        assertFalse(userDto.isFollowed());
    }

    @Test
    void testSetAndGetFollowerCount() {
        long followerCount = 42L;
        userDto.setFollowerCount(followerCount);
        assertEquals(followerCount, userDto.getFollowerCount());
    }

    @Test
    void testUserDtoWithAllFields() {
        String username = "testuser";
        String email = "test@example.com";
        String fullName = "Test User";
        String bio = "Test biography";
        String profileUrl = "https://example.com/pic.jpg";
        long followerCount = 100L;

        userDto.setId(testId);
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setFullName(fullName);
        userDto.setBiography(bio);
        userDto.setProfilePictureUrl(profileUrl);
        userDto.setEnabled(true);
        userDto.setIsFollowed(false);
        userDto.setFollowerCount(followerCount);

        assertEquals(testId, userDto.getId());
        assertEquals(username, userDto.getUsername());
        assertEquals(email, userDto.getEmail());
        assertEquals(fullName, userDto.getFullName());
        assertEquals(bio, userDto.getBiography());
        assertEquals(profileUrl, userDto.getProfilePictureUrl());
        assertTrue(userDto.isEnabled());
        assertFalse(userDto.isFollowed());
        assertEquals(followerCount, userDto.getFollowerCount());
    }

    @Test
    void testUserDtoEquality() {
        UserDto userDto1 = new UserDto();
        UserDto userDto2 = new UserDto();

        userDto1.setId(testId);
        userDto1.setUsername("testuser");
        userDto1.setEmail("test@example.com");

        userDto2.setId(testId);
        userDto2.setUsername("testuser");
        userDto2.setEmail("test@example.com");

        assertEquals(userDto1.getId(), userDto2.getId());
        assertEquals(userDto1.getUsername(), userDto2.getUsername());
        assertEquals(userDto1.getEmail(), userDto2.getEmail());
    }

    @Test
    void testUserDtoWithNullValues() {
        userDto.setId(null);
        userDto.setUsername(null);
        userDto.setEmail(null);
        userDto.setFullName(null);
        userDto.setBiography(null);
        userDto.setProfilePictureUrl(null);

        assertNull(userDto.getId());
        assertNull(userDto.getUsername());
        assertNull(userDto.getEmail());
        assertNull(userDto.getFullName());
        assertNull(userDto.getBiography());
        assertNull(userDto.getProfilePictureUrl());
    }

    @Test
    void testUserDtoDefaultValues() {
        UserDto newUserDto = new UserDto();
        
        assertNull(newUserDto.getId());
        assertNull(newUserDto.getUsername());
        assertNull(newUserDto.getEmail());
        assertNull(newUserDto.getFullName());
        assertNull(newUserDto.getBiography());
        assertNull(newUserDto.getProfilePictureUrl());
        assertFalse(newUserDto.isEnabled());
        assertFalse(newUserDto.isFollowed());
        assertEquals(0L, newUserDto.getFollowerCount());
    }
} 