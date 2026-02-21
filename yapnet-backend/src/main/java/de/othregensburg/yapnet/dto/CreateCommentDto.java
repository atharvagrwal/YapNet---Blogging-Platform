package de.othregensburg.yapnet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCommentDto {
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 200, message = "Content cannot exceed 200 characters")
    private String content;

    // Constructors
    public CreateCommentDto() {}

    public CreateCommentDto(String content) {
        this.content = content;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
} 