package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

class LoginRequestDtoTest {

    private LoginRequestDto loginRequestDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testLoginRequestDtoCreation() {
        assertNotNull(loginRequestDto);
    }

    @Test
    void testSetAndGetUsername() {
        String username = "testuser";
        loginRequestDto.setUsername(username);
        assertEquals(username, loginRequestDto.getUsername());
    }

    @Test
    void testSetAndGetPassword() {
        String password = "testpassword";
        loginRequestDto.setPassword(password);
        assertEquals(password, loginRequestDto.getPassword());
    }

    @Test
    void testValidLoginRequest() {
        loginRequestDto.setUsername("testuser");
        loginRequestDto.setPassword("testpassword");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations");
    }

    @Test
    void testUsernameRequired() {
        loginRequestDto.setPassword("testpassword");
        // username is null

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for missing username");
        
        boolean hasUsernameViolation = violations.stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("username"));
        assertTrue(hasUsernameViolation, "Should have username validation violation");
    }

    @Test
    void testPasswordRequired() {
        loginRequestDto.setUsername("testuser");
        // password is null

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for missing password");
        
        boolean hasPasswordViolation = violations.stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("password"));
        assertTrue(hasPasswordViolation, "Should have password validation violation");
    }

    @Test
    void testEmptyUsername() {
        loginRequestDto.setUsername("");
        loginRequestDto.setPassword("testpassword");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for empty username");
    }

    @Test
    void testEmptyPassword() {
        loginRequestDto.setUsername("testuser");
        loginRequestDto.setPassword("");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for empty password");
    }

    @Test
    void testBlankUsername() {
        loginRequestDto.setUsername("   ");
        loginRequestDto.setPassword("testpassword");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for blank username");
    }

    @Test
    void testBlankPassword() {
        loginRequestDto.setUsername("testuser");
        loginRequestDto.setPassword("   ");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for blank password");
    }

    @Test
    void testLoginRequestWithAllFields() {
        String username = "testuser";
        String password = "testpassword";

        loginRequestDto.setUsername(username);
        loginRequestDto.setPassword(password);

        assertEquals(username, loginRequestDto.getUsername());
        assertEquals(password, loginRequestDto.getPassword());

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations");
    }

    @Test
    void testLoginRequestEquality() {
        LoginRequestDto loginRequest1 = new LoginRequestDto();
        LoginRequestDto loginRequest2 = new LoginRequestDto();

        loginRequest1.setUsername("testuser");
        loginRequest1.setPassword("testpassword");

        loginRequest2.setUsername("testuser");
        loginRequest2.setPassword("testpassword");

        assertEquals(loginRequest1.getUsername(), loginRequest2.getUsername());
        assertEquals(loginRequest1.getPassword(), loginRequest2.getPassword());
    }

    @Test
    void testLoginRequestWithSpecialCharacters() {
        String username = "test_user@123";
        String password = "test@password#123";

        loginRequestDto.setUsername(username);
        loginRequestDto.setPassword(password);

        assertEquals(username, loginRequestDto.getUsername());
        assertEquals(password, loginRequestDto.getPassword());

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations for special characters");
    }

    @Test
    void testLoginRequestWithLongValues() {
        String username = "a".repeat(50);
        String password = "b".repeat(100);

        loginRequestDto.setUsername(username);
        loginRequestDto.setPassword(password);

        assertEquals(username, loginRequestDto.getUsername());
        assertEquals(password, loginRequestDto.getPassword());

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations for long values");
    }

    @Test
    void testLoginRequestDefaultValues() {
        LoginRequestDto newLoginRequest = new LoginRequestDto();
        
        assertNull(newLoginRequest.getUsername());
        assertNull(newLoginRequest.getPassword());
    }
} 