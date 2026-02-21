package de.othregensburg.yapnet.config;

import de.othregensburg.yapnet.dto.RegisterRequestDto;
import de.othregensburg.yapnet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ContextRefreshedEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitializationConfigTest {

    @Mock
    private UserService userService;

    @Mock
    private ContextRefreshedEvent contextRefreshedEvent;

    private InitializationConfig initializationConfig;

    @BeforeEach
    void setUp() {
        initializationConfig = new InitializationConfig();
        // Use reflection to set the private userService field
        try {
            java.lang.reflect.Field field = InitializationConfig.class.getDeclaredField("userService");
            field.setAccessible(true);
            field.set(initializationConfig, userService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set userService", e);
        }
    }

    @Test
    void testInitCallsUserServiceInit() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(userService.register(any(RegisterRequestDto.class))).thenReturn(null);

        // Act
        initializationConfig.init();

        // Assert
        verify(userService, times(1)).init();
    }

    @Test
    void testInitCreatesTestUserWhenNotExists() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(userService.register(any(RegisterRequestDto.class))).thenReturn(null);

        // Act
        initializationConfig.init();

        // Assert
        verify(userService, times(1)).existsByUsername("testuser");
        verify(userService, times(1)).register(argThat(request -> 
            "testuser".equals(request.getUsername()) &&
            "test@example.com".equals(request.getEmail()) &&
            "testpassword123".equals(request.getPassword())
        ));
    }

    @Test
    void testInitDoesNotCreateTestUserWhenExists() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(true);

        // Act
        initializationConfig.init();

        // Assert
        verify(userService, times(1)).existsByUsername("testuser");
        verify(userService, never()).register(any(RegisterRequestDto.class));
    }

    @Test
    void testInitHandlesUserServiceInitException() {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(userService).init();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            initializationConfig.init();
        });
    }

    @Test
    void testInitHandlesUserServiceExistsException() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            initializationConfig.init();
        });
    }

    @Test
    void testInitHandlesUserServiceRegisterException() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(userService.register(any(RegisterRequestDto.class))).thenThrow(new RuntimeException("Registration error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            initializationConfig.init();
        });
    }
} 