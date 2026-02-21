package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

class CreatePostDtoTest {

    private CreatePostDto createPostDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        createPostDto = new CreatePostDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCreatePostDtoCreation() {
        assertNotNull(createPostDto);
    }

    @Test
    void testSetAndGetTitle() {
        String title = "Test Post Title";
        createPostDto.setTitle(title);
        assertEquals(title, createPostDto.getTitle());
    }

    @Test
    void testSetAndGetContent() {
        String content = "This is the content of the test post";
        createPostDto.setContent(content);
        assertEquals(content, createPostDto.getContent());
    }

    @Test
    void testValidCreatePostDto() {
        createPostDto.setTitle("Test Post Title");
        createPostDto.setContent("This is the content of the test post");

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations");
    }

    @Test
    void testTitleRequired() {
        createPostDto.setContent("This is the content of the test post");
        // title is null

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for missing title");
        
        boolean hasTitleViolation = violations.stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("title"));
        assertTrue(hasTitleViolation, "Should have title validation violation");
    }

    @Test
    void testContentRequired() {
        createPostDto.setTitle("Test Post Title");
        // content is null

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for missing content");
        
        boolean hasContentViolation = violations.stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("content"));
        assertTrue(hasContentViolation, "Should have content validation violation");
    }

    @Test
    void testEmptyTitle() {
        createPostDto.setTitle("");
        createPostDto.setContent("This is the content of the test post");

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for empty title");
    }

    @Test
    void testEmptyContent() {
        createPostDto.setTitle("Test Post Title");
        createPostDto.setContent("");

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for empty content");
    }

    @Test
    void testBlankTitle() {
        createPostDto.setTitle("   ");
        createPostDto.setContent("This is the content of the test post");

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for blank title");
    }

    @Test
    void testBlankContent() {
        createPostDto.setTitle("Test Post Title");
        createPostDto.setContent("   ");

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for blank content");
    }

    @Test
    void testCreatePostDtoWithAllFields() {
        String title = "Test Post Title";
        String content = "This is the content of the test post";

        createPostDto.setTitle(title);
        createPostDto.setContent(content);

        assertEquals(title, createPostDto.getTitle());
        assertEquals(content, createPostDto.getContent());

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations");
    }

    @Test
    void testCreatePostDtoEquality() {
        CreatePostDto post1 = new CreatePostDto();
        CreatePostDto post2 = new CreatePostDto();

        post1.setTitle("Test Post Title");
        post1.setContent("This is the content of the test post");

        post2.setTitle("Test Post Title");
        post2.setContent("This is the content of the test post");

        assertEquals(post1.getTitle(), post2.getTitle());
        assertEquals(post1.getContent(), post2.getContent());
    }

    @Test
    void testCreatePostDtoWithSpecialCharacters() {
        String title = "Test Post Title with @#$%^&*() characters!";
        String content = "This is the content with special characters: @#$%^&*() and emojis 🎉🚀";

        createPostDto.setTitle(title);
        createPostDto.setContent(content);

        assertEquals(title, createPostDto.getTitle());
        assertEquals(content, createPostDto.getContent());

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations for special characters");
    }

    @Test
    void testCreatePostDtoWithLongValues() {
        String title = "a".repeat(120); // max allowed length
        String content = "b".repeat(500); // max allowed length

        createPostDto.setTitle(title);
        createPostDto.setContent(content);

        assertEquals(title, createPostDto.getTitle());
        assertEquals(content, createPostDto.getContent());

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations for max allowed values");
    }

    @Test
    void testCreatePostDtoDefaultValues() {
        CreatePostDto newPost = new CreatePostDto();
        
        assertNull(newPost.getTitle());
        assertNull(newPost.getContent());
    }

    @Test
    void testCreatePostDtoWithNullValues() {
        createPostDto.setTitle(null);
        createPostDto.setContent(null);

        assertNull(createPostDto.getTitle());
        assertNull(createPostDto.getContent());

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for null values");
    }

    @Test
    void testCreatePostDtoWithMinimalValidContent() {
        createPostDto.setTitle("T"); // Minimum valid title
        createPostDto.setContent("C"); // Minimum valid content

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations for minimal valid content");
    }

    @Test
    void testCreatePostDtoWithNewlines() {
        String title = "Test Post Title";
        String content = "This is the first line.\nThis is the second line.\nThis is the third line.";

        createPostDto.setTitle(title);
        createPostDto.setContent(content);

        assertEquals(title, createPostDto.getTitle());
        assertEquals(content, createPostDto.getContent());

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations for content with newlines");
    }

    @Test
    void testCreatePostDtoWithUnicodeCharacters() {
        String title = "Test Post Title with Unicode: 测试标题 🎉";
        String content = "This is content with Unicode characters: 测试内容 🚀 and emojis 🎊";

        createPostDto.setTitle(title);
        createPostDto.setContent(content);

        assertEquals(title, createPostDto.getTitle());
        assertEquals(content, createPostDto.getContent());

        Set<ConstraintViolation<CreatePostDto>> violations = validator.validate(createPostDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations for Unicode characters");
    }
} 