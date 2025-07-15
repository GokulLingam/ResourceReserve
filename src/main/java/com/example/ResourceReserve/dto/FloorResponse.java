package com.example.ResourceReserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorResponse {
    
    private UUID id;
    private UUID buildingId;
    private String buildingName;
    private String name;
    private Integer floorNumber;
    private String description;
    private Boolean isActive;
    private Long deskCount;
    private Long availableDesks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 