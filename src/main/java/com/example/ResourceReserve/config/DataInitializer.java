package com.example.ResourceReserve.config;

import com.example.ResourceReserve.entity.User;
import com.example.ResourceReserve.entity.UserRole;
import com.example.ResourceReserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if it doesn't exist
        if (!userRepository.existsByEmail("admin@upsreserve.com")) {
            User adminUser = User.builder()
                    .email("admin@upsreserve.com")
                    .password(passwordEncoder.encode("admin123"))
                    .name("Admin User")
                    .role(UserRole.ADMIN)
                    .department("IT")
                    .employeeId("EMP001")
                    .avatar("https://example.com/avatar.jpg")
                    .permissions(getAdminPermissions())
                    .isActive(true)
                    .build();
            
            userRepository.save(adminUser);
            log.info("Default admin user created: admin@upsreserve.com");
        }
        
        // Create a test employee user
        if (!userRepository.existsByEmail("employee@upsreserve.com")) {
            User employeeUser = User.builder()
                    .email("employee@upsreserve.com")
                    .password(passwordEncoder.encode("employee123"))
                    .name("Test Employee")
                    .role(UserRole.EMPLOYEE)
                    .department("Marketing")
                    .employeeId("EMP002")
                    .permissions(getEmployeePermissions())
                    .isActive(true)
                    .build();
            
            userRepository.save(employeeUser);
            log.info("Test employee user created: employee@upsreserve.com");
        }
    }
    
    private Set<String> getAdminPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.addAll(Set.of(
                "seat:read", "seat:write", "seat:delete",
                "user:read", "user:write", "user:delete",
                "floor:read", "floor:write", "floor:delete",
                "booking:read", "booking:write", "booking:delete",
                "admin:all"
        ));
        return permissions;
    }
    
    private Set<String> getEmployeePermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.addAll(Set.of(
                "seat:read",
                "floor:read",
                "booking:read", "booking:write"
        ));
        return permissions;
    }
} 