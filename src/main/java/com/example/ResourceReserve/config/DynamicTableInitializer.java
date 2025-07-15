package com.example.ResourceReserve.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DynamicTableInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicTableInitializer.class);
    

    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Dynamic floor plan system initialized. No sample data will be created.");
        logger.info("Tables will be created dynamically when floor plan data is first saved.");
    }
    

} 