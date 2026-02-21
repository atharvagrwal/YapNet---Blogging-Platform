package de.othregensburg.yapnet.config;

import de.othregensburg.yapnet.dto.RegisterRequestDto;
import de.othregensburg.yapnet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class InitializationConfig {
    
    @Autowired
    private UserService userService;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        userService.init();
        
        // Add test user if it doesn't exist
        if (!userService.existsByUsername("testuser")) {
            userService.register(new RegisterRequestDto("testuser", "test@example.com", "testpassword123"));
        }
    }
}
