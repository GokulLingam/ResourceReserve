package com.example.ResourceReserve.controller;

import com.example.ResourceReserve.dto.ApiResponse;
import com.example.ResourceReserve.dto.DynamicFloorPlanRequest;
import com.example.ResourceReserve.entity.Booking;
import com.example.ResourceReserve.repository.BookingRepository;
import com.example.ResourceReserve.service.DynamicFloorPlanService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dynamic-floor-plans")
@CrossOrigin(origins = "*")
public class DynamicFloorPlanController {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicFloorPlanController.class);
    
    @Autowired
    private DynamicFloorPlanService dynamicFloorPlanService;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    /**
     * Save or update floor plan data
     */
    @PostMapping
    public ResponseEntity<ApiResponse> saveFloorPlan(@Valid @RequestBody DynamicFloorPlanRequest request) {
        try {
            logger.info("Saving floor plan data for: officeLocation={}, buildingName={}, floorId={}", 
                       request.getOfficeLocation(), request.getBuildingName(), request.getFloorId());
            
            dynamicFloorPlanService.saveFloorPlanData(
                request.getOfficeLocation(),
                request.getBuildingName(),
                request.getFloorId(),
                request.getPlanJson()
            );
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Floor plan data saved successfully")
                    .data(Map.of(
                        "table_name", dynamicFloorPlanService.generateTableName(
                            request.getOfficeLocation(), 
                            request.getBuildingName(), 
                            request.getFloorId()
                        )
                    ))
                    .build());
                    
        } catch (Exception e) {
            logger.error("Error saving floor plan data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to save floor plan data: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get floor plan data by parameters
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getFloorPlan(
            @RequestParam String officeLocation,
            @RequestParam String buildingName,
            @RequestParam String floorId,
            @RequestParam(required = false) String date) {
        try {
            logger.info("Retrieving floor plan data for: officeLocation={}, buildingName={}, floorId={}, date={}", 
                       officeLocation, buildingName, floorId, date);
            
            Optional<String> planData;
            if (date != null && !date.isEmpty()) {
                LocalDate bookingDate = LocalDate.parse(date);
                planData = dynamicFloorPlanService.getFloorPlanData(officeLocation, buildingName, floorId, bookingDate);
            } else {
                planData = dynamicFloorPlanService.getFloorPlanData(officeLocation, buildingName, floorId);
            }
            
            if (planData.isPresent()) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(true)
                        .message("Floor plan data retrieved successfully")
                        .data(Map.of(
                            "office_location", officeLocation,
                            "building_name", buildingName,
                            "floor_id", floorId,
                            "date", date != null ? date : LocalDate.now().toString(),
                            "plan_json", planData.get(),
                            "table_name", dynamicFloorPlanService.generateTableName(officeLocation, buildingName, floorId)
                        ))
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.builder()
                                .success(false)
                                .message("Floor plan data not found")
                                .build());
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving floor plan data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to retrieve floor plan data: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Check if floor plan exists
     */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse> checkFloorPlanExists(
            @RequestParam String officeLocation,
            @RequestParam String buildingName,
            @RequestParam String floorId) {
        try {
            boolean exists = dynamicFloorPlanService.floorPlanExists(officeLocation, buildingName, floorId);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Floor plan existence check completed")
                    .data(Map.of(
                        "exists", exists,
                        "office_location", officeLocation,
                        "building_name", buildingName,
                        "floor_id", floorId,
                        "table_name", dynamicFloorPlanService.generateTableName(officeLocation, buildingName, floorId)
                    ))
                    .build());
                    
        } catch (Exception e) {
            logger.error("Error checking floor plan existence: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to check floor plan existence: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get table metadata
     */
    @GetMapping("/metadata")
    public ResponseEntity<ApiResponse> getTableMetadata(
            @RequestParam String officeLocation,
            @RequestParam String buildingName,
            @RequestParam String floorId) {
        try {
            Map<String, Object> metadata = dynamicFloorPlanService.getTableMetadata(officeLocation, buildingName, floorId);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Table metadata retrieved successfully")
                    .data(Map.of(
                        "office_location", officeLocation,
                        "building_name", buildingName,
                        "floor_id", floorId,
                        "table_name", dynamicFloorPlanService.generateTableName(officeLocation, buildingName, floorId),
                        "metadata", metadata
                    ))
                    .build());
                    
        } catch (Exception e) {
            logger.error("Error retrieving table metadata: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to retrieve table metadata: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get all office locations
     */
    @GetMapping("/office-locations")
    public ResponseEntity<ApiResponse> getAllOfficeLocations() {
        try {
            List<String> officeLocations = dynamicFloorPlanService.getAllOfficeLocations();
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Office locations retrieved successfully")
                    .data(Map.of("office_locations", officeLocations))
                    .build());
                    
        } catch (Exception e) {
            logger.error("Error retrieving office locations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to retrieve office locations: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get buildings by office location
     */
    @GetMapping("/buildings")
    public ResponseEntity<ApiResponse> getBuildingsByOfficeLocation(@RequestParam String officeLocation) {
        try {
            List<String> buildings = dynamicFloorPlanService.getBuildingsByOfficeLocation(officeLocation);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Buildings retrieved successfully")
                    .data(Map.of(
                        "office_location", officeLocation,
                        "buildings", buildings
                    ))
                    .build());
                    
        } catch (Exception e) {
            logger.error("Error retrieving buildings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to retrieve buildings: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get floors by office location and building
     */
    @GetMapping("/floors")
    public ResponseEntity<ApiResponse> getFloorsByOfficeLocationAndBuilding(
            @RequestParam String officeLocation,
            @RequestParam String buildingName) {
        try {
            List<String> floors = dynamicFloorPlanService.getFloorsByOfficeLocationAndBuilding(officeLocation, buildingName);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Floors retrieved successfully")
                    .data(Map.of(
                        "office_location", officeLocation,
                        "building_name", buildingName,
                        "floors", floors
                    ))
                    .build());
                    
        } catch (Exception e) {
            logger.error("Error retrieving floors: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to retrieve floors: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Delete floor plan table
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteFloorPlanTable(
            @RequestParam String officeLocation,
            @RequestParam String buildingName,
            @RequestParam String floorId) {
        try {
            logger.info("Deleting floor plan table for: officeLocation={}, buildingName={}, floorId={}", 
                       officeLocation, buildingName, floorId);
            
            dynamicFloorPlanService.deleteFloorPlanTable(officeLocation, buildingName, floorId);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Floor plan table deleted successfully")
                    .data(Map.of(
                        "office_location", officeLocation,
                        "building_name", buildingName,
                        "floor_id", floorId,
                        "table_name", dynamicFloorPlanService.generateTableName(officeLocation, buildingName, floorId)
                    ))
                    .build());
                    
        } catch (Exception e) {
            logger.error("Error deleting floor plan table: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to delete floor plan table: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get booking status for a specific floor plan
     */
    @GetMapping("/booking-status")
    public ResponseEntity<ApiResponse> getBookingStatus(
            @RequestParam String officeLocation,
            @RequestParam String buildingName,
            @RequestParam String floorId,
            @RequestParam(required = false) String date) {
        try {
            LocalDate bookingDate = date != null ? LocalDate.parse(date) : LocalDate.now();
            
            // Get bookings for this location and date
            List<Booking> bookings = bookingRepository.findByOfficeLocationAndBuildingAndFloorAndDate(
                officeLocation, buildingName, floorId, bookingDate
            );
            
            List<String> bookedSeats = bookings.stream()
                .filter(booking -> "confirmed".equals(booking.getStatus()))
                .map(Booking::getSubType)
                .toList();
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Booking status retrieved successfully")
                    .data(Map.of(
                        "office_location", officeLocation,
                        "building_name", buildingName,
                        "floor_id", floorId,
                        "date", bookingDate.toString(),
                        "total_bookings", bookings.size(),
                        "confirmed_bookings", bookedSeats.size(),
                        "booked_seats", bookedSeats
                    ))
                    .build());
                    
        } catch (Exception e) {
            logger.error("Error retrieving booking status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to retrieve booking status: " + e.getMessage())
                            .build());
        }
    }
} 