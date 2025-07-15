package com.example.ResourceReserve.dto;

import com.example.ResourceReserve.entity.Desk;
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
public class DeskResponse {
    
    private UUID id;
    private UUID floorId;
    private String floorName;
    private String deskNumber;
    private Integer xPosition;
    private Integer yPosition;
    private Integer width;
    private Integer height;
    private Desk.DeskStatus status;
    private Desk.DeskType deskType;
    private String equipment;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 