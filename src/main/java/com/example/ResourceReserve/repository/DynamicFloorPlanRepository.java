package com.example.ResourceReserve.repository;

import com.example.ResourceReserve.entity.DynamicFloorPlanTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DynamicFloorPlanRepository extends JpaRepository<DynamicFloorPlanTable, UUID> {
    
    // Find by office location, building name, and floor ID
    Optional<DynamicFloorPlanTable> findByOfficeLocationAndBuildingNameAndFloorId(
            String officeLocation, String buildingName, String floorId);
    
    // Find all by office location and building name
    List<DynamicFloorPlanTable> findByOfficeLocationAndBuildingName(String officeLocation, String buildingName);
    
    // Find all by office location
    List<DynamicFloorPlanTable> findByOfficeLocation(String officeLocation);
    
    // Check if table exists for given parameters
    boolean existsByOfficeLocationAndBuildingNameAndFloorId(String officeLocation, String buildingName, String floorId);
    
    // Custom query to get all unique office locations
    @Query("SELECT DISTINCT d.officeLocation FROM DynamicFloorPlanTable d")
    List<String> findAllOfficeLocations();
    
    // Custom query to get all buildings for a specific office location
    @Query("SELECT DISTINCT d.buildingName FROM DynamicFloorPlanTable d WHERE d.officeLocation = :officeLocation")
    List<String> findBuildingsByOfficeLocation(@Param("officeLocation") String officeLocation);
    
    // Custom query to get all floors for a specific office location and building
    @Query("SELECT DISTINCT d.floorId FROM DynamicFloorPlanTable d WHERE d.officeLocation = :officeLocation AND d.buildingName = :buildingName")
    List<String> findFloorsByOfficeLocationAndBuilding(@Param("officeLocation") String officeLocation, @Param("buildingName") String buildingName);
} 