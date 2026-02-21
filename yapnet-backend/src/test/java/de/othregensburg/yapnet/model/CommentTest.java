package de.othregensburg.yapnet.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

class CommentTest {

    private Comment comment;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password123", "test@example.com");
        post = new Post("Test Post", "Test content", user);
        comment = new Comment("Test comment", user, post);
    }

    @Test
    void testCommentConstructorWithValidData() {
        Comment validComment = new Comment("Valid comment", user, post);
        
        assertEquals("Valid comment", validComment.getContent());
        assertEquals(user, validComment.getUser());
        assertEquals(post, validComment.getPost());
        assertNotNull(validComment.getCreatedAt());
        assertNotNull(validComment.getUpdatedAt());
    }

    @Test
    void testCommentConstructorWithNullContent() {
        Comment comment = new Comment(null, user, post);
        assertNull(comment.getContent());
    }

    @Test
    void testCommentConstructorWithEmptyContent() {
        Comment comment = new Comment("", user, post);
        assertEquals("", comment.getContent());
    }

    @Test
    void testCommentConstructorWithNullUser() {
        Comment comment = new Comment("Test comment", null, post);
        assertNull(comment.getUser());
    }

    @Test
    void testCommentConstructorWithNullPost() {
        Comment comment = new Comment("Test comment", user, null);
        assertNull(comment.getPost());
    }

    @Test
    void testCommentConstructorTrimsContent() {
        Comment trimmedComment = new Comment("  Test comment  ", user, post);
        
        assertEquals("  Test comment  ", trimmedComment.getContent());
    }

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        comment.setId(id);
        assertEquals(id, comment.getId());

        comment.setContent("New comment");
        assertEquals("New comment", comment.getContent());

        User newUser = new User("newuser", "password123", "new@example.com");
        comment.setUser(newUser);
        assertEquals(newUser, comment.getUser());

        Post newPost = new Post("New Post", "New content", newUser);
        comment.setPost(newPost);
        assertEquals(newPost, comment.getPost());

        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        assertEquals(now, comment.getCreatedAt());

        LocalDateTime updated = LocalDateTime.now().plusHours(1);
        comment.setUpdatedAt(updated);
        assertEquals(updated, comment.getUpdatedAt());
    }

    @Test
    void testSetContentWithNull() {
        comment.setContent(null);
        assertNull(comment.getContent());
    }

    @Test
    void testSetContentWithEmpty() {
        comment.setContent("");
        assertEquals("", comment.getContent());
    }

    @Test
    void testSetUserWithNull() {
        comment.setUser(null);
        assertNull(comment.getUser());
    }

    @Test
    void testSetPostWithNull() {
        comment.setPost(null);
        assertNull(comment.getPost());
    }

    @Test
    void testSetContentTrimsWhitespace() {
        comment.setContent("  New comment  ");
        assertEquals("  New comment  ", comment.getContent());
    }

    @Test
    void testCommentInitialization() {
        assertNotNull(comment.getCreatedAt());
        assertNotNull(comment.getUpdatedAt());
        assertTrue(comment.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(comment.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testCommentWithLongContent() {
        String longContent = "A".repeat(1000); // Long comment
        Comment longComment = new Comment(longContent, user, post);
        assertEquals(longContent, longComment.getContent());
    }

    @Test
    void testCommentWithSpecialCharacters() {
        String specialContent = "Comment with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        Comment specialComment = new Comment(specialContent, user, post);
        assertEquals(specialContent, specialComment.getContent());
    }

    @Test
    void testCommentWithUnicodeCharacters() {
        String unicodeContent = "Comment with unicode: 🚀🌟🎉";
        Comment unicodeComment = new Comment(unicodeContent, user, post);
        assertEquals(unicodeContent, unicodeComment.getContent());
    }
} 