package com.example.ResourceReserve.dto;

import com.example.ResourceReserve.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementResponse {
    
    private String id;
    private String name;
    private String email;
    private UserRole role;
    private String department;
    private String employeeId;
    private String avatar;
    private Set<String> permissions;
    private LocalDateTime lastLogin;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 