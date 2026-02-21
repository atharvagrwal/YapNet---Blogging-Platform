package de.othregensburg.yapnet.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

class PostTest {

    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password123", "test@example.com");
        post = new Post("Test Title", "Test content", user);
    }

    @Test
    void testPostConstructorWithValidData() {
        Post validPost = new Post("Valid Title", "Valid content", user);
        
        assertEquals("Valid Title", validPost.getTitle());
        assertEquals("Valid content", validPost.getContent());
        assertEquals(user, validPost.getUser());
        assertNotNull(validPost.getCreatedAt());
        assertNotNull(validPost.getUpdatedAt());
    }

    @Test
    void testPostConstructorWithNullTitle() {
        Post post = new Post(null, "Test content", user);
        assertNull(post.getTitle());
    }

    @Test
    void testPostConstructorWithEmptyTitle() {
        Post post = new Post("", "Test content", user);
        assertEquals("", post.getTitle());
    }

    @Test
    void testPostConstructorWithNullContent() {
        Post post = new Post("Test Title", null, user);
        assertNull(post.getContent());
    }

    @Test
    void testPostConstructorWithEmptyContent() {
        Post post = new Post("Test Title", "", user);
        assertEquals("", post.getContent());
    }

    @Test
    void testPostConstructorWithNullUser() {
        Post post = new Post("Test Title", "Test content", null);
        assertNull(post.getUser());
    }

    @Test
    void testPostConstructorTrimsTitleAndContent() {
        Post trimmedPost = new Post("  Test Title  ", "  Test content  ", user);
        
        assertEquals("  Test Title  ", trimmedPost.getTitle());
        assertEquals("  Test content  ", trimmedPost.getContent());
    }

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        post.setId(id);
        assertEquals(id, post.getId());

        post.setTitle("New Title");
        assertEquals("New Title", post.getTitle());

        post.setContent("New content");
        assertEquals("New content", post.getContent());

        User newUser = new User("newuser", "password123", "new@example.com");
        post.setUser(newUser);
        assertEquals(newUser, post.getUser());

        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        assertEquals(now, post.getCreatedAt());

        LocalDateTime updated = LocalDateTime.now().plusHours(1);
        post.setUpdatedAt(updated);
        assertEquals(updated, post.getUpdatedAt());
    }

    @Test
    void testSetTitleWithNull() {
        post.setTitle(null);
        assertNull(post.getTitle());
    }

    @Test
    void testSetTitleWithEmpty() {
        post.setTitle("");
        assertEquals("", post.getTitle());
    }

    @Test
    void testSetContentWithNull() {
        post.setContent(null);
        assertNull(post.getContent());
    }

    @Test
    void testSetContentWithEmpty() {
        post.setContent("");
        assertEquals("", post.getContent());
    }

    @Test
    void testSetUserWithNull() {
        post.setUser(null);
        assertNull(post.getUser());
    }

    @Test
    void testSetTitleTrimsWhitespace() {
        post.setTitle("  New Title  ");
        assertEquals("  New Title  ", post.getTitle());
    }

    @Test
    void testSetContentTrimsWhitespace() {
        post.setContent("  New content  ");
        assertEquals("  New content  ", post.getContent());
    }

    @Test
    void testPostInitialization() {
        assertNotNull(post.getCreatedAt());
        assertNotNull(post.getUpdatedAt());
        assertTrue(post.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(post.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testPostWithLongTitle() {
        String longTitle = "A".repeat(255); // Maximum reasonable length
        Post longTitlePost = new Post(longTitle, "Content", user);
        assertEquals(longTitle, longTitlePost.getTitle());
    }

    @Test
    void testPostWithLongContent() {
        String longContent = "A".repeat(1000); // Long content
        Post longContentPost = new Post("Title", longContent, user);
        assertEquals(longContent, longContentPost.getContent());
    }
} 