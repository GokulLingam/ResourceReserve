package com.example.ResourceReserve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorLayoutRequest {
    
    @NotBlank(message = "Layout data is required")
    private String layoutData; // JSON string containing layout information
    
    @Builder.Default
    private Boolean isActive = true;
} 