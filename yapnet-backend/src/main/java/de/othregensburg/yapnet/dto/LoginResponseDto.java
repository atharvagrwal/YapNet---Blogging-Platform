package de.othregensburg.yapnet.dto;

// No Lombok imports needed


public class LoginResponseDto {
    private String token;
    private String username;
    private String email;
    private String errorMessage;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LoginResponseDto(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.errorMessage = null;
    }

    public LoginResponseDto(String token, String username, String email, String errorMessage) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.errorMessage = errorMessage;
    }

    public LoginResponseDto(String errorMessage) {
        this(null, null, null, errorMessage);
    }
}
