package com.example.ResourceReserve.dto;

import com.example.ResourceReserve.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private Boolean success;
    private UserDto user;
    private String token;
    private String refreshToken;
    private Integer expiresIn;
    private String message;
    private String error;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private String id;
        private String email;
        private String name;
        private String role;
        private String department;
        private String employeeId;
        private String avatar;
        private Set<String> permissions;
        private String lastLogin;
        private Boolean isActive;
    }
} 