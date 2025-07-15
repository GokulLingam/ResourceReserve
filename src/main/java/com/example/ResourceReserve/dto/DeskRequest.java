package com.example.ResourceReserve.dto;

import com.example.ResourceReserve.entity.Desk;
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
public class DeskRequest {
    
    @NotNull(message = "Floor ID is required")
    private UUID floorId;
    
    @NotBlank(message = "Desk number is required")
    @Size(max = 50, message = "Desk number must not exceed 50 characters")
    private String deskNumber;
    
    @NotNull(message = "X position is required")
    private Integer xPosition;
    
    @NotNull(message = "Y position is required")
    private Integer yPosition;
    
    @Builder.Default
    private Integer width = 100;
    
    @Builder.Default
    private Integer height = 100;
    
    @Builder.Default
    private Desk.DeskStatus status = Desk.DeskStatus.AVAILABLE;
    
    @Builder.Default
    private Desk.DeskType deskType = Desk.DeskType.STANDARD;
    
    private String equipment; // JSON string for equipment
    
    @Builder.Default
    private Boolean isActive = true;
} 