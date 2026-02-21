package de.othregensburg.yapnet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.othregensburg.yapnet.dto.UserStatsDto;
import de.othregensburg.yapnet.dto.UserDto;
import de.othregensburg.yapnet.dto.PostDto;
import de.othregensburg.yapnet.model.User;
import de.othregensburg.yapnet.model.Post;
import de.othregensburg.yapnet.service.UserService;
import de.othregensburg.yapnet.service.PostService;
import de.othregensburg.yapnet.service.SimpleAuthService;
import de.othregensburg.yapnet.repository.UserRepository;
import de.othregensburg.yapnet.repository.PostRepository;
import de.othregensburg.yapnet.repository.CommentRepository;
import de.othregensburg.yapnet.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private SimpleAuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    private StatsController statsController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final String username = "testuser";
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        statsController = new StatsController(userService, postService, authService, 
                userRepository, postRepository, commentRepository, likeRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(statsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getUserStats_success() throws Exception {
        UserStatsDto userStats = new UserStatsDto(username, 5L, 10L, 25L, 15L, 8L, 25L, 10L);

        when(authService.extractUsername(anyString())).thenReturn(username);
        when(userService.getUserStats(username)).thenReturn(userStats);

        mockMvc.perform(get("/api/stats/user")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.postCount").value(5))
                .andExpect(jsonPath("$.totalLikes").value(25))
                .andExpect(jsonPath("$.totalComments").value(10))
                .andExpect(jsonPath("$.followerCount").value(15))
                .andExpect(jsonPath("$.followingCount").value(8));
    }



    @Test
    void getAllUserStats_success() throws Exception {
        User user1 = new User("user1", "password123", "user1@example.com");
        User user2 = new User("user2", "password123", "user2@example.com");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }



    @Test
    void getGlobalStats_success() throws Exception {
        when(userRepository.count()).thenReturn(100L);
        when(postRepository.count()).thenReturn(500L);
        when(commentRepository.count()).thenReturn(2000L);
        when(likeRepository.count()).thenReturn(10000L);

        mockMvc.perform(get("/api/stats/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.totalPosts").value(500))
                .andExpect(jsonPath("$.totalComments").value(2000))
                .andExpect(jsonPath("$.totalLikes").value(10000));
    }



    @Test
    void getUsersSortedByLikes_success() throws Exception {
        User user1 = new User("user1", "password123", "user1@example.com");
        User user2 = new User("user2", "password123", "user2@example.com");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/stats/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    void getTop5UsersByLikes_success() throws Exception {
        User user1 = new User("user1", "password123", "user1@example.com");
        User user2 = new User("user2", "password123", "user2@example.com");
        User user3 = new User("user3", "password123", "user3@example.com");
        User user4 = new User("user4", "password123", "user4@example.com");
        User user5 = new User("user5", "password123", "user5@example.com");
        User user6 = new User("user6", "password123", "user6@example.com");
        List<User> users = Arrays.asList(user1, user2, user3, user4, user5, user6);

        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/stats/top5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(5)));
    }

    @Test
    void getMostPopularUsers_success() throws Exception {
        UserDto user1 = new UserDto();
        user1.setUsername("user1");
        user1.setId(userId);
        UserDto user2 = new UserDto();
        user2.setUsername("user2");
        user2.setId(UUID.randomUUID());
        List<UserDto> popularUsers = Arrays.asList(user1, user2);
        when(userService.getMostPopularUsers(10, null)).thenReturn(popularUsers);
        mockMvc.perform(get("/api/stats/users/popular")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }



    @Test
    void getMostPopularPosts_success() throws Exception {
        PostDto post1 = new PostDto();
        post1.setId(UUID.randomUUID());
        post1.setContent("Popular post 1");
        
        PostDto post2 = new PostDto();
        post2.setId(UUID.randomUUID());
        post2.setContent("Popular post 2");
        
        List<PostDto> popularPosts = Arrays.asList(post1, post2);

        when(postService.getMostPopularPosts(10)).thenReturn(popularPosts);

        mockMvc.perform(get("/api/stats/posts/popular")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].content").value("Popular post 1"))
                .andExpect(jsonPath("$[1].content").value("Popular post 2"));
    }



    @Test
    void exportStatsCsvAsFile_success() throws Exception {
        User user = new User("testuser", "password123", "test@example.com");
        user.setId(UUID.randomUUID());
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(postRepository.countByUser(user)).thenReturn(5L);
        when(commentRepository.countByUser(user)).thenReturn(10L);
        when(likeRepository.countByUser(user)).thenReturn(15L);
        when(userService.getFollowerCount(user.getId())).thenReturn(20L);
        when(userService.getFollowingCount(user.getId())).thenReturn(25L);
        when(postRepository.findByUserId(user.getId())).thenReturn(List.of());
        
        mockMvc.perform(get("/api/stats/export/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"user_stats.csv\""))
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    void exportStatsCsvAsFile_exception() throws Exception {
        when(userRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/stats/export/csv"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUserStats_exception() throws Exception {
        when(authService.extractUsername(anyString())).thenThrow(new RuntimeException("Auth error"));
        mockMvc.perform(get("/api/stats/user").header("Authorization", "token"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllUserStats_exception() throws Exception {
        when(userRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getGlobalStats_exception() throws Exception {
        when(userRepository.count()).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/stats/global"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUsersSortedByLikes_exception() throws Exception {
        when(userRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/stats/top"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getTop5UsersByLikes_exception() throws Exception {
        when(userRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        mockMvc.perform(get("/api/stats/top5"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getMostPopularUsers_exception() throws Exception {
        when(userService.getMostPopularUsers(anyInt(), anyString())).thenThrow(new RuntimeException("Service error"));
        mockMvc.perform(get("/api/stats/users/popular"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getMostPopularPosts_exception() throws Exception {
        when(postService.getMostPopularPosts(anyInt())).thenThrow(new RuntimeException("Service error"));
        mockMvc.perform(get("/api/stats/posts/popular"))
                .andExpect(status().isInternalServerError());
    }
} 