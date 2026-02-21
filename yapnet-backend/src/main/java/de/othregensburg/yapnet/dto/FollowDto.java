package de.othregensburg.yapnet.dto;

import java.util.UUID;

public class FollowDto {
    private UUID followingId;
    private String errorMessage;

    public UUID getFollowingId() {
        return followingId;
    }

    public void setFollowingId(UUID followingId) {
        this.followingId = followingId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
