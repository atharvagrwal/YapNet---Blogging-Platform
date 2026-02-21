package de.othregensburg.yapnet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public String test() {
        return "Backend is working!";
    }

    @GetMapping("/test-db")
    public String testDb() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            return "Database connection successful! Result: " + result;
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
} 