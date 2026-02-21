package de.othregensburg.yapnet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.othregensburg.yapnet.dto.CreatePostDto;
import de.othregensburg.yapnet.dto.PostDto;
import de.othregensburg.yapnet.service.PostService;
import de.othregensburg.yapnet.service.SimpleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @Mock private PostService postService;
    @Mock private SimpleAuthService authService;
    @Mock private UserRepository userRepository;
    private PostController postController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private final UUID postId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        postController = new PostController(postService, authService, userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void createPost_success() throws Exception {
        CreatePostDto createDto = new CreatePostDto();
        createDto.setTitle("Test title");
        createDto.setContent("Test content");
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        User mockUser = new User("user", "password", "user@example.com");
        when(authService.extractUsername("Bearer testtoken")).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(java.util.Optional.of(mockUser));
        when(postService.createPost(any(), any(User.class))).thenReturn(postDto);
        mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer testtoken")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPosts_success() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.getAllPosts(anyString())).thenReturn(List.of(new PostDto()));
        mockMvc.perform(get("/api/posts").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void getMyPosts_success() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.getPostsByUser(anyString())).thenReturn(List.of(new PostDto()));
        mockMvc.perform(get("/api/posts/me").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserPosts_success() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.getPostsByUserId(any(), anyString())).thenReturn(new ArrayList<>(List.of(new PostDto())));
        mockMvc.perform(get("/api/posts/user/" + userId).header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserPosts_withSortingAndLimiting() throws Exception {
        List<PostDto> posts = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            PostDto post = new PostDto();
            post.setId(UUID.randomUUID());
            post.setCreatedAt(LocalDateTime.now().minusDays(i));
            posts.add(post);
        }
        
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.getPostsByUserId(any(), anyString())).thenReturn(posts);
        mockMvc.perform(get("/api/posts/user/" + userId).header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserPosts_emptyList() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.getPostsByUserId(any(), anyString())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/api/posts/user/" + userId).header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void getFeed_success() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.getTimelinePosts(anyString())).thenReturn(List.of(new PostDto()));
        mockMvc.perform(get("/api/posts/feed").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void getFeed_emptyFeed() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.getTimelinePosts(anyString())).thenReturn(List.of());
        mockMvc.perform(get("/api/posts/feed").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void getPost_success() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.getPost(any(), anyString())).thenReturn(new PostDto());
        mockMvc.perform(get("/api/posts/" + postId).header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void updatePost_success() throws Exception {
        CreatePostDto createDto = new CreatePostDto();
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.updatePost(any(), any(), anyString())).thenReturn(postDto);
        mockMvc.perform(put("/api/posts/" + postId)
                .header("Authorization", "token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePost_withContent() throws Exception {
        CreatePostDto createDto = new CreatePostDto();
        createDto.setContent("Updated content");
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.updatePost(any(), any(), anyString())).thenReturn(postDto);
        mockMvc.perform(put("/api/posts/" + postId)
                .header("Authorization", "token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePost_success() throws Exception {
        when(authService.extractUsername(anyString())).thenReturn("user");
        mockMvc.perform(delete("/api/posts/" + postId).header("Authorization", "token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void likePost_success() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.likePost(any(), anyString())).thenReturn(postDto);
        mockMvc.perform(post("/api/posts/" + postId + "/likes").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void unlikePost_success() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        when(authService.extractUsername(anyString())).thenReturn("user");
        when(postService.unlikePost(any(), anyString())).thenReturn(postDto);
        mockMvc.perform(delete("/api/posts/" + postId + "/likes").header("Authorization", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void createPost_nullBody() throws Exception {
        mockMvc.perform(post("/api/posts")
                .header("Authorization", "token")
                .contentType("application/json")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePost_nullBody() throws Exception {
        mockMvc.perform(put("/api/posts/" + postId)
                .header("Authorization", "token")
                .contentType("application/json")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
} 