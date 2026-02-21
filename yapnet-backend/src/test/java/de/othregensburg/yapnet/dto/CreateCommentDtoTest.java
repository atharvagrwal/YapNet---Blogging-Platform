package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateCommentDtoTest {
    @Test
    void testAllArgsConstructorAndGettersSetters() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setContent("comment");
        assertEquals("comment", dto.getContent());
    }

    @Test
    void testDefaultValues() {
        CreateCommentDto dto = new CreateCommentDto();
        assertNull(dto.getContent());
    }
} 