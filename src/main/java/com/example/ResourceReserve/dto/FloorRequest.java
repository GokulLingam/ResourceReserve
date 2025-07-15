package com.example.ResourceReserve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorRequest {
    
    @NotNull(message = "Building ID is required")
    private UUID buildingId;
    
    @NotBlank(message = "Floor name is required")
    @Size(max = 255, message = "Floor name must not exceed 255 characters")
    private String name;
    
    @NotNull(message = "Floor number is required")
    private Integer floorNumber;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Builder.Default
    private Boolean isActive = true;
} 