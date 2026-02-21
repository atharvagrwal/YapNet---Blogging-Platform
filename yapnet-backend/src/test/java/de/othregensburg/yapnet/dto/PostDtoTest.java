package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PostDtoTest {
    @Test
    void testAllArgsConstructorAndGettersSetters() {
        PostDto dto = new PostDto();
        UUID id = UUID.randomUUID();
        dto.setId(id);
        dto.setContent("content");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setUserId(UUID.randomUUID());
        dto.setUsername("user");
        dto.setLikes(5);
        dto.setLikedByCurrentUser(true);
        assertEquals(id, dto.getId());
        assertEquals("content", dto.getContent());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
        assertNotNull(dto.getUserId());
        assertEquals("user", dto.getUsername());
        assertEquals(5, dto.getLikes());
        assertTrue(dto.isLikedByCurrentUser());
    }

    @Test
    void testDefaultValues() {
        PostDto dto = new PostDto();
        assertNull(dto.getId());
        assertNull(dto.getContent());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getUserId());
        assertNull(dto.getUsername());
        assertEquals(0, dto.getLikes());
        assertFalse(dto.isLikedByCurrentUser());
    }
} 