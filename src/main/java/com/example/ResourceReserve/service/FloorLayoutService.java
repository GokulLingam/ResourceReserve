package com.example.ResourceReserve.service;

import com.example.ResourceReserve.dto.FloorLayoutRequest;
import com.example.ResourceReserve.dto.FloorLayoutResponse;
import com.example.ResourceReserve.entity.Floor;
import com.example.ResourceReserve.entity.FloorLayout;
import com.example.ResourceReserve.repository.FloorLayoutRepository;
import com.example.ResourceReserve.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FloorLayoutService {
    
    private final FloorLayoutRepository floorLayoutRepository;
    private final FloorRepository floorRepository;
    
    public FloorLayoutResponse getFloorLayout(UUID floorId) {
        log.info("Fetching layout for floor: {}", floorId);
        FloorLayout layout = floorLayoutRepository.findByFloorIdAndIsActive(floorId)
                .orElseThrow(() -> new RuntimeException("Floor layout not found for floor: " + floorId));
        return mapToResponse(layout);
    }
    
    public FloorLayoutResponse getFloorLayoutByVersion(UUID floorId, Integer version) {
        log.info("Fetching layout for floor: {} version: {}", floorId, version);
        FloorLayout layout = floorLayoutRepository.findByFloorIdAndVersion(floorId, version)
                .orElseThrow(() -> new RuntimeException("Floor layout not found for floor: " + floorId + " version: " + version));
        return mapToResponse(layout);
    }
    
    public FloorLayoutResponse createOrUpdateFloorLayout(UUID floorId, FloorLayoutRequest request) {
        log.info("Creating/updating layout for floor: {}", floorId);
        
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new RuntimeException("Floor not found with id: " + floorId));
        
        // Check if layout already exists
        FloorLayout existingLayout = floorLayoutRepository.findByFloorIdAndIsActive(floorId).orElse(null);
        
        if (existingLayout != null) {
            // Deactivate current layout
            existingLayout.setIsActive(false);
            floorLayoutRepository.save(existingLayout);
            
            // Create new version
            FloorLayout newLayout = FloorLayout.builder()
                    .floor(floor)
                    .layoutData(request.getLayoutData())
                    .version(existingLayout.getVersion() + 1)
                    .isActive(request.getIsActive())
                    .build();
            
            FloorLayout savedLayout = floorLayoutRepository.save(newLayout);
            log.info("Floor layout updated successfully with version: {}", savedLayout.getVersion());
            return mapToResponse(savedLayout);
        } else {
            // Create first layout
            FloorLayout layout = FloorLayout.builder()
                    .floor(floor)
                    .layoutData(request.getLayoutData())
                    .version(1)
                    .isActive(request.getIsActive())
                    .build();
            
            FloorLayout savedLayout = floorLayoutRepository.save(layout);
            log.info("Floor layout created successfully with version: {}", savedLayout.getVersion());
            return mapToResponse(savedLayout);
        }
    }
    
    public void deleteFloorLayout(UUID floorId) {
        log.info("Deleting layout for floor: {}", floorId);
        FloorLayout layout = floorLayoutRepository.findByFloorIdAndIsActive(floorId)
                .orElseThrow(() -> new RuntimeException("Floor layout not found for floor: " + floorId));
        
        layout.setIsActive(false);
        floorLayoutRepository.save(layout);
        log.info("Floor layout deleted successfully");
    }
    
    public FloorLayoutResponse getLatestFloorLayout(UUID floorId) {
        log.info("Fetching latest layout for floor: {}", floorId);
        FloorLayout layout = floorLayoutRepository.findLatestByFloorId(floorId)
                .orElseThrow(() -> new RuntimeException("Floor layout not found for floor: " + floorId));
        return mapToResponse(layout);
    }
    
    private FloorLayoutResponse mapToResponse(FloorLayout layout) {
        return FloorLayoutResponse.builder()
                .id(layout.getId())
                .floorId(layout.getFloor().getId())
                .layoutData(layout.getLayoutData())
                .version(layout.getVersion())
                .isActive(layout.getIsActive())
                .createdAt(layout.getCreatedAt())
                .updatedAt(layout.getUpdatedAt())
                .build();
    }
} 