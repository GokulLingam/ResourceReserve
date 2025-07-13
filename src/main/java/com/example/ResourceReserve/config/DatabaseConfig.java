package com.example.ResourceReserve.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Bean
    public CommandLineRunner databaseInfo() {
        return args -> {
            log.info("=== Database Configuration ===");
            log.info("Database URL: {}", databaseUrl);
            log.info("Username: {}", databaseUsername);
            log.info("Password: {}", databasePassword.isEmpty() ? "(empty)" : "***");
            log.info("===============================");
        };
    }
} 