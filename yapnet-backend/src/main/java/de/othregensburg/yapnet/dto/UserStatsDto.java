package de.othregensburg.yapnet.dto;

public class UserStatsDto {
    private String username;
    private long postCount;
    private long commentCount;
    private long likeCount;
    private long followerCount;
    private long followingCount;
    private long totalLikes;
    private long totalComments;
    private double avgLikesPerPost;
    private double avgCommentsPerPost;

    public UserStatsDto(String username, long postCount, long commentCount, long likeCount, 
                       long followerCount, long followingCount, long totalLikes, long totalComments) {
        this.username = username;
        this.postCount = postCount;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.totalLikes = totalLikes;
        this.totalComments = totalComments;
        this.avgLikesPerPost = postCount > 0 ? (double) totalLikes / postCount : 0.0;
        this.avgCommentsPerPost = postCount > 0 ? (double) totalComments / postCount : 0.0;
    }

    // Constructor for backward compatibility
    public UserStatsDto(long postCount, long commentCount, long likeCount, long followerCount, long followingCount) {
        this.username = null; // Set to null for backward compatibility
        this.postCount = postCount;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.totalLikes = likeCount; // Assuming likeCount is total likes for backward compatibility
        this.totalComments = commentCount;
        this.avgLikesPerPost = postCount > 0 ? (double) totalLikes / postCount : 0.0;
        this.avgCommentsPerPost = postCount > 0 ? (double) totalComments / postCount : 0.0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPostCount() {
        return postCount;
    }

    public void setPostCount(long postCount) {
        this.postCount = postCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public double getAvgLikesPerPost() {
        return avgLikesPerPost;
    }

    public void setAvgLikesPerPost(double avgLikesPerPost) {
        this.avgLikesPerPost = avgLikesPerPost;
    }

    public double getAvgCommentsPerPost() {
        return avgCommentsPerPost;
    }

    public void setAvgCommentsPerPost(double avgCommentsPerPost) {
        this.avgCommentsPerPost = avgCommentsPerPost;
    }
}