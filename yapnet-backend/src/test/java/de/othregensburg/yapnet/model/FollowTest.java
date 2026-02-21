package de.othregensburg.yapnet.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

class FollowTest {

    private Follow follow;
    private User follower;
    private User following;

    @BeforeEach
    void setUp() {
        follower = new User("follower", "password123", "follower@example.com");
        following = new User("following", "password123", "following@example.com");
        follow = new Follow(follower, following);
    }

    @Test
    void testFollowConstructorWithValidData() {
        Follow validFollow = new Follow(follower, following);
        
        assertEquals(follower, validFollow.getFollower());
        assertEquals(following, validFollow.getFollowing());
    }

    @Test
    void testFollowConstructorWithNullFollower() {
        Follow followWithNullFollower = new Follow(null, following);
        assertNull(followWithNullFollower.getFollower());
        assertEquals(following, followWithNullFollower.getFollowing());
    }

    @Test
    void testFollowConstructorWithNullFollowing() {
        Follow followWithNullFollowing = new Follow(follower, null);
        assertEquals(follower, followWithNullFollowing.getFollower());
        assertNull(followWithNullFollowing.getFollowing());
    }

    @Test
    void testGetters() {
        assertEquals(follower, follow.getFollower());
        assertEquals(following, follow.getFollowing());
    }

    @Test
    void testFollowWithSelfFollow() {
        // Test that a user can follow themselves (though this might not be desired in business logic)
        Follow selfFollow = new Follow(follower, follower);
        assertEquals(follower, selfFollow.getFollower());
        assertEquals(follower, selfFollow.getFollowing());
    }

    @Test
    void testFollowWithDifferentUsers() {
        User user1 = new User("user1", "password123", "user1@example.com");
        User user2 = new User("user2", "password123", "user2@example.com");
        
        Follow follow1 = new Follow(user1, user2);
        assertEquals(user1, follow1.getFollower());
        assertEquals(user2, follow1.getFollowing());
        
        Follow follow2 = new Follow(user2, user1);
        assertEquals(user2, follow2.getFollower());
        assertEquals(user1, follow2.getFollowing());
    }

    @Test
    void testDefaultConstructor() {
        Follow defaultFollow = new Follow();
        assertNotNull(defaultFollow);
    }
} 