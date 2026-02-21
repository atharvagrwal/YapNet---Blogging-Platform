package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

class RegisterRequestDtoTest {

    private RegisterRequestDto registerRequestDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        registerRequestDto = new RegisterRequestDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testRegisterRequestDtoCreation() {
        assertNotNull(registerRequestDto);
    }

    @Test
    void testSetAndGetUsername() {
        String username = "testuser";
        registerRequestDto.setUsername(username);
        assertEquals(username, registerRequestDto.getUsername());
    }

    @Test
    void testSetAndGetEmail() {
        String email = "test@example.com";
        registerRequestDto.setEmail(email);
        assertEquals(email, registerRequestDto.getEmail());
    }

    @Test
    void testSetAndGetPassword() {
        String password = "testpassword";
        registerRequestDto.setPassword(password);
        assertEquals(password, registerRequestDto.getPassword());
    }

    @Test
    void testValidRegisterRequest() {
        registerRequestDto.setUsername("testuser");
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("testpassword");

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations");
    }

    @Test
    void testUsernameRequired() {
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("testpassword");
        // username is null

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for missing username");
        
        boolean hasUsernameViolation = violations.stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("username"));
        assertTrue(hasUsernameViolation, "Should have username validation violation");
    }

    @Test
    void testEmailRequired() {
        registerRequestDto.setUsername("testuser");
        registerRequestDto.setPassword("testpassword");
        // email is null

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for missing email");
        
        boolean hasEmailViolation = violations.stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("email"));
        assertTrue(hasEmailViolation, "Should have email validation violation");
    }

    @Test
    void testPasswordRequired() {
        registerRequestDto.setUsername("testuser");
        registerRequestDto.setEmail("test@example.com");
        // password is null

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for missing password");
        
        boolean hasPasswordViolation = violations.stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("password"));
        assertTrue(hasPasswordViolation, "Should have password validation violation");
    }

    @Test
    void testInvalidEmailFormat() {
        registerRequestDto.setUsername("testuser");
        registerRequestDto.setEmail("invalid-email");
        registerRequestDto.setPassword("testpassword");

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for invalid email");
        
        boolean hasEmailViolation = violations.stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("email"));
        assertTrue(hasEmailViolation, "Should have email validation violation");
    }

    @Test
    void testEmptyUsername() {
        registerRequestDto.setUsername("");
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("testpassword");

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for empty username");
    }

    @Test
    void testEmptyEmail() {
        registerRequestDto.setUsername("testuser");
        registerRequestDto.setEmail("");
        registerRequestDto.setPassword("testpassword");

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for empty email");
    }

    @Test
    void testEmptyPassword() {
        registerRequestDto.setUsername("testuser");
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("");

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for empty password");
    }

    @Test
    void testUsernameTooShort() {
        registerRequestDto.setUsername("ab"); // Less than 3 characters
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("testpassword");

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for username too short");
    }

    @Test
    void testUsernameTooLong() {
        registerRequestDto.setUsername("a".repeat(21)); // More than 20 characters
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("testpassword");

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for username too long");
    }

    @Test
    void testPasswordTooShort() {
        registerRequestDto.setUsername("testuser");
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("12345"); // Less than 6 characters

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertFalse(violations.isEmpty(), "Should have validation violations for password too short");
    }

    @Test
    void testRegisterRequestWithAllFields() {
        String username = "testuser";
        String email = "test@example.com";
        String password = "testpassword";

        registerRequestDto.setUsername(username);
        registerRequestDto.setEmail(email);
        registerRequestDto.setPassword(password);

        assertEquals(username, registerRequestDto.getUsername());
        assertEquals(email, registerRequestDto.getEmail());
        assertEquals(password, registerRequestDto.getPassword());

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations");
    }

    @Test
    void testRegisterRequestEquality() {
        RegisterRequestDto registerRequest1 = new RegisterRequestDto();
        RegisterRequestDto registerRequest2 = new RegisterRequestDto();

        registerRequest1.setUsername("testuser");
        registerRequest1.setEmail("test@example.com");
        registerRequest1.setPassword("testpassword");

        registerRequest2.setUsername("testuser");
        registerRequest2.setEmail("test@example.com");
        registerRequest2.setPassword("testpassword");

        assertEquals(registerRequest1.getUsername(), registerRequest2.getUsername());
        assertEquals(registerRequest1.getEmail(), registerRequest2.getEmail());
        assertEquals(registerRequest1.getPassword(), registerRequest2.getPassword());
    }

    @Test
    void testRegisterRequestWithSpecialCharacters() {
        String username = "test_user@123";
        String email = "test.user+tag@example.co.uk";
        String password = "test@password#123";

        registerRequestDto.setUsername(username);
        registerRequestDto.setEmail(email);
        registerRequestDto.setPassword(password);

        assertEquals(username, registerRequestDto.getUsername());
        assertEquals(email, registerRequestDto.getEmail());
        assertEquals(password, registerRequestDto.getPassword());

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations for special characters");
    }

    @Test
    void testRegisterRequestWithLongValues() {
        String username = "a".repeat(20); // Maximum allowed length
        String email = "test@example.com";
        String password = "b".repeat(100);

        registerRequestDto.setUsername(username);
        registerRequestDto.setEmail(email);
        registerRequestDto.setPassword(password);

        assertEquals(username, registerRequestDto.getUsername());
        assertEquals(email, registerRequestDto.getEmail());
        assertEquals(password, registerRequestDto.getPassword());

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);
        assertTrue(violations.isEmpty(), "Should have no validation violations for long values");
    }

    @Test
    void testRegisterRequestDefaultValues() {
        RegisterRequestDto newRegisterRequest = new RegisterRequestDto();
        
        assertNull(newRegisterRequest.getUsername());
        assertNull(newRegisterRequest.getEmail());
        assertNull(newRegisterRequest.getPassword());
    }

    @Test
    void testRegisterRequestConstructor() {
        String username = "testuser";
        String email = "test@example.com";
        String password = "testpassword";

        RegisterRequestDto registerRequest = new RegisterRequestDto(username, email, password);

        assertEquals(username, registerRequest.getUsername());
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
    }
} 