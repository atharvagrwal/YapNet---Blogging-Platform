package de.othregensburg.yapnet.controller;

import de.othregensburg.yapnet.dto.CommentDto;
import de.othregensburg.yapnet.dto.CreateCommentDto;
import de.othregensburg.yapnet.service.CommentService;
import de.othregensburg.yapnet.service.SimpleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class CommentController {

    private final CommentService commentService;
    private final SimpleAuthService authService;

    public CommentController(CommentService commentService, SimpleAuthService authService) {
        this.commentService = commentService;
        this.authService = authService;
    }

    /**
     * Create a comment on a post
     * POST /api/posts/{postId}/comments
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable UUID postId, 
                                                  @RequestBody CreateCommentDto createCommentDto,
                                                  @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        if (username == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(commentService.createComment(postId, createCommentDto, username));
    }

    /**
     * Get all comments for a post
     * GET /api/posts/{postId}/comments
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable UUID postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    /**
     * Get a specific comment by ID
     * GET /api/posts/comments/{commentId}
     */
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> getComment(@PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    /**
     * Update a comment (only by the comment author)
     * PUT /api/posts/comments/{commentId}
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable UUID commentId,
                                                  @RequestBody CreateCommentDto createCommentDto,
                                                  @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        if (username == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(commentService.updateComment(commentId, createCommentDto, username));
    }

    /**
     * Delete a comment (only by the comment author)
     * DELETE /api/posts/comments/{commentId}
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId,
                                            @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        if (username == null) {
            return ResponseEntity.status(401).build();
        }
        /*public Page<Comment> getCommentsPaginated(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return commentService.getCommentsByPost(postId, page, size);
    }*/
        commentService.deleteComment(commentId, username);
        return ResponseEntity.noContent().build();
    }
} 