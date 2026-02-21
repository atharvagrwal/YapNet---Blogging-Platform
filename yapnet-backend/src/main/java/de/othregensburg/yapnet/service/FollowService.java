package de.othregensburg.yapnet.service;

import de.othregensburg.yapnet.dto.FollowDto;
import de.othregensburg.yapnet.model.Follow;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.FollowRepository;
import de.othregensburg.yapnet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Autowired
    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public boolean createFollow(FollowDto followDto, User follower) {
        try {
            if (follower == null) {
                throw new RuntimeException("Current user not found");
            }
            
            if (followDto == null || followDto.getFollowingId() == null) {
                throw new RuntimeException("Invalid follow request: missing followingId");
            }

            User following = userRepository.findById(followDto.getFollowingId())
                    .orElseThrow(() -> new RuntimeException("Following user not found"));

            if (follower.getId().equals(following.getId())) {
                throw new RuntimeException("Cannot follow yourself");
            }

            if (followRepository.existsByFollowerIdAndFollowingId(follower.getId(), following.getId())) {
                return false;
            }

            Follow follow = new Follow(follower, following);
            followRepository.save(follow);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create follow: " + e.getMessage(), e);
        }
    }

    public void deleteFollow(UUID followerId, UUID followingId) {
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    public Optional<Long> getFollowerCount(UUID userId) {
        return Optional.of(followRepository.countByFollowingId(userId));
    }

    public Optional<Long> getFollowingCount(UUID userId) {
        return Optional.of(followRepository.countByFollowerId(userId));
    }

    @Transactional
    public FollowDto toggleFollow(UUID targetUserId, User follower) {
        try {
            System.out.println("FollowService.toggleFollow called with targetUserId: " + targetUserId + ", follower: " + follower.getUsername());
            
            if (follower.getId().equals(targetUserId)) {
                throw new IllegalArgumentException("You cannot follow/unfollow yourself.");
            }
            User following = userRepository.findById(targetUserId)
                    .orElseThrow(() -> new RuntimeException("Target user not found"));
            
            System.out.println("Found following user: " + following.getUsername());
            
            boolean alreadyFollowing = followRepository.existsByFollowerIdAndFollowingId(follower.getId(), following.getId());
            System.out.println("Already following: " + alreadyFollowing);
            
            if (alreadyFollowing) {
                System.out.println("Deleting follow relationship");
                followRepository.deleteByFollowerIdAndFollowingId(follower.getId(), following.getId());
                FollowDto result = new FollowDto();
                result.setFollowingId(targetUserId);
                return result;
            } else {
                System.out.println("Creating follow relationship");
                Follow follow = new Follow(follower, following);
                followRepository.save(follow);
                FollowDto result = new FollowDto();
                result.setFollowingId(targetUserId);
                return result;
            }
        } catch (Exception e) {
            System.err.println("Error in toggleFollow: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
