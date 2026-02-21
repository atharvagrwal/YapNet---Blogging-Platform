package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UpdateUserProfileDtoTest {
    @Test
    void testAllArgsConstructorAndGettersSetters() {
        UpdateUserProfileDto dto = new UpdateUserProfileDto();
        dto.setFullName("Full Name");
        dto.setBiography("Bio");
        dto.setProfilePictureUrl("url");
        assertEquals("Full Name", dto.getFullName());
        assertEquals("Bio", dto.getBiography());
        assertEquals("url", dto.getProfilePictureUrl());
    }

    @Test
    void testDefaultValues() {
        UpdateUserProfileDto dto = new UpdateUserProfileDto();
        assertNull(dto.getFullName());
        assertNull(dto.getBiography());
        assertNull(dto.getProfilePictureUrl());
    }
} 