package com.example.ResourceReserve.controller;

import com.example.ResourceReserve.dto.ApiResponse;
import com.example.ResourceReserve.dto.FloorLayoutRequest;
import com.example.ResourceReserve.dto.FloorLayoutResponse;
import com.example.ResourceReserve.dto.FloorRequest;
import com.example.ResourceReserve.dto.FloorResponse;
import com.example.ResourceReserve.service.FloorLayoutService;
import com.example.ResourceReserve.service.FloorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/floors")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FloorController {
    
    private final FloorService floorService;
    private final FloorLayoutService floorLayoutService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<FloorResponse>>> getAllFloors() {
        try {
            List<FloorResponse> floors = floorService.getAllFloors();
            return ResponseEntity.ok(ApiResponse.<List<FloorResponse>>builder()
                    .success(true)
                    .data(floors)
                    .message("Floors retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving floors", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<FloorResponse>>builder()
                            .success(false)
                            .message("Error retrieving floors: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/building/{buildingId}")
    public ResponseEntity<ApiResponse<List<FloorResponse>>> getFloorsByBuilding(@PathVariable UUID buildingId) {
        try {
            List<FloorResponse> floors = floorService.getFloorsByBuilding(buildingId);
            return ResponseEntity.ok(ApiResponse.<List<FloorResponse>>builder()
                    .success(true)
                    .data(floors)
                    .message("Floors retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving floors for building: {}", buildingId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<FloorResponse>>builder()
                            .success(false)
                            .message("Error retrieving floors: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FloorResponse>> getFloorById(@PathVariable UUID id) {
        try {
            FloorResponse floor = floorService.getFloorById(id);
            return ResponseEntity.ok(ApiResponse.<FloorResponse>builder()
                    .success(true)
                    .data(floor)
                    .message("Floor retrieved successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Floor not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<FloorResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error retrieving floor with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<FloorResponse>builder()
                            .success(false)
                            .message("Error retrieving floor: " + e.getMessage())
                            .build());
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<FloorResponse>> createFloor(@Valid @RequestBody FloorRequest request) {
        try {
            FloorResponse floor = floorService.createFloor(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<FloorResponse>builder()
                            .success(true)
                            .data(floor)
                            .message("Floor created successfully")
                            .build());
        } catch (Exception e) {
            log.error("Error creating floor", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<FloorResponse>builder()
                            .success(false)
                            .message("Error creating floor: " + e.getMessage())
                            .build());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FloorResponse>> updateFloor(
            @PathVariable UUID id, 
            @Valid @RequestBody FloorRequest request) {
        try {
            FloorResponse floor = floorService.updateFloor(id, request);
            return ResponseEntity.ok(ApiResponse.<FloorResponse>builder()
                    .success(true)
                    .data(floor)
                    .message("Floor updated successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Floor not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<FloorResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating floor with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<FloorResponse>builder()
                            .success(false)
                            .message("Error updating floor: " + e.getMessage())
                            .build());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFloor(@PathVariable UUID id) {
        try {
            floorService.deleteFloor(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Floor deleted successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Floor not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error deleting floor with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Error deleting floor: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<FloorResponse>>> getActiveFloors() {
        try {
            List<FloorResponse> floors = floorService.getActiveFloors();
            return ResponseEntity.ok(ApiResponse.<List<FloorResponse>>builder()
                    .success(true)
                    .data(floors)
                    .message("Active floors retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving active floors", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<FloorResponse>>builder()
                            .success(false)
                            .message("Error retrieving active floors: " + e.getMessage())
                            .build());
        }
    }
    
    // Floor Layout endpoints
    
    @GetMapping("/{id}/layout")
    public ResponseEntity<ApiResponse<FloorLayoutResponse>> getFloorLayout(@PathVariable UUID id) {
        try {
            FloorLayoutResponse layout = floorLayoutService.getFloorLayout(id);
            return ResponseEntity.ok(ApiResponse.<FloorLayoutResponse>builder()
                    .success(true)
                    .data(layout)
                    .message("Floor layout retrieved successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Floor layout not found for floor: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<FloorLayoutResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error retrieving floor layout for floor: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<FloorLayoutResponse>builder()
                            .success(false)
                            .message("Error retrieving floor layout: " + e.getMessage())
                            .build());
        }
    }
    
    @PutMapping("/{id}/layout")
    public ResponseEntity<ApiResponse<FloorLayoutResponse>> updateFloorLayout(
            @PathVariable UUID id, 
            @Valid @RequestBody FloorLayoutRequest request) {
        try {
            FloorLayoutResponse layout = floorLayoutService.createOrUpdateFloorLayout(id, request);
            return ResponseEntity.ok(ApiResponse.<FloorLayoutResponse>builder()
                    .success(true)
                    .data(layout)
                    .message("Floor layout updated successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Floor not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<FloorLayoutResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating floor layout for floor: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<FloorLayoutResponse>builder()
                            .success(false)
                            .message("Error updating floor layout: " + e.getMessage())
                            .build());
        }
    }
    
    @DeleteMapping("/{id}/layout")
    public ResponseEntity<ApiResponse<Void>> deleteFloorLayout(@PathVariable UUID id) {
        try {
            floorLayoutService.deleteFloorLayout(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Floor layout deleted successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Floor layout not found for floor: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error deleting floor layout for floor: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Error deleting floor layout: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/{id}/layout/latest")
    public ResponseEntity<ApiResponse<FloorLayoutResponse>> getLatestFloorLayout(@PathVariable UUID id) {
        try {
            FloorLayoutResponse layout = floorLayoutService.getLatestFloorLayout(id);
            return ResponseEntity.ok(ApiResponse.<FloorLayoutResponse>builder()
                    .success(true)
                    .data(layout)
                    .message("Latest floor layout retrieved successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Floor layout not found for floor: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<FloorLayoutResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error retrieving latest floor layout for floor: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<FloorLayoutResponse>builder()
                            .success(false)
                            .message("Error retrieving latest floor layout: " + e.getMessage())
                            .build());
        }
    }
} 