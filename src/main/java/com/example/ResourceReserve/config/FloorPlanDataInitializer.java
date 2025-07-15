package com.example.ResourceReserve.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FloorPlanDataInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.info("FloorPlanDataInitializer: No sample data is being inserted.");
    }
} 