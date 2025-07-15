package com.example.ResourceReserve.service;

import com.example.ResourceReserve.dto.FloorRequest;
import com.example.ResourceReserve.dto.FloorResponse;
import com.example.ResourceReserve.entity.Building;
import com.example.ResourceReserve.entity.Floor;
import com.example.ResourceReserve.repository.BuildingRepository;
import com.example.ResourceReserve.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FloorService {
    
    private final FloorRepository floorRepository;
    private final BuildingRepository buildingRepository;
    
    public List<FloorResponse> getAllFloors() {
        log.info("Fetching all floors");
        List<Floor> floors = floorRepository.findByIsActiveOrderByBuildingNameAndFloorNumber(true);
        return floors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<FloorResponse> getFloorsByBuilding(UUID buildingId) {
        log.info("Fetching floors for building: {}", buildingId);
        List<Floor> floors = floorRepository.findByBuildingIdAndIsActiveOrderByFloorNumber(buildingId, true);
        return floors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public FloorResponse getFloorById(UUID id) {
        log.info("Fetching floor with id: {}", id);
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found with id: " + id));
        return mapToResponse(floor);
    }
    
    public FloorResponse createFloor(FloorRequest request) {
        log.info("Creating new floor: {} for building: {}", request.getName(), request.getBuildingId());
        
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + request.getBuildingId()));
        
        // Check if floor number already exists for this building
        floorRepository.findByBuildingIdAndFloorNumber(request.getBuildingId(), request.getFloorNumber())
                .ifPresent(floor -> {
                    throw new RuntimeException("Floor number " + request.getFloorNumber() + " already exists for this building");
                });
        
        Floor floor = Floor.builder()
                .building(building)
                .name(request.getName())
                .floorNumber(request.getFloorNumber())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .build();
        
        Floor savedFloor = floorRepository.save(floor);
        log.info("Floor created successfully with id: {}", savedFloor.getId());
        return mapToResponse(savedFloor);
    }
    
    public FloorResponse updateFloor(UUID id, FloorRequest request) {
        log.info("Updating floor with id: {}", id);
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found with id: " + id));
        
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + request.getBuildingId()));
        
        // Check if floor number already exists for this building (excluding current floor)
        floorRepository.findByBuildingIdAndFloorNumber(request.getBuildingId(), request.getFloorNumber())
                .ifPresent(existingFloor -> {
                    if (!existingFloor.getId().equals(id)) {
                        throw new RuntimeException("Floor number " + request.getFloorNumber() + " already exists for this building");
                    }
                });
        
        floor.setBuilding(building);
        floor.setName(request.getName());
        floor.setFloorNumber(request.getFloorNumber());
        floor.setDescription(request.getDescription());
        floor.setIsActive(request.getIsActive());
        
        Floor updatedFloor = floorRepository.save(floor);
        log.info("Floor updated successfully");
        return mapToResponse(updatedFloor);
    }
    
    public void deleteFloor(UUID id) {
        log.info("Deleting floor with id: {}", id);
        if (!floorRepository.existsById(id)) {
            throw new RuntimeException("Floor not found with id: " + id);
        }
        floorRepository.deleteById(id);
        log.info("Floor deleted successfully");
    }
    
    public List<FloorResponse> getActiveFloors() {
        log.info("Fetching active floors");
        List<Floor> floors = floorRepository.findByIsActiveOrderByBuildingNameAndFloorNumber(true);
        return floors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private FloorResponse mapToResponse(Floor floor) {
        Long deskCount = floorRepository.countActiveDesksByFloorId(floor.getId());
        Long availableDesks = floorRepository.countAvailableDesksByFloorId(floor.getId());
        
        return FloorResponse.builder()
                .id(floor.getId())
                .buildingId(floor.getBuilding().getId())
                .buildingName(floor.getBuilding().getName())
                .name(floor.getName())
                .floorNumber(floor.getFloorNumber())
                .description(floor.getDescription())
                .isActive(floor.getIsActive())
                .deskCount(deskCount)
                .availableDesks(availableDesks)
                .createdAt(floor.getCreatedAt())
                .updatedAt(floor.getUpdatedAt())
                .build();
    }
} 