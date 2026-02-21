package de.othregensburg.yapnet;

import de.othregensburg.yapnet.dto.FollowDto;
import de.othregensburg.yapnet.model.Follow;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.FollowRepository;
import de.othregensburg.yapnet.repository.UserRepository;
import de.othregensburg.yapnet.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock private FollowRepository followRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private FollowService followService;

    private User follower;
    private User following;
    private FollowDto followDto;
    private UUID followerId;
    private UUID followingId;

    @BeforeEach
    void setUp() {
        followerId = UUID.randomUUID();
        followingId = UUID.randomUUID();
        
        follower = new User("follower", "password", "follower@example.com");
        follower.setId(followerId);
        
        following = new User("following", "password", "following@example.com");
        following.setId(followingId);
        
        followDto = new FollowDto();
        followDto.setFollowingId(followingId);
    }

    @Test
    void testCreateFollowSuccess() {
        when(userRepository.findById(followingId)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenReturn(new Follow(follower, following));

        boolean result = followService.createFollow(followDto, follower);

        assertTrue(result);
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void testCreateFollowAlreadyFollowing() {
        when(userRepository.findById(followingId)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(true);

        boolean result = followService.createFollow(followDto, follower);

        assertFalse(result);
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    void testCreateFollowSelf() {
        followDto.setFollowingId(follower.getId());

        assertThrows(RuntimeException.class, () -> 
            followService.createFollow(followDto, follower));
    }

    @Test
    void testDeleteFollow() {
        followService.deleteFollow(followerId, followingId);

        verify(followRepository).deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Test
    void testGetFollowerCount() {
        when(followRepository.countByFollowingId(followingId)).thenReturn(5L);

        Optional<Long> result = followService.getFollowerCount(followingId);

        assertTrue(result.isPresent());
        assertEquals(5L, result.get());
    }

    @Test
    void testGetFollowingCount() {
        when(followRepository.countByFollowerId(followerId)).thenReturn(3L);

        Optional<Long> result = followService.getFollowingCount(followerId);

        assertTrue(result.isPresent());
        assertEquals(3L, result.get());
    }

    @Test
    void testToggleFollowCreate() {
        when(userRepository.findById(followingId)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenReturn(new Follow(follower, following));

        FollowDto result = followService.toggleFollow(followingId, follower);

        assertNotNull(result);
        assertEquals(followingId, result.getFollowingId());
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void testToggleFollowDelete() {
        when(userRepository.findById(followingId)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(true);

        FollowDto result = followService.toggleFollow(followingId, follower);

        assertNotNull(result);
        assertEquals(followingId, result.getFollowingId());
        verify(followRepository).deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Test
    void testToggleFollowSelf() {
        assertThrows(IllegalArgumentException.class, () -> 
            followService.toggleFollow(follower.getId(), follower));
    }

    @Test
    void testCreateFollowNullFollower() {
        assertThrows(RuntimeException.class, () -> 
            followService.createFollow(followDto, null));
    }

    @Test
    void testCreateFollowNullFollowDto() {
        assertThrows(RuntimeException.class, () -> 
            followService.createFollow(null, follower));
    }

    @Test
    void testCreateFollowNullFollowingId() {
        followDto.setFollowingId(null);
        assertThrows(RuntimeException.class, () -> 
            followService.createFollow(followDto, follower));
    }

    @Test
    void testCreateFollowUserNotFound() {
        when(userRepository.findById(followingId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> 
            followService.createFollow(followDto, follower));
    }

    @Test
    void testCreateFollowExceptionHandling() {
        when(userRepository.findById(followingId)).thenThrow(new RuntimeException("Database error"));
        
        assertThrows(RuntimeException.class, () -> 
            followService.createFollow(followDto, follower));
    }

    @Test
    void testToggleFollowNullFollower() {
        assertThrows(RuntimeException.class, () -> 
            followService.toggleFollow(followingId, null));
    }

    @Test
    void testToggleFollowUserNotFound() {
        when(userRepository.findById(followingId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> 
            followService.toggleFollow(followingId, follower));
    }

    @Test
    void testToggleFollowExceptionHandling() {
        when(userRepository.findById(followingId)).thenThrow(new RuntimeException("Database error"));
        
        assertThrows(RuntimeException.class, () -> 
            followService.toggleFollow(followingId, follower));
    }

    @Test
    void testGetFollowerCountZero() {
        when(followRepository.countByFollowingId(followingId)).thenReturn(0L);

        Optional<Long> result = followService.getFollowerCount(followingId);

        assertTrue(result.isPresent());
        assertEquals(0L, result.get());
    }

    @Test
    void testGetFollowingCountZero() {
        when(followRepository.countByFollowerId(followerId)).thenReturn(0L);

        Optional<Long> result = followService.getFollowingCount(followerId);

        assertTrue(result.isPresent());
        assertEquals(0L, result.get());
    }

    @Test
    void testCreateFollowWithExceptionInSave() {
        when(userRepository.findById(followingId)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenThrow(new RuntimeException("Save failed"));

        assertThrows(RuntimeException.class, () -> 
            followService.createFollow(followDto, follower));
    }

    @Test
    void testToggleFollowWithExceptionInSave() {
        when(userRepository.findById(followingId)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenThrow(new RuntimeException("Save failed"));

        assertThrows(RuntimeException.class, () -> 
            followService.toggleFollow(followingId, follower));
    }

    @Test
    void testToggleFollowWithExceptionInDelete() {
        when(userRepository.findById(followingId)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(true);
        doThrow(new RuntimeException("Delete failed")).when(followRepository).deleteByFollowerIdAndFollowingId(followerId, followingId);

        assertThrows(RuntimeException.class, () -> 
            followService.toggleFollow(followingId, follower));
    }
} 