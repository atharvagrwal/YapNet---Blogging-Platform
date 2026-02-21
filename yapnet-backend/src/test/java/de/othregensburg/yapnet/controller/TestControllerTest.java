package de.othregensburg.yapnet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private TestController testController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        testController = new TestController();
        // Use reflection to set the jdbcTemplate field since it's autowired
        try {
            java.lang.reflect.Field field = TestController.class.getDeclaredField("jdbcTemplate");
            field.setAccessible(true);
            field.set(testController, jdbcTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set jdbcTemplate", e);
        }
        
        mockMvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void test_success() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Backend is working!"));
    }

    @Test
    void testDb_success() throws Exception {
        when(jdbcTemplate.queryForObject(eq("SELECT 1"), eq(String.class)))
                .thenReturn("1");

        mockMvc.perform(get("/test-db"))
                .andExpect(status().isOk())
                .andExpect(content().string("Database connection successful! Result: 1"));
    }

    @Test
    void testDb_exception() throws Exception {
        when(jdbcTemplate.queryForObject(eq("SELECT 1"), eq(String.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        mockMvc.perform(get("/test-db"))
                .andExpect(status().isOk())
                .andExpect(content().string("Database connection failed: Connection failed"));
    }
} 