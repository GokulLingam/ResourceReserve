package com.example.ResourceReserve.controller;

import com.example.ResourceReserve.dto.ApiResponse;
import com.example.ResourceReserve.dto.DeskRequest;
import com.example.ResourceReserve.dto.DeskResponse;
import com.example.ResourceReserve.entity.Desk;
import com.example.ResourceReserve.service.DeskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/desks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DeskController {
    
    private final DeskService deskService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeskResponse>>> getAllDesks() {
        try {
            List<DeskResponse> desks = deskService.getAllDesks();
            return ResponseEntity.ok(ApiResponse.<List<DeskResponse>>builder()
                    .success(true)
                    .data(desks)
                    .message("Desks retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving desks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DeskResponse>>builder()
                            .success(false)
                            .message("Error retrieving desks: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/floor/{floorId}")
    public ResponseEntity<ApiResponse<List<DeskResponse>>> getDesksByFloor(@PathVariable UUID floorId) {
        try {
            List<DeskResponse> desks = deskService.getDesksByFloor(floorId);
            return ResponseEntity.ok(ApiResponse.<List<DeskResponse>>builder()
                    .success(true)
                    .data(desks)
                    .message("Desks retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving desks for floor: {}", floorId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DeskResponse>>builder()
                            .success(false)
                            .message("Error retrieving desks: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DeskResponse>>> getDesksByStatus(@PathVariable Desk.DeskStatus status) {
        try {
            List<DeskResponse> desks = deskService.getDesksByStatus(status);
            return ResponseEntity.ok(ApiResponse.<List<DeskResponse>>builder()
                    .success(true)
                    .data(desks)
                    .message("Desks retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving desks with status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DeskResponse>>builder()
                            .success(false)
                            .message("Error retrieving desks: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/type/{deskType}")
    public ResponseEntity<ApiResponse<List<DeskResponse>>> getDesksByType(@PathVariable Desk.DeskType deskType) {
        try {
            List<DeskResponse> desks = deskService.getDesksByType(deskType);
            return ResponseEntity.ok(ApiResponse.<List<DeskResponse>>builder()
                    .success(true)
                    .data(desks)
                    .message("Desks retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving desks with type: {}", deskType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DeskResponse>>builder()
                            .success(false)
                            .message("Error retrieving desks: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/floor/{floorId}/available")
    public ResponseEntity<ApiResponse<List<DeskResponse>>> getAvailableDesksByFloor(@PathVariable UUID floorId) {
        try {
            List<DeskResponse> desks = deskService.getAvailableDesksByFloor(floorId);
            return ResponseEntity.ok(ApiResponse.<List<DeskResponse>>builder()
                    .success(true)
                    .data(desks)
                    .message("Available desks retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving available desks for floor: {}", floorId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DeskResponse>>builder()
                            .success(false)
                            .message("Error retrieving available desks: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeskResponse>> getDeskById(@PathVariable UUID id) {
        try {
            DeskResponse desk = deskService.getDeskById(id);
            return ResponseEntity.ok(ApiResponse.<DeskResponse>builder()
                    .success(true)
                    .data(desk)
                    .message("Desk retrieved successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Desk not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<DeskResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error retrieving desk with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<DeskResponse>builder()
                            .success(false)
                            .message("Error retrieving desk: " + e.getMessage())
                            .build());
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<DeskResponse>> createDesk(@Valid @RequestBody DeskRequest request) {
        try {
            DeskResponse desk = deskService.createDesk(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<DeskResponse>builder()
                            .success(true)
                            .data(desk)
                            .message("Desk created successfully")
                            .build());
        } catch (Exception e) {
            log.error("Error creating desk", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<DeskResponse>builder()
                            .success(false)
                            .message("Error creating desk: " + e.getMessage())
                            .build());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeskResponse>> updateDesk(
            @PathVariable UUID id, 
            @Valid @RequestBody DeskRequest request) {
        try {
            DeskResponse desk = deskService.updateDesk(id, request);
            return ResponseEntity.ok(ApiResponse.<DeskResponse>builder()
                    .success(true)
                    .data(desk)
                    .message("Desk updated successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Desk not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<DeskResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating desk with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<DeskResponse>builder()
                            .success(false)
                            .message("Error updating desk: " + e.getMessage())
                            .build());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDesk(@PathVariable UUID id) {
        try {
            deskService.deleteDesk(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Desk deleted successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Desk not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error deleting desk with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Error deleting desk: " + e.getMessage())
                            .build());
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DeskResponse>> updateDeskStatus(
            @PathVariable UUID id, 
            @RequestParam Desk.DeskStatus status) {
        try {
            DeskResponse desk = deskService.updateDeskStatus(id, status);
            return ResponseEntity.ok(ApiResponse.<DeskResponse>builder()
                    .success(true)
                    .data(desk)
                    .message("Desk status updated successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Desk not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<DeskResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating desk status with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<DeskResponse>builder()
                            .success(false)
                            .message("Error updating desk status: " + e.getMessage())
                            .build());
        }
    }
} 