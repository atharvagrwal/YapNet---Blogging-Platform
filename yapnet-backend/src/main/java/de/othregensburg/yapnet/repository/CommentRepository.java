package de.othregensburg.yapnet.repository;

import de.othregensburg.yapnet.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import de.othregensburg.yapnet.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostIdOrderByCreatedAtDesc(UUID postId);
    List<Comment> findByUserId(UUID userId);
    void deleteByPostId(UUID postId);
    void deleteByUserId(UUID userId);
    long countByUser(User user);
    long countByPostId(UUID postId);
} 