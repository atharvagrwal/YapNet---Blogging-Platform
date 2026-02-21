package de.othregensburg.yapnet.controller;

import de.othregensburg.yapnet.dto.CreatePostDto;
import de.othregensburg.yapnet.dto.PostDto;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.UserRepository;
import de.othregensburg.yapnet.service.PostService;
import de.othregensburg.yapnet.service.SimpleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final SimpleAuthService authService;
    private final UserRepository userRepository;

    public PostController(PostService postService, SimpleAuthService authService, UserRepository userRepository) {
        this.postService = postService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * Create a new post
     * POST /api/posts
     */
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody @Valid CreatePostDto createPostDto, @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        PostDto postDto = postService.createPost(createPostDto, user);
        return ResponseEntity.ok(postDto);
    }

    /**
     * Get all posts (for browsing, discovery, etc.)
     * GET /api/posts
     */
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(@RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(postService.getAllPosts(username));
    }

    /**
     * Get current user's own posts
     * GET /api/posts/me
     */
    @GetMapping("/me")
    public ResponseEntity<List<PostDto>> getMyPosts(@RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(postService.getPostsByUser(username));
    }

    /**
     * Get posts from a specific user
     * GET /api/posts/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable UUID userId, @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        List<PostDto> posts = postService.getPostsByUserId(userId, username);
        // Sort reverse chronological and limit to 20
        posts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        if (posts.size() > 20) {
            posts = posts.subList(0, 20);
        }
        return ResponseEntity.ok(posts);
    }

    /**
     * Get user's personalized feed (posts from followed users)
     * GET /api/posts/feed
     */
    @GetMapping("/feed")
    public ResponseEntity<List<PostDto>> getFeed(@RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(postService.getTimelinePosts(username));
    }

    /**
     * Get a specific post by ID
     * GET /api/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable UUID postId, @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(postService.getPost(postId, username));

        /*@GetMapping("/search")
    public ResponseEntity<List<PostDto>> searchPosts(@RequestParam String keyword) {
        List<Post> posts = postService.searchPosts(keyword);
        List<PostDto> dtos = posts.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok(dtos);
    }

    // Get paginated posts as Page<PostDto>
    @GetMapping("/paginated")
    public ResponseEntity<Page<PostDto>> getPostsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> postsPage = postService.getPostsPaginated(page, size);
        Page<PostDto> dtoPage = postsPage.map(this::convertToDto);
        return ResponseEntity.ok(dtoPage);
    */
    }

    /**
     * Update a post (only by the post author)
     * PUT /api/posts/{postId}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable UUID postId,
                                            @RequestBody CreatePostDto createPostDto,
                                            @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(postService.updatePost(postId, createPostDto, username));
    }

    /**
     * Delete a post (only by the post author)
     * DELETE /api/posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId, @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        postService.deletePost(postId, username);
        return ResponseEntity.noContent().build();
    }

    /**
     * Like a post
     * POST /api/posts/{postId}/likes
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<PostDto> likePost(@PathVariable UUID postId, @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(postService.likePost(postId, username));
    }

    /**
     * Unlike a post
     * DELETE /api/posts/{postId}/likes
     */
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<PostDto> unlikePost(@PathVariable UUID postId, @RequestHeader("Authorization") String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(postService.unlikePost(postId, username));
    }
}
