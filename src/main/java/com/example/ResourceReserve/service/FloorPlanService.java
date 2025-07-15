package com.example.ResourceReserve.service;

import com.example.ResourceReserve.dto.FloorPlanResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FloorPlanService {
    
    private final DynamicFloorPlanService dynamicFloorPlanService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public FloorPlanResponse getFloorPlan(String building, String office, String floor, String date) {
        log.info("Getting floor plan for building: {}, office: {}, floor: {}, date: {}", building, office, floor, date);
        
        try {
            // Get floor plan data from dynamic table
            Optional<String> planData;
            if (date != null && !date.isEmpty()) {
                LocalDate bookingDate = LocalDate.parse(date);
                planData = dynamicFloorPlanService.getFloorPlanData(office, building, floor, bookingDate);
            } else {
                planData = dynamicFloorPlanService.getFloorPlanData(office, building, floor);
            }
            
            if (planData.isPresent()) {
                // Parse the JSON data and convert to FloorPlanResponse
                return parseFloorPlanData(planData.get(), building, office, floor);
            } else {
                log.warn("No floor plan data found for: office={}, building={}, floor={}, date={}", office, building, floor, date);
                return createDefaultFloorPlan();
            }
            
        } catch (Exception e) {
            log.error("Error getting floor plan: {}", e.getMessage(), e);
            return createDefaultFloorPlan();
        }
    }
    
    private FloorPlanResponse parseFloorPlanData(String planJson, String building, String office, String floor) {
        try {
            JsonNode rootNode = objectMapper.readTree(planJson);
            
            List<FloorPlanResponse.Seat> seats = new ArrayList<>();
            List<FloorPlanResponse.DeskArea> deskAreas = new ArrayList<>();
            
            // Parse seats
            if (rootNode.has("seats") && rootNode.get("seats").isArray()) {
                for (JsonNode seatNode : rootNode.get("seats")) {
                    FloorPlanResponse.Seat seat = FloorPlanResponse.Seat.builder()
                            .id(seatNode.has("id") ? seatNode.get("id").asText() : UUID.randomUUID().toString())
                            .x(seatNode.has("x") ? seatNode.get("x").asDouble() : 0.0)
                            .y(seatNode.has("y") ? seatNode.get("y").asDouble() : 0.0)
                            .status(seatNode.has("status") ? seatNode.get("status").asText() : "available")
                            .type(seatNode.has("type") ? seatNode.get("type").asText() : "desk")
                            .equipment(parseEquipmentFromJson(seatNode))
                            .rotation(seatNode.has("rotation") ? seatNode.get("rotation").asInt() : 0)
                            .build();
                    seats.add(seat);
                }
            }
            
            // Parse desk areas
            if (rootNode.has("deskAreas") && rootNode.get("deskAreas").isArray()) {
                for (JsonNode areaNode : rootNode.get("deskAreas")) {
                    FloorPlanResponse.DeskArea area = FloorPlanResponse.DeskArea.builder()
                            .id(areaNode.has("id") ? areaNode.get("id").asText() : "area1")
                            .name(areaNode.has("name") ? areaNode.get("name").asText() : "Main Workspace")
                            .x(areaNode.has("x") ? areaNode.get("x").asDouble() : 1.6101207354199758)
                            .y(areaNode.has("y") ? areaNode.get("y").asDouble() : 1.869158965001005)
                            .width(areaNode.has("width") ? areaNode.get("width").asInt() : 60)
                            .height(areaNode.has("height") ? areaNode.get("height").asInt() : 40)
                            .type(areaNode.has("type") ? areaNode.get("type").asText() : "workspace")
                            .rotation(areaNode.has("rotation") ? areaNode.get("rotation").asInt() : 0)
                            .build();
                    deskAreas.add(area);
                }
            }
            
            // Create default desk area if none found
            if (deskAreas.isEmpty()) {
                deskAreas.add(FloorPlanResponse.DeskArea.builder()
                        .id("area1")
                        .name("Main Workspace")
                        .x(1.6101207354199758)
                        .y(1.869158965001005)
                        .width(60)
                        .height(40)
                        .type("workspace")
                        .rotation(0)
                        .build());
            }
            
            // Create office layout
            FloorPlanResponse.OfficeLayout officeLayout = FloorPlanResponse.OfficeLayout.builder()
                    .x(0)
                    .y(0)
                    .width(210)
                    .height(82)
                    .fillColor("hsl(var(--muted))")
                    .fillOpacity(0.1)
                    .strokeColor("hsl(var(--border))")
                    .strokeWidth(1)
                    .build();
            
            log.info("Successfully parsed floor plan with {} seats", seats.size());
            return FloorPlanResponse.builder()
                    .seats(seats)
                    .deskAreas(deskAreas)
                    .officeLayout(officeLayout)
                    .build();
                    
        } catch (JsonProcessingException e) {
            log.error("Error parsing floor plan JSON data: {}", e.getMessage(), e);
            return createDefaultFloorPlan();
        }
    }
    
    private List<String> parseEquipmentFromJson(JsonNode seatNode) {
        List<String> equipment = new ArrayList<>();
        
        if (seatNode.has("equipment") && seatNode.get("equipment").isArray()) {
            seatNode.get("equipment").forEach(item -> equipment.add(item.asText()));
        }
        
        return equipment;
    }
    

    
    private FloorPlanResponse createDefaultFloorPlan() {
        // Return a default floor plan when no data is found
        List<FloorPlanResponse.Seat> seats = Arrays.asList(
                FloorPlanResponse.Seat.builder()
                        .id("D1")
                        .x(7)
                        .y(5)
                        .status("available")
                        .type("desk")
                        .equipment(Arrays.asList("Monitor", "Dock", "Window Seat"))
                        .rotation(0)
                        .build(),
                FloorPlanResponse.Seat.builder()
                        .id("D2")
                        .x(7.730657859981704)
                        .y(19.892523418729393)
                        .status("available")
                        .type("desk")
                        .equipment(new ArrayList<>())
                        .rotation(0)
                        .build()
        );
        
        List<FloorPlanResponse.DeskArea> deskAreas = Arrays.asList(
                FloorPlanResponse.DeskArea.builder()
                        .id("area1")
                        .name("Main Workspace")
                        .x(1.6101207354199758)
                        .y(1.869158965001005)
                        .width(60)
                        .height(40)
                        .type("workspace")
                        .rotation(0)
                        .build()
        );
        
        FloorPlanResponse.OfficeLayout officeLayout = FloorPlanResponse.OfficeLayout.builder()
                .x(0)
                .y(0)
                .width(210)
                .height(82)
                .fillColor("hsl(var(--muted))")
                .fillOpacity(0.1)
                .strokeColor("hsl(var(--border))")
                .strokeWidth(1)
                .build();
        
        return FloorPlanResponse.builder()
                .seats(seats)
                .deskAreas(deskAreas)
                .officeLayout(officeLayout)
                .build();
    }
} 