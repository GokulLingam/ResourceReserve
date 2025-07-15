package com.example.ResourceReserve.controller;

import com.example.ResourceReserve.dto.ApiResponse;
import com.example.ResourceReserve.dto.UserManagementRequest;
import com.example.ResourceReserve.dto.UserManagementResponse;
import com.example.ResourceReserve.entity.UserRole;
import com.example.ResourceReserve.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('admin:all')")
public class UserManagementController {
    
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<UserManagementResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Users retrieved successfully")
                    .data(Map.of("users", users, "count", users.size()))
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error retrieving users: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveUsers() {
        try {
            List<UserManagementResponse> users = userService.getActiveUsers();
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Active users retrieved successfully")
                    .data(Map.of("users", users, "count", users.size()))
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving active users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error retrieving active users: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable String userId) {
        try {
            UserManagementResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User retrieved successfully")
                    .data(Map.of("user", user))
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder()
                    .success(false)
                    .message("User not found: " + e.getMessage())
                    .build());
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserManagementRequest request) {
        try {
            UserManagementResponse user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                    .success(true)
                    .message("User created successfully")
                    .data(Map.of("user", user))
                    .build());
        } catch (Exception e) {
            log.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                    .success(false)
                    .message("Error creating user: " + e.getMessage())
                    .build());
        }
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable String userId, @Valid @RequestBody UserManagementRequest request) {
        try {
            UserManagementResponse user = userService.updateUser(userId, request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User updated successfully")
                    .data(Map.of("user", user))
                    .build());
        } catch (Exception e) {
            log.error("Error updating user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                    .success(false)
                    .message("Error updating user: " + e.getMessage())
                    .build());
        }
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User deleted successfully")
                    .data(Map.of("userId", userId))
                    .build());
        } catch (Exception e) {
            log.error("Error deleting user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                    .success(false)
                    .message("Error deleting user: " + e.getMessage())
                    .build());
        }
    }
    
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable String userId) {
        try {
            UserManagementResponse user = userService.deactivateUser(userId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User deactivated successfully")
                    .data(Map.of("user", user))
                    .build());
        } catch (Exception e) {
            log.error("Error deactivating user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                    .success(false)
                    .message("Error deactivating user: " + e.getMessage())
                    .build());
        }
    }
    
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable String userId) {
        try {
            UserManagementResponse user = userService.activateUser(userId);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User activated successfully")
                    .data(Map.of("user", user))
                    .build());
        } catch (Exception e) {
            log.error("Error activating user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                    .success(false)
                    .message("Error activating user: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse> getUsersByRole(@PathVariable UserRole role) {
        try {
            List<UserManagementResponse> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Users retrieved successfully")
                    .data(Map.of("users", users, "count", users.size(), "role", role))
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving users by role: {}", role, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error retrieving users by role: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse> getUsersByDepartment(@PathVariable String department) {
        try {
            List<UserManagementResponse> users = userService.getUsersByDepartment(department);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Users retrieved successfully")
                    .data(Map.of("users", users, "count", users.size(), "department", department))
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving users by department: {}", department, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error retrieving users by department: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse> getAvailableRoles() {
        try {
            UserRole[] roles = UserRole.values();
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Available roles retrieved successfully")
                    .data(Map.of("roles", roles))
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving available roles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error retrieving available roles: " + e.getMessage())
                    .build());
        }
    }
} 