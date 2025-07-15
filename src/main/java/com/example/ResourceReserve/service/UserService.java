package com.example.ResourceReserve.service;

import com.example.ResourceReserve.dto.UserManagementRequest;
import com.example.ResourceReserve.dto.UserManagementResponse;
import com.example.ResourceReserve.entity.User;
import com.example.ResourceReserve.entity.UserRole;
import com.example.ResourceReserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<UserManagementResponse> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<UserManagementResponse> getActiveUsers() {
        log.info("Fetching active users");
        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public UserManagementResponse getUserById(String userId) {
        log.info("Fetching user with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return mapToResponse(user);
    }
    
    public UserManagementResponse createUser(UserManagementRequest request) {
        log.info("Creating new user: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .department(request.getDepartment())
                .employeeId(request.getEmployeeId())
                .avatar(request.getAvatar())
                .permissions(request.getPermissions() != null ? request.getPermissions() : getDefaultPermissions(request.getRole()))
                .isActive(request.getIsActive())
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return mapToResponse(savedUser);
    }
    
    public UserManagementResponse updateUser(String userId, UserManagementRequest request) {
        log.info("Updating user with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setDepartment(request.getDepartment());
        user.setEmployeeId(request.getEmployeeId());
        user.setAvatar(request.getAvatar());
        user.setIsActive(request.getIsActive());
        
        // Update password only if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Update permissions if provided, otherwise use default for the role
        if (request.getPermissions() != null) {
            user.setPermissions(request.getPermissions());
        } else {
            user.setPermissions(getDefaultPermissions(request.getRole()));
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully");
        return mapToResponse(updatedUser);
    }
    
    public void deleteUser(String userId) {
        log.info("Deleting user with id: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
        log.info("User deleted successfully");
    }
    
    public UserManagementResponse deactivateUser(String userId) {
        log.info("Deactivating user with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setIsActive(false);
        User deactivatedUser = userRepository.save(user);
        log.info("User deactivated successfully");
        return mapToResponse(deactivatedUser);
    }
    
    public UserManagementResponse activateUser(String userId) {
        log.info("Activating user with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setIsActive(true);
        User activatedUser = userRepository.save(user);
        log.info("User activated successfully");
        return mapToResponse(activatedUser);
    }
    
    public List<UserManagementResponse> getUsersByRole(UserRole role) {
        log.info("Fetching users with role: {}", role);
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<UserManagementResponse> getUsersByDepartment(String department) {
        log.info("Fetching users in department: {}", department);
        List<User> users = userRepository.findByDepartment(department);
        return users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private UserManagementResponse mapToResponse(User user) {
        return UserManagementResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .employeeId(user.getEmployeeId())
                .avatar(user.getAvatar())
                .permissions(user.getPermissions())
                .lastLogin(user.getLastLogin())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
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