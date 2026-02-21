package de.othregensburg.yapnet;

import de.othregensburg.yapnet.dto.CreatePostDto;
import de.othregensburg.yapnet.dto.PostDto;
import de.othregensburg.yapnet.model.Post;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.PostRepository;
import de.othregensburg.yapnet.repository.UserRepository;
import de.othregensburg.yapnet.repository.LikeRepository;
import de.othregensburg.yapnet.repository.FollowRepository;
import de.othregensburg.yapnet.service.CommentService;
import de.othregensburg.yapnet.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private LikeRepository likeRepository;
    @Mock private CommentService commentService;
    @Mock private FollowRepository followRepository;
    @InjectMocks private PostService postService;

    private User testUser;
    private Post testPost;
    private CreatePostDto testCreateDto;
    private UUID testPostId;

    @BeforeEach
    void setUp() {
        testPostId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        testUser = new User("testuser", "password", "test@example.com");
        testUser.setId(userId);

        testPost = new Post();
        testPost.setId(testPostId);
        testPost.setTitle("Test title");
        testPost.setContent("Test post content");
        testPost.setUser(testUser);
        testPost.setCreatedAt(LocalDateTime.now());
        testPost.setUpdatedAt(LocalDateTime.now());
        testPost.setLikes(0);

        testCreateDto = new CreatePostDto();
        testCreateDto.setTitle("Test title");
        testCreateDto.setContent("Test post content");
    }

    @Test
    void testCreatePost() {
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        PostDto result = postService.createPost(testCreateDto, testUser);

        assertNotNull(result);
        assertEquals("Test post content", result.getContent());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetPostsByUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findByUserId(testUser.getId())).thenReturn(List.of(testPost));

        List<PostDto> result = postService.getPostsByUser("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPostId, result.get(0).getId());
    }

    @Test
    void testGetPost() {
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));

        PostDto result = postService.getPost(testPostId, "testuser");

        assertNotNull(result);
        assertEquals(testPostId, result.getId());
    }

    @Test
    void testUpdatePost() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        PostDto result = postService.updatePost(testPostId, testCreateDto, "testuser");

        assertNotNull(result);
        assertEquals("Test post content", result.getContent());
    }

    @Test
    void testDeletePost() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));

        postService.deletePost(testPostId, "testuser");

        verify(postRepository).delete(testPost);
    }

    @Test
    void testLikePost() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(likeRepository.existsByPostIdAndUserId(testPostId, testUser.getId())).thenReturn(false);

        PostDto result = postService.likePost(testPostId, "testuser");

        assertNotNull(result);
        assertEquals(1, result.getLikes());
    }

    @Test
    void testUnlikePost() {
        testPost.setLikes(1);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(likeRepository.existsByPostIdAndUserId(testPostId, testUser.getId())).thenReturn(true);

        PostDto result = postService.unlikePost(testPostId, "testuser");

        assertNotNull(result);
        assertEquals(0, result.getLikes());
    }

    @Test
    void testGetAllPosts() {
        when(postRepository.findAll()).thenReturn(List.of(testPost));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        List<PostDto> result = postService.getAllPosts("testuser");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPostId, result.get(0).getId());
    }

    @Test
    void testGetPostsByUserId() {
        when(postRepository.findByUserId(testUser.getId())).thenReturn(List.of(testPost));
        List<PostDto> result = postService.getPostsByUserId(testUser.getId(), "testuser");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPostId, result.get(0).getId());
    }

    @Test
    void testGetTimelinePosts() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(followRepository.findByFollowerId(testUser.getId())).thenReturn(List.of());
        List<PostDto> result = postService.getTimelinePosts("testuser");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTimelinePostsWithFollowedUsers() {
        User followed = new User("followed", "password", "f@e.com");
        followed.setId(UUID.randomUUID());
        de.othregensburg.yapnet.model.Follow follow = mock(de.othregensburg.yapnet.model.Follow.class);
        when(follow.getFollowing()).thenReturn(followed);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(followRepository.findByFollowerId(testUser.getId())).thenReturn(List.of(follow));
        Post followedPost = new Post();
        followedPost.setId(UUID.randomUUID());
        followedPost.setUser(followed);
        followedPost.setContent("Followed's post");
        followedPost.setCreatedAt(LocalDateTime.now());
        when(postRepository.findByUserIdIn(any())).thenReturn(new java.util.ArrayList<>(List.of(followedPost)));
        List<PostDto> result = postService.getTimelinePosts("testuser");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Followed's post", result.get(0).getContent());
    }

    @Test
    void testGetMostPopularPosts() {
        Post post2 = new Post();
        post2.setId(UUID.randomUUID());
        post2.setLikes(10);
        post2.setContent("Popular post");
        post2.setCreatedAt(LocalDateTime.now());
        when(postRepository.findAll()).thenReturn(List.of(testPost, post2));
        List<PostDto> result = postService.getMostPopularPosts(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Popular post", result.get(0).getContent());
    }

    @Test
    void testCreatePostUserNotFound() {
        assertThrows(RuntimeException.class, () -> postService.createPost(testCreateDto, (User) null));
    }

    @Test
    void testGetPostsByUserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.getPostsByUser("nouser"));
    }

    @Test
    void testGetPostNotFound() {
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.getPost(testPostId, "testuser"));
    }

    @Test
    void testUpdatePostUserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.updatePost(testPostId, testCreateDto, "nouser"));
    }

    @Test
    void testUpdatePostNotFoundOrUnauthorized() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.updatePost(testPostId, testCreateDto, "testuser"));
    }

    @Test
    void testDeletePostUserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.deletePost(testPostId, "nouser"));
    }

    @Test
    void testDeletePostNotFoundOrUnauthorized() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.deletePost(testPostId, "testuser"));
    }

    @Test
    void testLikePostAlreadyLiked() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByPostIdAndUserId(testPostId, testUser.getId())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> postService.likePost(testPostId, "testuser"));
    }

    @Test
    void testLikePostUserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.likePost(testPostId, "nouser"));
    }

    @Test
    void testLikePostNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.likePost(testPostId, "testuser"));
    }

    @Test
    void testUnlikePostNotLiked() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByPostIdAndUserId(testPostId, testUser.getId())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> postService.unlikePost(testPostId, "testuser"));
    }

    @Test
    void testUnlikePostUserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.unlikePost(testPostId, "nouser"));
    }

    @Test
    void testUnlikePostNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.unlikePost(testPostId, "testuser"));
    }

    @Test
    void testConvertToDtoBranches() {
        // Null user
        Post post = new Post();
        post.setId(UUID.randomUUID());
        post.setContent("No user");
        post.setCreatedAt(LocalDateTime.now());
        post.setLikes(0);
        PostDto dto = invokeConvertToDto(post, null);
        assertNotNull(dto);
        assertEquals("No user", dto.getContent());
        assertNull(dto.getUsername());
        assertFalse(dto.isLikedByCurrentUser());
        // likedByCurrentUser true
        post.setUser(testUser);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(likeRepository.findByUserIdAndPostId(testUser.getId(), post.getId())).thenReturn(Optional.of(new de.othregensburg.yapnet.model.Like(testUser, post)));
        dto = invokeConvertToDto(post, "testuser");
        assertTrue(dto.isLikedByCurrentUser());
        // likedByCurrentUser false
        when(likeRepository.findByUserIdAndPostId(testUser.getId(), post.getId())).thenReturn(Optional.empty());
        dto = invokeConvertToDto(post, "testuser");
        assertFalse(dto.isLikedByCurrentUser());
    }

    // Helper to invoke private convertToDto
    private PostDto invokeConvertToDto(Post post, String username) {
        try {
            java.lang.reflect.Method m = PostService.class.getDeclaredMethod("convertToDto", Post.class, String.class);
            m.setAccessible(true);
            return (PostDto) m.invoke(postService, post, username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
