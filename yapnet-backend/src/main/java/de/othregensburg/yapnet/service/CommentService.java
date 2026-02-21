package de.othregensburg.yapnet.service;

import de.othregensburg.yapnet.dto.CommentDto;
import de.othregensburg.yapnet.dto.CreateCommentDto;
import de.othregensburg.yapnet.model.Comment;
import de.othregensburg.yapnet.model.Post;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.CommentRepository;
import de.othregensburg.yapnet.repository.PostRepository;
import de.othregensburg.yapnet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public CommentDto createComment(UUID postId, CreateCommentDto createCommentDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(createCommentDto.getContent());
        comment.setUser(user);
        comment.setPost(post);
        
        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    public List<CommentDto> getCommentsByPost(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CommentDto getComment(UUID commentId) {
        return commentRepository.findById(commentId)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public CommentDto updateComment(UUID commentId, CreateCommentDto createCommentDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only edit your own comments");
        }
        
        comment.setContent(createCommentDto.getContent());
        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    public void deleteComment(UUID commentId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own comments");
        }
        
        commentRepository.delete(comment);
    }

    public void deleteCommentsByPost(UUID postId) {
        commentRepository.deleteByPostId(postId);
    }

    public void deleteCommentsByUser(UUID userId) {
        commentRepository.deleteByUserId(userId);
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setPostId(comment.getPost().getId());
        
        if (comment.getUser() != null) {
            dto.setUsername(comment.getUser().getUsername());
            dto.setUserId(comment.getUser().getId());
        }
        
        return dto;
    }
} 