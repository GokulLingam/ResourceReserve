package com.example.ResourceReserve.controller;

import com.example.ResourceReserve.dto.ApiResponse;
import com.example.ResourceReserve.dto.BuildingRequest;
import com.example.ResourceReserve.dto.BuildingResponse;
import com.example.ResourceReserve.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BuildingController {
    
    private final BuildingService buildingService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<BuildingResponse>>> getAllBuildings() {
        try {
            List<BuildingResponse> buildings = buildingService.getAllBuildings();
            return ResponseEntity.ok(ApiResponse.<List<BuildingResponse>>builder()
                    .success(true)
                    .data(buildings)
                    .message("Buildings retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving buildings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<BuildingResponse>>builder()
                            .success(false)
                            .message("Error retrieving buildings: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BuildingResponse>> getBuildingById(@PathVariable UUID id) {
        try {
            BuildingResponse building = buildingService.getBuildingById(id);
            return ResponseEntity.ok(ApiResponse.<BuildingResponse>builder()
                    .success(true)
                    .data(building)
                    .message("Building retrieved successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Building not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<BuildingResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error retrieving building with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<BuildingResponse>builder()
                            .success(false)
                            .message("Error retrieving building: " + e.getMessage())
                            .build());
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<BuildingResponse>> createBuilding(@Valid @RequestBody BuildingRequest request) {
        try {
            BuildingResponse building = buildingService.createBuilding(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<BuildingResponse>builder()
                            .success(true)
                            .data(building)
                            .message("Building created successfully")
                            .build());
        } catch (Exception e) {
            log.error("Error creating building", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<BuildingResponse>builder()
                            .success(false)
                            .message("Error creating building: " + e.getMessage())
                            .build());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BuildingResponse>> updateBuilding(
            @PathVariable UUID id, 
            @Valid @RequestBody BuildingRequest request) {
        try {
            BuildingResponse building = buildingService.updateBuilding(id, request);
            return ResponseEntity.ok(ApiResponse.<BuildingResponse>builder()
                    .success(true)
                    .data(building)
                    .message("Building updated successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Building not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<BuildingResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating building with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<BuildingResponse>builder()
                            .success(false)
                            .message("Error updating building: " + e.getMessage())
                            .build());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBuilding(@PathVariable UUID id) {
        try {
            buildingService.deleteBuilding(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Building deleted successfully")
                    .build());
        } catch (RuntimeException e) {
            log.error("Building not found with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error deleting building with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Error deleting building: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<BuildingResponse>>> getBuildingsByCity(@PathVariable String city) {
        try {
            List<BuildingResponse> buildings = buildingService.getBuildingsByCity(city);
            return ResponseEntity.ok(ApiResponse.<List<BuildingResponse>>builder()
                    .success(true)
                    .data(buildings)
                    .message("Buildings retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving buildings by city: {}", city, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<BuildingResponse>>builder()
                            .success(false)
                            .message("Error retrieving buildings: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/country/{country}")
    public ResponseEntity<ApiResponse<List<BuildingResponse>>> getBuildingsByCountry(@PathVariable String country) {
        try {
            List<BuildingResponse> buildings = buildingService.getBuildingsByCountry(country);
            return ResponseEntity.ok(ApiResponse.<List<BuildingResponse>>builder()
                    .success(true)
                    .data(buildings)
                    .message("Buildings retrieved successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving buildings by country: {}", country, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<BuildingResponse>>builder()
                            .success(false)
                            .message("Error retrieving buildings: " + e.getMessage())
                            .build());
        }
    }
} 