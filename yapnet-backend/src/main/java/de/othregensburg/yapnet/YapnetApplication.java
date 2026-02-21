package de.othregensburg.yapnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"de.othregensburg.yapnet", "de.othregensburg.yapnet.config"})
public class YapnetApplication {
    public static void main(String[] args) {
        SpringApplication.run(YapnetApplication.class, args);
    }
} 