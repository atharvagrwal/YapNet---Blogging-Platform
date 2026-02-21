package de.othregensburg.yapnet.repository;

import de.othregensburg.yapnet.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;
import de.othregensburg.yapnet.model.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByPostIdAndUserId(UUID postId, UUID userId);
    void deleteByPostIdAndUserId(UUID postId, UUID userId);
    void deleteByPostId(UUID postId);
    Long countByPostId(UUID postId);
    Optional<Like> findByUserIdAndPostId(UUID userId, UUID postId);
    long countByUser(User user);
}
