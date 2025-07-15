package com.example.ResourceReserve.service;

import com.example.ResourceReserve.dto.DeskRequest;
import com.example.ResourceReserve.dto.DeskResponse;
import com.example.ResourceReserve.entity.Desk;
import com.example.ResourceReserve.entity.Floor;
import com.example.ResourceReserve.repository.DeskRepository;
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
public class DeskService {
    
    private final DeskRepository deskRepository;
    private final FloorRepository floorRepository;
    
    public List<DeskResponse> getAllDesks() {
        log.info("Fetching all desks");
        List<Desk> desks = deskRepository.findByFloorIdAndIsActiveOrderByDeskNumber(null, true);
        return desks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DeskResponse> getDesksByFloor(UUID floorId) {
        log.info("Fetching desks for floor: {}", floorId);
        List<Desk> desks = deskRepository.findByFloorIdAndIsActiveOrderByDeskNumber(floorId, true);
        return desks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DeskResponse> getDesksByStatus(Desk.DeskStatus status) {
        log.info("Fetching desks with status: {}", status);
        List<Desk> desks = deskRepository.findByStatusAndIsActiveOrderByBuildingAndFloorAndDesk(status);
        return desks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DeskResponse> getDesksByType(Desk.DeskType deskType) {
        log.info("Fetching desks with type: {}", deskType);
        List<Desk> desks = deskRepository.findByDeskTypeAndIsActiveOrderByBuildingAndFloorAndDesk(deskType);
        return desks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DeskResponse> getAvailableDesksByFloor(UUID floorId) {
        log.info("Fetching available desks for floor: {}", floorId);
        List<Desk> desks = deskRepository.findByFloorIdAndStatusAndIsActiveOrderByDeskNumber(floorId, Desk.DeskStatus.AVAILABLE);
        return desks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public DeskResponse getDeskById(UUID id) {
        log.info("Fetching desk with id: {}", id);
        Desk desk = deskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Desk not found with id: " + id));
        return mapToResponse(desk);
    }
    
    public DeskResponse createDesk(DeskRequest request) {
        log.info("Creating new desk: {} for floor: {}", request.getDeskNumber(), request.getFloorId());
        
        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new RuntimeException("Floor not found with id: " + request.getFloorId()));
        
        // Check if desk number already exists for this floor
        deskRepository.findByFloorIdAndDeskNumber(request.getFloorId(), request.getDeskNumber())
                .ifPresent(desk -> {
                    throw new RuntimeException("Desk number " + request.getDeskNumber() + " already exists for this floor");
                });
        
        Desk desk = Desk.builder()
                .floor(floor)
                .deskNumber(request.getDeskNumber())
                .xPosition(request.getXPosition())
                .yPosition(request.getYPosition())
                .width(request.getWidth())
                .height(request.getHeight())
                .status(request.getStatus())
                .deskType(request.getDeskType())
                .equipment(request.getEquipment())
                .isActive(request.getIsActive())
                .build();
        
        Desk savedDesk = deskRepository.save(desk);
        log.info("Desk created successfully with id: {}", savedDesk.getId());
        return mapToResponse(savedDesk);
    }
    
    public DeskResponse updateDesk(UUID id, DeskRequest request) {
        log.info("Updating desk with id: {}", id);
        Desk desk = deskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Desk not found with id: " + id));
        
        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new RuntimeException("Floor not found with id: " + request.getFloorId()));
        
        // Check if desk number already exists for this floor (excluding current desk)
        deskRepository.findByFloorIdAndDeskNumber(request.getFloorId(), request.getDeskNumber())
                .ifPresent(existingDesk -> {
                    if (!existingDesk.getId().equals(id)) {
                        throw new RuntimeException("Desk number " + request.getDeskNumber() + " already exists for this floor");
                    }
                });
        
        desk.setFloor(floor);
        desk.setDeskNumber(request.getDeskNumber());
        desk.setXPosition(request.getXPosition());
        desk.setYPosition(request.getYPosition());
        desk.setWidth(request.getWidth());
        desk.setHeight(request.getHeight());
        desk.setStatus(request.getStatus());
        desk.setDeskType(request.getDeskType());
        desk.setEquipment(request.getEquipment());
        desk.setIsActive(request.getIsActive());
        
        Desk updatedDesk = deskRepository.save(desk);
        log.info("Desk updated successfully");
        return mapToResponse(updatedDesk);
    }
    
    public void deleteDesk(UUID id) {
        log.info("Deleting desk with id: {}", id);
        if (!deskRepository.existsById(id)) {
            throw new RuntimeException("Desk not found with id: " + id);
        }
        deskRepository.deleteById(id);
        log.info("Desk deleted successfully");
    }
    
    public DeskResponse updateDeskStatus(UUID id, Desk.DeskStatus status) {
        log.info("Updating desk status to: {} for desk: {}", status, id);
        Desk desk = deskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Desk not found with id: " + id));
        
        desk.setStatus(status);
        Desk updatedDesk = deskRepository.save(desk);
        log.info("Desk status updated successfully");
        return mapToResponse(updatedDesk);
    }
    
    private DeskResponse mapToResponse(Desk desk) {
        return DeskResponse.builder()
                .id(desk.getId())
                .floorId(desk.getFloor().getId())
                .floorName(desk.getFloor().getName())
                .deskNumber(desk.getDeskNumber())
                .xPosition(desk.getXPosition())
                .yPosition(desk.getYPosition())
                .width(desk.getWidth())
                .height(desk.getHeight())
                .status(desk.getStatus())
                .deskType(desk.getDeskType())
                .equipment(desk.getEquipment())
                .isActive(desk.getIsActive())
                .createdAt(desk.getCreatedAt())
                .updatedAt(desk.getUpdatedAt())
                .build();
    }
} 