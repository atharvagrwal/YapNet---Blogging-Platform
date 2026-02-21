package de.othregensburg.yapnet.service;

import de.othregensburg.yapnet.dto.PostDto;
import de.othregensburg.yapnet.dto.CreatePostDto;
import de.othregensburg.yapnet.model.Post;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.PostRepository;
import de.othregensburg.yapnet.repository.UserRepository;
import de.othregensburg.yapnet.repository.LikeRepository;
import de.othregensburg.yapnet.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    @Lazy
    private CommentService commentService;

    public PostDto createPost(CreatePostDto createPostDto, User user) {
        Post post = new Post(createPostDto.getTitle(), createPostDto.getContent(), user);
        postRepository.save(post);
        return mapToDto(post, false);
    }

    public List<PostDto> getAllPosts(String username) {
        return postRepository.findAll()
                .stream()
                .map(post -> convertToDto(post, username))
                .collect(Collectors.toList());
    }

    public List<PostDto> getPostsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findByUserId(user.getId())
                .stream()
                .map(post -> convertToDto(post, username))
                .collect(Collectors.toList());
    }

    public PostDto getPost(UUID postId, String username) {
        return postRepository.findById(postId)
                .map(post -> convertToDto(post, username))
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public PostDto updatePost(UUID postId, CreatePostDto createPostDto, String username) {
        return userRepository.findByUsername(username)
                .map(user -> postRepository.findById(postId)
                        .filter(p -> p.getUser().getId().equals(user.getId()))
                        .map(post -> {
                            post.setContent(createPostDto.getContent());
                            return convertToDto(postRepository.save(post), username);
                        })
                        .orElseThrow(() -> new RuntimeException("Post not found or unauthorized")))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deletePost(UUID postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        postRepository.findById(postId)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .ifPresentOrElse(
                    post -> {
                        commentService.deleteCommentsByPost(post.getId());
                        likeRepository.deleteByPostId(post.getId());
                        postRepository.delete(post);
                    },
                    () -> {
                        throw new RuntimeException("Post not found or unauthorized");
                    }
                );
    }

    public PostDto likePost(UUID postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new RuntimeException("You have already liked this post");
        }
        post.incrementLikes();
        // Save Like entity
        likeRepository.save(new de.othregensburg.yapnet.model.Like(user, post));
        return convertToDto(postRepository.save(post), username);
    }

    @Transactional
    public PostDto unlikePost(UUID postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Check if user has liked the post
        if (!likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new RuntimeException("You have not liked this post");
        }
        
        // Delete the like entity
        likeRepository.deleteByPostIdAndUserId(postId, user.getId());
        
        // Decrement likes count
        post.decrementLikes();
        return convertToDto(postRepository.save(post), username);
    }

    public List<PostDto> getPostsByUserId(UUID userId, String username) {
        return postRepository.findByUserId(userId)
                .stream()
                .map(post -> convertToDto(post, username))
                .collect(Collectors.toList());
    }

    public List<PostDto> getTimelinePosts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<UUID> followedIds = followRepository.findByFollowerId(user.getId())
                .stream().map(f -> f.getFollowing().getId()).collect(Collectors.toList());
        if (followedIds.isEmpty()) return List.of();
        List<Post> posts = postRepository.findByUserIdIn(followedIds);
        posts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        if (posts.size() > 20) posts = posts.subList(0, 20);
        return posts.stream().map(p -> convertToDto(p, username)).collect(Collectors.toList());
    }

    public List<PostDto> getMostPopularPosts(int limit) {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
            .sorted((p1, p2) -> Integer.compare(p2.getLikes(), p1.getLikes()))
            .limit(limit)
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    private PostDto convertToDto(Post post) {
        return convertToDto(post, null);
    }

    private PostDto convertToDto(Post post, String username) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setLikes(post.getLikes());
        if (post.getUser() != null) {
            dto.setUsername(post.getUser().getUsername());
            dto.setUserId(post.getUser().getId());
        }
        if (username != null && post.getId() != null) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                dto.setLikedByCurrentUser(likeRepository.findByUserIdAndPostId(user.getId(), post.getId()).isPresent());
            } else {
                dto.setLikedByCurrentUser(false);
            }
        } else {
            dto.setLikedByCurrentUser(false);
        }
        return dto;
    }

    public PostDto mapToDto(Post post, boolean likedByCurrentUser) {
        return new PostDto(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getCreatedAt(),
            post.getUpdatedAt(),
            post.getLikes(),
            post.getUser().getUsername(),
            post.getUser().getId()
        );
    }
}
