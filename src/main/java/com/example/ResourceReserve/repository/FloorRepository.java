package com.example.ResourceReserve.repository;

import com.example.ResourceReserve.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FloorRepository extends JpaRepository<Floor, UUID> {
    
    @Query("SELECT f FROM Floor f WHERE f.building.id = :buildingId ORDER BY f.floorNumber")
    List<Floor> findByBuildingIdOrderByFloorNumber(@Param("buildingId") UUID buildingId);
    
    @Query("SELECT f FROM Floor f WHERE f.building.id = :buildingId AND f.isActive = :isActive ORDER BY f.floorNumber")
    List<Floor> findByBuildingIdAndIsActiveOrderByFloorNumber(@Param("buildingId") UUID buildingId, @Param("isActive") Boolean isActive);
    
    @Query("SELECT f FROM Floor f WHERE f.building.id = :buildingId AND f.floorNumber = :floorNumber")
    Optional<Floor> findByBuildingIdAndFloorNumber(@Param("buildingId") UUID buildingId, @Param("floorNumber") Integer floorNumber);
    
    @Query("SELECT f FROM Floor f WHERE f.isActive = :isActive ORDER BY f.building.name, f.floorNumber")
    List<Floor> findByIsActiveOrderByBuildingNameAndFloorNumber(@Param("isActive") Boolean isActive);
    
    @Query("SELECT COUNT(d) FROM Desk d WHERE d.floor.id = :floorId AND d.isActive = true")
    Long countActiveDesksByFloorId(@Param("floorId") UUID floorId);
    
    @Query("SELECT COUNT(d) FROM Desk d WHERE d.floor.id = :floorId AND d.isActive = true AND d.status = 'AVAILABLE'")
    Long countAvailableDesksByFloorId(@Param("floorId") UUID floorId);
} 