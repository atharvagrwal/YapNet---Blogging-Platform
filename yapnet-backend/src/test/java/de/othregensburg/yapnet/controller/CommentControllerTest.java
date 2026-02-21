package de.othregensburg.yapnet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.othregensburg.yapnet.dto.CommentDto;
import de.othregensburg.yapnet.dto.CreateCommentDto;
import de.othregensburg.yapnet.service.CommentService;
import de.othregensburg.yapnet.service.SimpleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private SimpleAuthService authService;

    private CommentController commentController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final UUID postId = UUID.randomUUID();
    private final UUID commentId = UUID.randomUUID();
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        commentController = new CommentController(commentService, authService);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createComment_success() throws Exception {
        CreateCommentDto createDto = new CreateCommentDto();
        createDto.setContent("Test comment");
        
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setContent("Test comment");
        commentDto.setUsername(username);

        when(authService.extractUsername(anyString())).thenReturn(username);
        when(commentService.createComment(eq(postId), any(CreateCommentDto.class), eq(username)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("Test comment"))
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void getCommentsByPost_success() throws Exception {
        CommentDto comment1 = new CommentDto();
        comment1.setId(commentId);
        comment1.setContent("Comment 1");
        comment1.setUsername(username);

        CommentDto comment2 = new CommentDto();
        comment2.setId(UUID.randomUUID());
        comment2.setContent("Comment 2");
        comment2.setUsername("user2");

        List<CommentDto> comments = Arrays.asList(comment1, comment2);

        when(commentService.getCommentsByPost(postId)).thenReturn(comments);

        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(commentId.toString()))
                .andExpect(jsonPath("$[0].content").value("Comment 1"))
                .andExpect(jsonPath("$[1].id").value(comment2.getId().toString()))
                .andExpect(jsonPath("$[1].content").value("Comment 2"));
    }

    @Test
    void getComment_success() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setContent("Test comment");
        commentDto.setUsername(username);

        when(commentService.getComment(commentId)).thenReturn(commentDto);

        mockMvc.perform(get("/api/posts/comments/{commentId}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("Test comment"))
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void updateComment_success() throws Exception {
        CreateCommentDto updateDto = new CreateCommentDto();
        updateDto.setContent("Updated comment");
        
        CommentDto updatedComment = new CommentDto();
        updatedComment.setId(commentId);
        updatedComment.setContent("Updated comment");
        updatedComment.setUsername(username);

        when(authService.extractUsername(anyString())).thenReturn(username);
        when(commentService.updateComment(eq(commentId), any(CreateCommentDto.class), eq(username)))
                .thenReturn(updatedComment);

        mockMvc.perform(put("/api/posts/comments/{commentId}", commentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("Updated comment"));
    }

    @Test
    void deleteComment_success() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn(username);
        doNothing().when(commentService).deleteComment(commentId, username);

        mockMvc.perform(delete("/api/posts/comments/{commentId}", commentId)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createComment_missingAuthorization() throws Exception {
        CreateCommentDto createDto = new CreateCommentDto();
        createDto.setContent("Test comment");

        when(authService.extractUsername(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))
                .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateComment_missingAuthorization() throws Exception {
        CreateCommentDto updateDto = new CreateCommentDto();
        updateDto.setContent("Updated comment");

        when(authService.extractUsername(anyString())).thenReturn(null);

        mockMvc.perform(put("/api/posts/comments/{commentId}", commentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteComment_missingAuthorization() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn(null);

        mockMvc.perform(delete("/api/posts/comments/{commentId}", commentId)
                .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }
} 