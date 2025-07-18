package com.example.ResourceReserve.service;

import com.example.ResourceReserve.dto.*;
import com.example.ResourceReserve.entity.RefreshToken;
import com.example.ResourceReserve.entity.User;
import com.example.ResourceReserve.entity.UserRole;
import com.example.ResourceReserve.repository.RefreshTokenRepository;
import com.example.ResourceReserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            // Save refresh token
            saveRefreshToken(user, refreshToken);
            
            return LoginResponse.builder()
                    .success(true)
                    .user(convertToUserDto(user))
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(3600)
                    .message("Login successful")
                    .build();
                    
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    @Transactional
    public ApiResponse<String> logout(String token, String refreshToken) {
        try {
            String userId = jwtService.extractUserId(token, jwtService.getJwtSecret());
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Only revoke the provided refresh token
            RefreshToken tokenEntity = refreshTokenRepository.findByTokenAndIsRevokedFalse(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found or already revoked"));
            tokenEntity.setIsRevoked(true);
            refreshTokenRepository.save(tokenEntity);

            return ApiResponse.success("Logged out successfully");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            throw new RuntimeException("Logout failed");
        }
    }
    
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        try {
            if (!jwtService.validateToken(request.getRefreshToken(), jwtService.getJwtRefreshSecret())) {
                throw new RuntimeException("Invalid refresh token");
            }
            
            String email = jwtService.extractEmail(request.getRefreshToken(), jwtService.getJwtRefreshSecret());
            User user = userRepository.findByEmailAndIsActiveTrue(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Verify refresh token exists and is not revoked
            RefreshToken refreshToken = refreshTokenRepository.findByTokenAndIsRevokedFalse(request.getRefreshToken())
                    .orElseThrow(() -> new RuntimeException("Refresh token not found or revoked"));
            
            // Generate new tokens
            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);
            
            // Revoke old refresh token and save new one
            refreshToken.setIsRevoked(true);
            refreshTokenRepository.save(refreshToken);
            saveRefreshToken(user, newRefreshToken);
            
            return RefreshTokenResponse.builder()
                    .success(true)
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .expiresIn(3600)
                    .build();
                    
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed");
        }
    }
    
    @Transactional
    public ApiResponse<LoginResponse.UserDto> registerUser(UserRegistrationRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .role(request.getRole())
                    .department(request.getDepartment())
                    .employeeId(request.getEmployeeId())
                    .permissions(getDefaultPermissions(request.getRole()))
                    .isActive(true)
                    .build();
            
            User savedUser = userRepository.save(user);
            
            return ApiResponse.success(convertToUserDto(savedUser), "User registered successfully");
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage());
            throw new RuntimeException("User registration failed: " + e.getMessage());
        }
    }
    
    @Transactional
    public ApiResponse<LoginResponse.UserDto> updateProfile(String userId, UpdateProfileRequest request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getDepartment() != null) {
                user.setDepartment(request.getDepartment());
            }
            if (request.getEmployeeId() != null) {
                user.setEmployeeId(request.getEmployeeId());
            }
            
            User updatedUser = userRepository.save(user);
            return ApiResponse.success(convertToUserDto(updatedUser), "Profile updated successfully");
        } catch (Exception e) {
            log.error("Profile update failed: {}", e.getMessage());
            throw new RuntimeException("Profile update failed");
        }
    }
    
    @Transactional
    public ApiResponse<String> changePassword(String userId, ChangePasswordRequest request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            
            return ApiResponse.success("Password changed successfully");
        } catch (Exception e) {
            log.error("Password change failed: {}", e.getMessage());
            throw new RuntimeException("Password change failed: " + e.getMessage());
        }
    }
    
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate reset token (in a real implementation, this would be sent via email)
            String resetToken = UUID.randomUUID().toString();
            
            // TODO: Send email with reset token
            log.info("Password reset token for {}: {}", user.getEmail(), resetToken);
            
            return ApiResponse.success("Password reset email sent successfully");
        } catch (Exception e) {
            log.error("Forgot password failed: {}", e.getMessage());
            throw new RuntimeException("Forgot password failed");
        }
    }
    
    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        try {
            // TODO: Validate reset token from database
            // For now, we'll just return success
            log.info("Password reset with token: {}", request.getToken());
            
            return ApiResponse.success("Password reset successfully");
        } catch (Exception e) {
            log.error("Password reset failed: {}", e.getMessage());
            throw new RuntimeException("Password reset failed");
        }
    }
    
    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getJwtRefreshExpiration()))
                .isRevoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
    }
    
    private LoginResponse.UserDto convertToUserDto(User user) {
        return LoginResponse.UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .employeeId(user.getEmployeeId())
                .avatar(user.getAvatar())
                .permissions(user.getPermissions())
                .lastLogin(user.getLastLogin() != null ? user.getLastLogin().toString() : null)
                .isActive(user.getIsActive())
                .build();
    }
    
    private Set<String> getDefaultPermissions(UserRole role) {
        Set<String> permissions = new HashSet<>();
        
        switch (role) {
            case ADMIN:
                permissions.addAll(Set.of(
                        "seat:read", "seat:write", "seat:delete",
                        "user:read", "user:write", "user:delete",
                        "floor:read", "floor:write", "floor:delete",
                        "booking:read", "booking:write", "booking:delete",
                        "admin:all"
                ));
                break;
            case MANAGER:
                permissions.addAll(Set.of(
                        "seat:read", "seat:write",
                        "user:read",
                        "floor:read", "floor:write",
                        "booking:read", "booking:write", "booking:delete"
                ));
                break;
            case EMPLOYEE:
                permissions.addAll(Set.of(
                        "seat:read",
                        "floor:read",
                        "booking:read", "booking:write"
                ));
                break;
            case GUEST:
                permissions.addAll(Set.of(
                        "seat:read",
                        "floor:read",
                        "booking:read"
                ));
                break;
        }
        
        return permissions;
    }
} 