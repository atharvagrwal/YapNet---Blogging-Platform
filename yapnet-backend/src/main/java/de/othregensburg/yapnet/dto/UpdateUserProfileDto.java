package de.othregensburg.yapnet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserProfileDto {
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;
    
    @Size(max = 500, message = "Biography cannot exceed 500 characters")
    private String biography;
    
    @Size(max = 255, message = "Profile picture URL cannot exceed 255 characters")
    private String profilePictureUrl;
    
    @Email(message = "Email must be a valid email address")
    private String email;

    // Constructors
    public UpdateUserProfileDto() {}

    public UpdateUserProfileDto(String fullName, String biography, String profilePictureUrl, String email) {
        this.fullName = fullName;
        this.biography = biography;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
    }

    // Getters and Setters
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
} 