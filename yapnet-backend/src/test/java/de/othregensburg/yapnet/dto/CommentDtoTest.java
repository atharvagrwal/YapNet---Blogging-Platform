package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CommentDtoTest {
    @Test
    void testAllArgsConstructorAndGettersSetters() {
        CommentDto dto = new CommentDto();
        UUID id = UUID.randomUUID();
        dto.setId(id);
        dto.setContent("comment");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setUserId(UUID.randomUUID());
        dto.setUsername("user");
        dto.setPostId(UUID.randomUUID());
        assertEquals(id, dto.getId());
        assertEquals("comment", dto.getContent());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
        assertNotNull(dto.getUserId());
        assertEquals("user", dto.getUsername());
        assertNotNull(dto.getPostId());
    }

    @Test
    void testDefaultValues() {
        CommentDto dto = new CommentDto();
        assertNull(dto.getId());
        assertNull(dto.getContent());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getUserId());
        assertNull(dto.getUsername());
        assertNull(dto.getPostId());
    }
} 