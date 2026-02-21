package de.othregensburg.yapnet;

import de.othregensburg.yapnet.dto.CommentDto;
import de.othregensburg.yapnet.dto.CreateCommentDto;
import de.othregensburg.yapnet.model.Comment;
import de.othregensburg.yapnet.model.Post;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.CommentRepository;
import de.othregensburg.yapnet.repository.PostRepository;
import de.othregensburg.yapnet.repository.UserRepository;
import de.othregensburg.yapnet.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private CommentService commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private CreateCommentDto testCreateDto;
    private UUID commentId;
    private UUID postId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        commentId = UUID.randomUUID();
        postId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        testUser = new User("testuser", "password", "test@example.com");
        testUser.setId(userId);
        
        testPost = new Post();
        testPost.setId(postId);
        testPost.setContent("Test post");
        testPost.setUser(testUser);
        
        testComment = new Comment();
        testComment.setId(commentId);
        testComment.setContent("Test comment");
        testComment.setUser(testUser);
        testComment.setPost(testPost);
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setUpdatedAt(LocalDateTime.now());
        
        testCreateDto = new CreateCommentDto();
        testCreateDto.setContent("Test comment");
    }

    @Test
    void testCreateComment() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentDto result = commentService.createComment(postId, testCreateDto, "testuser");

        assertNotNull(result);
        assertEquals("Test comment", result.getContent());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetCommentsByPost() {
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(postId)).thenReturn(List.of(testComment));

        List<CommentDto> result = commentService.getCommentsByPost(postId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(commentId, result.get(0).getId());
    }

    @Test
    void testGetComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));

        CommentDto result = commentService.getComment(commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
        assertEquals("Test comment", result.getContent());
    }

    @Test
    void testUpdateComment() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentDto result = commentService.updateComment(commentId, testCreateDto, "testuser");

        assertNotNull(result);
        assertEquals("Test comment", result.getContent());
    }

    @Test
    void testDeleteComment() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));

        commentService.deleteComment(commentId, "testuser");

        verify(commentRepository).delete(testComment);
    }

    @Test
    void testDeleteCommentsByPost() {
        commentService.deleteCommentsByPost(postId);

        verify(commentRepository).deleteByPostId(postId);
    }

    @Test
    void testDeleteCommentsByUser() {
        commentService.deleteCommentsByUser(userId);

        verify(commentRepository).deleteByUserId(userId);
    }

    @Test
    void testCreateCommentUserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> commentService.createComment(postId, testCreateDto, "nouser"));
    }

    @Test
    void testCreateCommentPostNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> commentService.createComment(postId, testCreateDto, "testuser"));
    }

    @Test
    void testGetCommentNotFound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> commentService.getComment(commentId));
    }

    @Test
    void testUpdateCommentUserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> commentService.updateComment(commentId, testCreateDto, "nouser"));
    }

    @Test
    void testUpdateCommentNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> commentService.updateComment(commentId, testCreateDto, "testuser"));
    }

    @Test
    void testUpdateCommentUnauthorized() {
        User otherUser = new User("otheruser", "password", "other@example.com");
        otherUser.setId(UUID.randomUUID());
        
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
        
        assertThrows(RuntimeException.class, () -> commentService.updateComment(commentId, testCreateDto, "otheruser"));
    }

    @Test
    void testDeleteCommentUserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> commentService.deleteComment(commentId, "nouser"));
    }

    @Test
    void testDeleteCommentNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> commentService.deleteComment(commentId, "testuser"));
    }

    @Test
    void testDeleteCommentUnauthorized() {
        User otherUser = new User("otheruser", "password", "other@example.com");
        otherUser.setId(UUID.randomUUID());
        
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
        
        assertThrows(RuntimeException.class, () -> commentService.deleteComment(commentId, "otheruser"));
    }

    @Test
    void testConvertToDtoBranches() {
        // Test with null user
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setContent("No user comment");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setPost(testPost);
        comment.setUser(null);
        
        CommentDto dto = invokeConvertToDto(comment);
        assertNotNull(dto);
        assertEquals("No user comment", dto.getContent());
        assertNull(dto.getUsername());
        assertNull(dto.getUserId());
        
        // Test with user
        comment.setUser(testUser);
        dto = invokeConvertToDto(comment);
        assertNotNull(dto);
        assertEquals("testuser", dto.getUsername());
        assertEquals(userId, dto.getUserId());
    }

    // Helper to invoke private convertToDto
    private CommentDto invokeConvertToDto(Comment comment) {
        try {
            java.lang.reflect.Method m = CommentService.class.getDeclaredMethod("convertToDto", Comment.class);
            m.setAccessible(true);
            return (CommentDto) m.invoke(commentService, comment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 