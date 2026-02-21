package de.othregensburg.yapnet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    private String fullName;
    private String biography;
    private String profilePictureUrl;
    private boolean enabled;
    @JsonProperty("isFollowed")
    private boolean isFollowed;
    private long followerCount;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getBiography() {
        return biography;
    }
    public void setBiography(String biography) {
        this.biography = biography;
    }
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public boolean isFollowed() {
        return isFollowed;
    }
    public void setIsFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }
    public long getFollowerCount() {
        return followerCount;
    }
    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }
}
