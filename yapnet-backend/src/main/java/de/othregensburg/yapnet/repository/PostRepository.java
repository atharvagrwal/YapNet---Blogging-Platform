package de.othregensburg.yapnet.repository;

import de.othregensburg.yapnet.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import de.othregensburg.yapnet.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Optional<Post> findByIdAndUser_Id(UUID postId, UUID userId);
    Optional<Post> findById(UUID postId);
    List<Post> findByUserId(UUID userId);
    List<Post> findByUserIdIn(List<UUID> userIds);
    long countByUser(User user);
}
