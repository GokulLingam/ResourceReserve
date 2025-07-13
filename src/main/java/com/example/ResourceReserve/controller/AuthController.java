package com.example.ResourceReserve.controller;

import com.example.ResourceReserve.dto.*;
import com.example.ResourceReserve.service.AuthService;
import com.example.ResourceReserve.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    private final JwtService jwtService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.builder()
                            .success(false)
                            .error("Invalid credentials")
                            .message("Email or password is incorrect")
                            .build());
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            ApiResponse<String> response = authService.logout(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("LOGOUT_FAILED", "Logout failed", "LOGOUT_FAILED"));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            RefreshTokenResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RefreshTokenResponse.builder()
                            .success(false)
                            .build());
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (jwtService.validateToken(token, jwtService.getJwtSecret())) {
                String email = jwtService.extractEmail(token, jwtService.getJwtSecret());
                String role = jwtService.extractRole(token, jwtService.getJwtSecret());
                var permissions = jwtService.extractPermissions(token, jwtService.getJwtSecret());
                
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", jwtService.extractUserId(token, jwtService.getJwtSecret()));
                userInfo.put("email", email);
                userInfo.put("role", role);
                userInfo.put("permissions", permissions);
                
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("user", userInfo);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false));
            }
        } catch (Exception e) {
            log.error("Token verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<LoginResponse.UserDto>> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            // In a real implementation, you would get the user from the database
            // For now, we'll return a mock response
            LoginResponse.UserDto userDto = LoginResponse.UserDto.builder()
                    .id("user_123456")
                    .email(email)
                    .name("John Doe")
                    .role("ADMIN")
                    .department("IT")
                    .employeeId("EMP001")
                    .avatar("https://example.com/avatar.jpg")
                    .isActive(true)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(userDto, "Profile retrieved successfully"));
        } catch (Exception e) {
            log.error("Get profile failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROFILE_RETRIEVAL_FAILED", "Failed to retrieve profile", "PROFILE_RETRIEVAL_FAILED"));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<LoginResponse.UserDto>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        try {
            String email = authentication.getName();
            // In a real implementation, you would get the user ID from the database
            String userId = "user_123456";
            ApiResponse<LoginResponse.UserDto> response = authService.updateProfile(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Profile update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROFILE_UPDATE_FAILED", "Profile update failed", "PROFILE_UPDATE_FAILED"));
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            String email = authentication.getName();
            // In a real implementation, you would get the user ID from the database
            String userId = "user_123456";
            ApiResponse<String> response = authService.changePassword(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("PASSWORD_CHANGE_FAILED", e.getMessage(), "PASSWORD_CHANGE_FAILED"));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            ApiResponse<String> response = authService.forgotPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Forgot password failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("FORGOT_PASSWORD_FAILED", e.getMessage(), "FORGOT_PASSWORD_FAILED"));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            ApiResponse<String> response = authService.resetPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("PASSWORD_RESET_FAILED", e.getMessage(), "PASSWORD_RESET_FAILED"));
        }
    }
    
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin:all')")
    public ResponseEntity<ApiResponse<LoginResponse.UserDto>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            ApiResponse<LoginResponse.UserDto> response = authService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("USER_REGISTRATION_FAILED", e.getMessage(), "USER_REGISTRATION_FAILED"));
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("No valid token found");
    }
} 