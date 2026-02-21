package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginResponseDtoTest {

    @Test
    void testLoginResponseDtoCreationWithToken() {
        String token = "eyJhbGciOiJIUzUxMiJ9.test.token";
        String username = "testuser";
        String email = "test@example.com";

        LoginResponseDto loginResponseDto = new LoginResponseDto(token, username, email);

        assertEquals(token, loginResponseDto.getToken());
        assertEquals(username, loginResponseDto.getUsername());
        assertEquals(email, loginResponseDto.getEmail());
        assertNull(loginResponseDto.getErrorMessage());
    }

    @Test
    void testLoginResponseDtoCreationWithErrorMessage() {
        String token = "eyJhbGciOiJIUzUxMiJ9.test.token";
        String username = "testuser";
        String email = "test@example.com";
        String errorMessage = "Invalid credentials";

        LoginResponseDto loginResponseDto = new LoginResponseDto(token, username, email, errorMessage);

        assertEquals(token, loginResponseDto.getToken());
        assertEquals(username, loginResponseDto.getUsername());
        assertEquals(email, loginResponseDto.getEmail());
        assertEquals(errorMessage, loginResponseDto.getErrorMessage());
    }

    @Test
    void testLoginResponseDtoCreationWithErrorOnly() {
        String errorMessage = "Invalid credentials";

        LoginResponseDto loginResponseDto = new LoginResponseDto(errorMessage);

        assertNull(loginResponseDto.getToken());
        assertNull(loginResponseDto.getUsername());
        assertNull(loginResponseDto.getEmail());
        assertEquals(errorMessage, loginResponseDto.getErrorMessage());
    }

    @Test
    void testSetAndGetToken() {
        String token1 = "eyJhbGciOiJIUzUxMiJ9.test.token1";
        String token2 = "eyJhbGciOiJIUzUxMiJ9.test.token2";
        String username = "testuser";
        String email = "test@example.com";

        LoginResponseDto loginResponseDto = new LoginResponseDto(token1, username, email);
        assertEquals(token1, loginResponseDto.getToken());

        loginResponseDto.setToken(token2);
        assertEquals(token2, loginResponseDto.getToken());
    }

    @Test
    void testLoginResponseWithAllFields() {
        String token = "eyJhbGciOiJIUzUxMiJ9.test.token";
        String username = "testuser";
        String email = "test@example.com";
        String errorMessage = null;

        LoginResponseDto loginResponseDto = new LoginResponseDto(token, username, email, errorMessage);

        assertEquals(token, loginResponseDto.getToken());
        assertEquals(username, loginResponseDto.getUsername());
        assertEquals(email, loginResponseDto.getEmail());
        assertEquals(errorMessage, loginResponseDto.getErrorMessage());
    }

    @Test
    void testLoginResponseWithError() {
        String token = null;
        String username = null;
        String email = null;
        String errorMessage = "Invalid credentials";

        LoginResponseDto loginResponseDto = new LoginResponseDto(token, username, email, errorMessage);

        assertNull(loginResponseDto.getToken());
        assertNull(loginResponseDto.getUsername());
        assertNull(loginResponseDto.getEmail());
        assertEquals(errorMessage, loginResponseDto.getErrorMessage());
    }

    @Test
    void testLoginResponseEquality() {
        String token = "token1";
        String username = "user1";
        String email = "user1@example.com";
        String errorMessage = null;

        LoginResponseDto response1 = new LoginResponseDto(token, username, email, errorMessage);
        LoginResponseDto response2 = new LoginResponseDto(token, username, email, errorMessage);

        assertEquals(response1.getToken(), response2.getToken());
        assertEquals(response1.getUsername(), response2.getUsername());
        assertEquals(response1.getEmail(), response2.getEmail());
        assertEquals(response1.getErrorMessage(), response2.getErrorMessage());
    }

    @Test
    void testLoginResponseWithNullValues() {
        LoginResponseDto loginResponseDto = new LoginResponseDto(null, null, null, null);

        assertNull(loginResponseDto.getToken());
        assertNull(loginResponseDto.getUsername());
        assertNull(loginResponseDto.getEmail());
        assertNull(loginResponseDto.getErrorMessage());
    }

    @Test
    void testLoginResponseWithSpecialCharacters() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0X3VzZXJAMTIzIiwiaWF0IjoxNjM0NTY3ODkwfQ.test";
        String username = "test_user@123";
        String email = "test.user+tag@example.co.uk";
        String errorMessage = "Error: Invalid @#$%^&*() characters";

        LoginResponseDto loginResponseDto = new LoginResponseDto(token, username, email, errorMessage);

        assertEquals(token, loginResponseDto.getToken());
        assertEquals(username, loginResponseDto.getUsername());
        assertEquals(email, loginResponseDto.getEmail());
        assertEquals(errorMessage, loginResponseDto.getErrorMessage());
    }

    @Test
    void testLoginResponseWithLongValues() {
        String token = "a".repeat(1000);
        String username = "b".repeat(100);
        String email = "c".repeat(200) + "@example.com";
        String errorMessage = "d".repeat(500);

        LoginResponseDto loginResponseDto = new LoginResponseDto(token, username, email, errorMessage);

        assertEquals(token, loginResponseDto.getToken());
        assertEquals(username, loginResponseDto.getUsername());
        assertEquals(email, loginResponseDto.getEmail());
        assertEquals(errorMessage, loginResponseDto.getErrorMessage());
    }

    @Test
    void testMultipleConstructors() {
        // Test constructor with token, username, email
        LoginResponseDto response1 = new LoginResponseDto("token1", "user1", "user1@example.com");
        assertEquals("token1", response1.getToken());
        assertEquals("user1", response1.getUsername());
        assertEquals("user1@example.com", response1.getEmail());
        assertNull(response1.getErrorMessage());

        // Test constructor with token, username, email, errorMessage
        LoginResponseDto response2 = new LoginResponseDto("token2", "user2", "user2@example.com", "error");
        assertEquals("token2", response2.getToken());
        assertEquals("user2", response2.getUsername());
        assertEquals("user2@example.com", response2.getEmail());
        assertEquals("error", response2.getErrorMessage());

        // Test constructor with errorMessage only
        LoginResponseDto response3 = new LoginResponseDto("error only");
        assertNull(response3.getToken());
        assertNull(response3.getUsername());
        assertNull(response3.getEmail());
        assertEquals("error only", response3.getErrorMessage());
    }
} 