package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserStatsDtoTest {
    @Test
    void testAllArgsConstructorAndGetters() {
        UserStatsDto dto = new UserStatsDto("user", 10, 5, 3, 2, 1, 20, 15);
        assertEquals("user", dto.getUsername());
        assertEquals(10, dto.getPostCount());
        assertEquals(5, dto.getCommentCount());
        assertEquals(3, dto.getLikeCount());
        assertEquals(2, dto.getFollowerCount());
        assertEquals(1, dto.getFollowingCount());
        assertEquals(20, dto.getTotalLikes());
        assertEquals(15, dto.getTotalComments());
        assertEquals(2.0, dto.getAvgLikesPerPost());
        assertEquals(1.5, dto.getAvgCommentsPerPost());
    }

    @Test
    void testBackwardCompatConstructor() {
        UserStatsDto dto = new UserStatsDto(10, 5, 3, 2, 1);
        assertEquals(10, dto.getPostCount());
        assertEquals(5, dto.getCommentCount());
        assertEquals(3, dto.getLikeCount());
        assertEquals(2, dto.getFollowerCount());
        assertEquals(1, dto.getFollowingCount());
        assertEquals(3, dto.getTotalLikes()); // likeCount used for totalLikes
        assertEquals(5, dto.getTotalComments());
        assertEquals(0.3, dto.getAvgLikesPerPost());
        assertEquals(0.5, dto.getAvgCommentsPerPost());
    }

    @Test
    void testSetters() {
        UserStatsDto dto = new UserStatsDto("user", 1, 1, 1, 1, 1, 1, 1);
        dto.setUsername("newuser");
        dto.setPostCount(2);
        dto.setCommentCount(3);
        dto.setLikeCount(4);
        dto.setFollowerCount(5);
        dto.setFollowingCount(6);
        dto.setTotalLikes(7);
        dto.setTotalComments(8);
        dto.setAvgLikesPerPost(9.0);
        dto.setAvgCommentsPerPost(10.0);
        assertEquals("newuser", dto.getUsername());
        assertEquals(2, dto.getPostCount());
        assertEquals(3, dto.getCommentCount());
        assertEquals(4, dto.getLikeCount());
        assertEquals(5, dto.getFollowerCount());
        assertEquals(6, dto.getFollowingCount());
        assertEquals(7, dto.getTotalLikes());
        assertEquals(8, dto.getTotalComments());
        assertEquals(9.0, dto.getAvgLikesPerPost());
        assertEquals(10.0, dto.getAvgCommentsPerPost());
    }

    @Test
    void testZeroPostsEdgeCase() {
        UserStatsDto dto = new UserStatsDto("user", 0, 5, 3, 2, 1, 0, 0);
        assertEquals(0.0, dto.getAvgLikesPerPost());
        assertEquals(0.0, dto.getAvgCommentsPerPost());
    }
} 