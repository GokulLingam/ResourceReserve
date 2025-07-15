package com.example.ResourceReserve.controller;

import com.example.ResourceReserve.dto.FloorPlanResponse;
import com.example.ResourceReserve.dto.DynamicFloorPlanRequest;
import com.example.ResourceReserve.dto.ApiResponse;
import com.example.ResourceReserve.service.FloorPlanService;
import com.example.ResourceReserve.service.DynamicFloorPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/floorplan", "/floorplan"})
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FloorPlanController {
    
    private final FloorPlanService floorPlanService;
    private final DynamicFloorPlanService dynamicFloorPlanService;
    
    @GetMapping
    public ResponseEntity<FloorPlanResponse> getFloorPlan(
            @RequestParam String building,
            @RequestParam String office,
            @RequestParam String floor,
            @RequestParam(required = false) String date) {
        
        log.info("Floor plan request - Building: {}, Office: {}, Floor: {}, Date: {}", building, office, floor, date);
        
        try {
            FloorPlanResponse floorPlan = floorPlanService.getFloorPlan(building, office, floor, date);
            return ResponseEntity.ok(floorPlan);
        } catch (Exception e) {
            log.error("Error getting floor plan for building: {}, office: {}, floor: {}, date: {}", building, office, floor, date, e);
            // Return default floor plan on error
            return ResponseEntity.ok(floorPlanService.getFloorPlan("", "", "", null));
        }
    }
    
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveFloorPlan(@RequestBody DynamicFloorPlanRequest request) {
        log.info("Saving floor plan - Office: {}, Building: {}, Floor: {}", 
                request.getOfficeLocation(), request.getBuildingName(), request.getFloorId());
        
        try {
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
            log.error("Error saving floor plan data: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Failed to save floor plan data: " + e.getMessage())
                            .build());
        }
    }
} 