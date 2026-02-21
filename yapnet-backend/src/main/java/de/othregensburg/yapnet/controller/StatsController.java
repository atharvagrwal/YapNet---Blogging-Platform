package de.othregensburg.yapnet.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final UserService userService;
    private final PostService postService;
    private final SimpleAuthService authService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public StatsController(UserService userService, PostService postService, SimpleAuthService authService,
                          UserRepository userRepository, PostRepository postRepository, 
                          CommentRepository commentRepository, LikeRepository likeRepository) {
        this.userService = userService;
        this.postService = postService;
        this.authService = authService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    // Get current user stats
    @GetMapping("/user")
    public ResponseEntity<UserStatsDto> getUserStats(@RequestHeader("Authorization") String token) {
        try {
            String username = authService.extractUsername(token);
            return ResponseEntity.ok(userService.getUserStats(username));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get all users stats
    @GetMapping
    public ResponseEntity<List<UserStatsDto>> getAllUserStats() {
        try {
            List<User> users = userRepository.findAll();
            List<UserStatsDto> allStats = new ArrayList<>();
            
            for (User user : users) {
                allStats.add(buildUserStats(user));
            }
            
            return ResponseEntity.ok(allStats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    // Global platform-wide statistics
    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        try {
            long totalUsers = userRepository.count();
            long totalPosts = postRepository.count();
            long totalComments = commentRepository.count();
            long totalLikes = likeRepository.count();

            Map<String, Object> globalStats = new HashMap<>();
            globalStats.put("totalUsers", totalUsers);
            globalStats.put("totalPosts", totalPosts);
            globalStats.put("totalComments", totalComments);
            globalStats.put("totalLikes", totalLikes);

            return ResponseEntity.ok(globalStats);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "Failed to get global stats");
            return ResponseEntity.status(500).body(errorStats);
        }
    }

    // Users sorted by total likes (descending)
    @GetMapping("/top")
    public ResponseEntity<List<UserStatsDto>> getUsersSortedByLikes() {
        try {
            List<User> users = userRepository.findAll();
            List<UserStatsDto> allStats = new ArrayList<>();
            
            for (User user : users) {
                allStats.add(buildUserStats(user));
            }
            
            allStats.sort((a, b) -> Long.compare(b.getTotalLikes(), a.getTotalLikes()));
            return ResponseEntity.ok(allStats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    // Top 5 users by total likes
    @GetMapping("/top5")
    public ResponseEntity<List<UserStatsDto>> getTop5UsersByLikes() {
        try {
            List<User> users = userRepository.findAll();
            List<UserStatsDto> allStats = new ArrayList<>();
            
            for (User user : users) {
                allStats.add(buildUserStats(user));
            }
            
            allStats.sort((a, b) -> Long.compare(b.getTotalLikes(), a.getTotalLikes()));
            
            if (allStats.size() > 5) {
                allStats = allStats.subList(0, 5);
            }
            
            return ResponseEntity.ok(allStats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    // Most popular users (by followers)
    @GetMapping("/users/popular")
    public ResponseEntity<List<UserDto>> getMostPopularUsers(@RequestParam(defaultValue = "10") int limit, 
                                                           @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            String currentUsername = null;
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    currentUsername = authService.extractUsername(token);
                } catch (Exception e) {
                    // Token is invalid or expired, continue without current user
                }
            }
            
            List<UserDto> users = userService.getMostPopularUsers(limit, currentUsername);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // Most popular posts
    @GetMapping("/posts/popular")
    public ResponseEntity<List<PostDto>> getMostPopularPosts(@RequestParam(defaultValue = "10") int limit) {
        try {
            return ResponseEntity.ok(postService.getMostPopularPosts(limit));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    // Export stats as CSV
    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportStatsCsvAsFile() {
        try {
            List<User> users = userRepository.findAll();
            List<UserStatsDto> stats = new ArrayList<>();
            
            for (User user : users) {
                stats.add(buildUserStats(user));
            }

            StringBuilder csv = new StringBuilder("Username,Posts,Likes,Comments,Followers,Following,AvgLikes,AvgComments\n");
            for (UserStatsDto s : stats) {
                csv.append(s.getUsername()).append(",")
                        .append(s.getPostCount()).append(",")
                        .append(s.getTotalLikes()).append(",")
                        .append(s.getTotalComments()).append(",")
                        .append(s.getFollowerCount()).append(",")
                        .append(s.getFollowingCount()).append(",")
                        .append(String.format("%.2f", s.getAvgLikesPerPost())).append(",")
                        .append(String.format("%.2f", s.getAvgCommentsPerPost())).append("\n");
            }

            byte[] csvBytes = csv.toString().getBytes();
            ByteArrayResource resource = new ByteArrayResource(csvBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user_stats.csv\"")
                    .contentLength(csvBytes.length)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
        /* platform-wide statistics
    @GetMapping("/global")
    public Map<String, Object> getGlobalStats() {
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long totalComments = commentRepository.count();

        long totalLikes = postRepository.findAll().stream()
                .mapToLong(post -> post.getLikedBy().size())
                .sum();

        Map<String, Object> globalStats = new HashMap<>();
        globalStats.put("totalUsers", totalUsers);
        globalStats.put("totalPosts", totalPosts);
        globalStats.put("totalComments", totalComments);
        globalStats.put("totalLikes", totalLikes);

        return globalStats;
    }

    //  Users sorted by total likes (descending)
    @GetMapping("/top")
    public List<UserStatsDto> getUsersSortedByLikes() {
        return userRepository.findAll().stream()
                .map(this::buildUserStats)
                .sorted(Comparator.comparingInt(UserStatsDto::getTotalLikes).reversed())
                .collect(Collectors.toList());
    }

    //  Top 5 users by total likes
    @GetMapping("/top5")
    public List<UserStatsDto> getTop5UsersByLikes() {
        return userRepository.findAll().stream()
                .map(this::buildUserStats)
                .sorted(Comparator.comparingInt(UserStatsDto::getTotalLikes).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    // Filtered stats between dates
    @GetMapping("/filtered")
    public List<UserStatsDto> getStatsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return userRepository.findAll().stream()
                .map(user -> {
                    List<Post> filteredPosts = postRepository.findAllByAuthor(user).stream()
                            .filter(p -> !p.getTimestamp().isBefore(start) && !p.getTimestamp().isAfter(end))
                            .collect(Collectors.toList());

                    int postCount = filteredPosts.size();
                    int totalLikes = filteredPosts.stream().mapToInt(p -> p.getLikedBy().size()).sum();
                    int totalComments = filteredPosts.stream()
                            .mapToInt(p -> commentRepository.findByPostId(p.getId()).size())
                            .sum();

                    return new UserStatsDto(user.getUsername(), postCount, totalLikes, totalComments);
                })
                .collect(Collectors.toList());
    }

//export as csv
    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportStatsCsvAsFile() {
        List<UserStatsDto> stats = getAllUserStats();

        StringBuilder csv = new StringBuilder("Username,Posts,Likes,Comments,AvgLikes,AvgComments\n");
        for (UserStatsDto s : stats) {
            csv.append(s.getUsername()).append(",")
                    .append(s.getPostCount()).append(",")
                    .append(s.getTotalLikes()).append(",")
                    .append(s.getTotalComments()).append(",")
                    .append(s.getAvgLikesPerPost()).append(",")
                    .append(s.getAvgCommentsPerPost()).append("\n");
        }

        byte[] csvBytes = csv.toString().getBytes();
        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user_stats.csv\"")
                .contentLength(csvBytes.length)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }


    // 🔧 Helper method to build UserStatsDto
    private UserStatsDto buildUserStats(User user) {
        List<Post> posts = postRepository.findAllByAuthor(user);

        int postCount = posts.size();
        int totalLikes = posts.stream().mapToInt(p -> p.getLikedBy().size()).sum();
        int totalComments = posts.stream()
                .mapToInt(p -> commentRepository.findByPostId(p.getId()).size())
                .sum();

        return new UserStatsDto(user.getUsername(), postCount, totalLikes, totalComments);
    }
}
*/
    }

    // Helper method to build UserStatsDto
    private UserStatsDto buildUserStats(User user) {
        try {
            // Get basic counts
            long postCount = postRepository.countByUser(user);
            long commentCount = commentRepository.countByUser(user);
            long likeCount = likeRepository.countByUser(user);
            
            // Get follower/following counts
            long followerCount = 0;
            long followingCount = 0;
            try {
                followerCount = userService.getFollowerCount(user.getId());
                followingCount = userService.getFollowingCount(user.getId());
            } catch (Exception e) {
                followerCount = 0;
                followingCount = 0;
            }
            
            // Calculate total likes and comments from user's posts (accurate)
            long totalLikes = 0;
            long totalComments = 0;
            try {
                List<Post> userPosts = postRepository.findByUserId(user.getId());
                for (Post post : userPosts) {
                    totalLikes += likeRepository.countByPostId(post.getId());
                    totalComments += commentRepository.countByPostId(post.getId());
                }
            } catch (Exception e) {
                totalLikes = 0;
                totalComments = 0;
            }

            UserStatsDto dto = new UserStatsDto(user.getUsername(), postCount, commentCount, likeCount, 
                                   followerCount, followingCount, totalLikes, totalComments);
            dto.setUsername(user.getUsername());
            return dto;
        } catch (Exception e) {
            UserStatsDto dto = new UserStatsDto(user.getUsername(), 0, 0, 0, 0, 0, 0, 0);
            dto.setUsername(user.getUsername());
            return dto;
        }
    }
} 