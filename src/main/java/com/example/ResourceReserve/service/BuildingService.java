package com.example.ResourceReserve.service;

import com.example.ResourceReserve.dto.BuildingRequest;
import com.example.ResourceReserve.dto.BuildingResponse;
import com.example.ResourceReserve.entity.Building;
import com.example.ResourceReserve.repository.BuildingRepository;
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
public class BuildingService {
    
    private final BuildingRepository buildingRepository;
    
    public List<BuildingResponse> getAllBuildings() {
        log.info("Fetching all buildings");
        List<Building> buildings = buildingRepository.findAllOrderByName();
        return buildings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public BuildingResponse getBuildingById(UUID id) {
        log.info("Fetching building with id: {}", id);
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + id));
        return mapToResponse(building);
    }
    
    public BuildingResponse createBuilding(BuildingRequest request) {
        log.info("Creating new building: {}", request.getName());
        Building building = Building.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .build();
        
        Building savedBuilding = buildingRepository.save(building);
        log.info("Building created successfully with id: {}", savedBuilding.getId());
        return mapToResponse(savedBuilding);
    }
    
    public BuildingResponse updateBuilding(UUID id, BuildingRequest request) {
        log.info("Updating building with id: {}", id);
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + id));
        
        building.setName(request.getName());
        building.setAddress(request.getAddress());
        building.setCity(request.getCity());
        building.setCountry(request.getCountry());
        
        Building updatedBuilding = buildingRepository.save(building);
        log.info("Building updated successfully");
        return mapToResponse(updatedBuilding);
    }
    
    public void deleteBuilding(UUID id) {
        log.info("Deleting building with id: {}", id);
        if (!buildingRepository.existsById(id)) {
            throw new RuntimeException("Building not found with id: " + id);
        }
        buildingRepository.deleteById(id);
        log.info("Building deleted successfully");
    }
    
    public List<BuildingResponse> getBuildingsByCity(String city) {
        log.info("Fetching buildings in city: {}", city);
        List<Building> buildings = buildingRepository.findByCityOrderByName(city);
        return buildings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<BuildingResponse> getBuildingsByCountry(String country) {
        log.info("Fetching buildings in country: {}", country);
        List<Building> buildings = buildingRepository.findByCountryOrderByName(country);
        return buildings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private BuildingResponse mapToResponse(Building building) {
        return BuildingResponse.builder()
                .id(building.getId())
                .name(building.getName())
                .address(building.getAddress())
                .city(building.getCity())
                .country(building.getCountry())
                .createdAt(building.getCreatedAt())
                .updatedAt(building.getUpdatedAt())
                .build();
    }
} 