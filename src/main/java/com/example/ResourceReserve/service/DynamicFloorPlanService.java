package com.example.ResourceReserve.service;

import com.example.ResourceReserve.entity.DynamicFloorPlanTable;
import com.example.ResourceReserve.repository.DynamicFloorPlanRepository;
import com.example.ResourceReserve.repository.BookingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DynamicFloorPlanService {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicFloorPlanService.class);
    
    @Autowired
    private DynamicFloorPlanRepository repository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Generate table name based on office location, building name, and floor ID
     * Removes spaces and special characters to ensure valid SQL table names
     */
    public String generateTableName(String officeLocation, String buildingName, String floorId) {
        // Remove spaces and replace with underscores, also remove other special characters
        String cleanOfficeLocation = officeLocation.replaceAll("[^a-zA-Z0-9]", "_");
        String cleanBuildingName = buildingName.replaceAll("[^a-zA-Z0-9]", "_");
        
        // Clean floor ID and remove "Floor" prefix if it exists to avoid duplication
        String cleanFloorId = floorId.replaceAll("[^a-zA-Z0-9]", "_");
        if (cleanFloorId.toLowerCase().startsWith("floor")) {
            cleanFloorId = cleanFloorId.substring(5); // Remove "Floor" prefix
        }
        
        return cleanOfficeLocation + "_" + cleanBuildingName + "_Floor" + cleanFloorId + "_table";
    }
    
    /**
     * Create a new dynamic table if it doesn't exist
     */
    @Transactional
    public void createTableIfNotExists(String officeLocation, String buildingName, String floorId) {
        String tableName = generateTableName(officeLocation, buildingName, floorId);
        
        String createTableSQL = String.format(
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='%s' AND xtype='U') " +
            "CREATE TABLE %s (" +
            "id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(), " +
            "building_name NVARCHAR(255) NOT NULL, " +
            "office_location NVARCHAR(255) NOT NULL, " +
            "floor_id NVARCHAR(255) NOT NULL, " +
            "plan_json NVARCHAR(MAX) NOT NULL, " +
            "updated_at DATETIME DEFAULT GETDATE()" +
            ")", tableName, tableName);
        
        try {
            jdbcTemplate.execute(createTableSQL);
            logger.info("Table {} created successfully or already exists", tableName);
        } catch (Exception e) {
            logger.error("Error creating table {}: {}", tableName, e.getMessage());
            throw new RuntimeException("Failed to create table: " + tableName, e);
        }
    }
    
    /**
     * Insert or update floor plan data in the dynamic table
     */
    @Transactional
    public void saveFloorPlanData(String officeLocation, String buildingName, String floorId, String planJson) {
        // First, ensure the table exists
        createTableIfNotExists(officeLocation, buildingName, floorId);
        
        String tableName = generateTableName(officeLocation, buildingName, floorId);
        
        // Check if data already exists
        String checkSQL = String.format(
            "SELECT COUNT(*) FROM %s WHERE office_location = ? AND building_name = ? AND floor_id = ?",
            tableName);
        
        int count = jdbcTemplate.queryForObject(checkSQL, Integer.class, officeLocation, buildingName, floorId);
        
        if (count > 0) {
            // Update existing record
            String updateSQL = String.format(
                "UPDATE %s SET plan_json = ?, updated_at = GETDATE() " +
                "WHERE office_location = ? AND building_name = ? AND floor_id = ?",
                tableName);
            
            jdbcTemplate.update(updateSQL, planJson, officeLocation, buildingName, floorId);
            logger.info("Updated floor plan data for table: {}", tableName);
        } else {
            // Insert new record
            String insertSQL = String.format(
                "INSERT INTO %s (building_name, office_location, floor_id, plan_json) " +
                "VALUES (?, ?, ?, ?)",
                tableName);
            
            jdbcTemplate.update(insertSQL, buildingName, officeLocation, floorId, planJson);
            logger.info("Inserted new floor plan data for table: {}", tableName);
        }
    }
    
    /**
     * Retrieve floor plan data from the dynamic table
     */
    public Optional<String> getFloorPlanData(String officeLocation, String buildingName, String floorId) {
        return getFloorPlanData(officeLocation, buildingName, floorId, LocalDate.now());
    }
    
    /**
     * Retrieve floor plan data from the dynamic table with booking status for a specific date
     */
    public Optional<String> getFloorPlanData(String officeLocation, String buildingName, String floorId, LocalDate date) {
        String tableName = generateTableName(officeLocation, buildingName, floorId);
        
        // Check if table exists
        String tableExistsSQL = 
            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?";
        
        int tableCount = jdbcTemplate.queryForObject(tableExistsSQL, Integer.class, tableName);
        
        if (tableCount == 0) {
            logger.warn("Table {} does not exist", tableName);
            return Optional.empty();
        }
        
        // Retrieve data without WHERE conditions
        String selectSQL = String.format(
            "SELECT plan_json FROM %s",
            tableName);
        
        try {
            String planJson = jdbcTemplate.queryForObject(selectSQL, String.class);
            if (planJson != null) {
                // Update seat status based on bookings
                String updatedPlanJson = updateSeatStatusWithBookings(planJson, officeLocation, buildingName, floorId, date);
                return Optional.of(updatedPlanJson);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.warn("No data found for table {}", tableName);
            return Optional.empty();
        }
    }
    
    /**
     * Update seat status in floor plan JSON based on booking data
     */
    private String updateSeatStatusWithBookings(String planJson, String officeLocation, String buildingName, String floorId, LocalDate date) {
        try {
            JsonNode planNode = objectMapper.readTree(planJson);
            
            // Get all active bookings for this location and date
            List<String> bookedSeats = bookingRepository.findByOfficeLocationAndBuildingAndFloorAndDate(
                officeLocation, buildingName, floorId, date
            ).stream()
            .filter(booking -> "confirmed".equals(booking.getStatus()))
            .map(booking -> booking.getSubType())
            .toList();
            
            logger.info("Found {} booked seats for date {}: {}", bookedSeats.size(), date, bookedSeats);
            
            // Update seats in the JSON
            if (planNode.has("seats") && planNode.get("seats").isArray()) {
                ArrayNode seatsArray = (ArrayNode) planNode.get("seats");
                
                for (JsonNode seatNode : seatsArray) {
                    if (seatNode.has("id")) {
                        String seatId = seatNode.get("id").asText();
                        
                        // Check if this seat is booked
                        if (bookedSeats.contains(seatId)) {
                            ((ObjectNode) seatNode).put("status", "occupied");
                            logger.debug("Marked seat {} as occupied", seatId);
                        } else {
                            // Ensure status is set to available if not booked
                            ((ObjectNode) seatNode).put("status", "available");
                        }
                    }
                }
            }
            
            return objectMapper.writeValueAsString(planNode);
            
        } catch (Exception e) {
            logger.error("Error updating seat status with bookings: {}", e.getMessage(), e);
            // Return original JSON if there's an error
            return planJson;
        }
    }
    
    /**
     * Get all available office locations
     */
    public List<String> getAllOfficeLocations() {
        return repository.findAllOfficeLocations();
    }
    
    /**
     * Get all buildings for a specific office location
     */
    public List<String> getBuildingsByOfficeLocation(String officeLocation) {
        return repository.findBuildingsByOfficeLocation(officeLocation);
    }
    
    /**
     * Get all floors for a specific office location and building
     */
    public List<String> getFloorsByOfficeLocationAndBuilding(String officeLocation, String buildingName) {
        return repository.findFloorsByOfficeLocationAndBuilding(officeLocation, buildingName);
    }
    
    /**
     * Check if a specific floor plan exists
     */
    public boolean floorPlanExists(String officeLocation, String buildingName, String floorId) {
        String tableName = generateTableName(officeLocation, buildingName, floorId);
        
        // Check if table exists
        String tableExistsSQL = 
            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?";
        
        int tableCount = jdbcTemplate.queryForObject(tableExistsSQL, Integer.class, tableName);
        
        if (tableCount == 0) {
            return false;
        }
        
        // Check if data exists
        String dataExistsSQL = String.format(
            "SELECT COUNT(*) FROM %s WHERE office_location = ? AND building_name = ? AND floor_id = ?",
            tableName);
        
        int dataCount = jdbcTemplate.queryForObject(dataExistsSQL, Integer.class, officeLocation, buildingName, floorId);
        return dataCount > 0;
    }
    
    /**
     * Delete a floor plan table and its data
     */
    @Transactional
    public void deleteFloorPlanTable(String officeLocation, String buildingName, String floorId) {
        String tableName = generateTableName(officeLocation, buildingName, floorId);
        
        String dropTableSQL = String.format("DROP TABLE IF EXISTS %s", tableName);
        
        try {
            jdbcTemplate.execute(dropTableSQL);
            logger.info("Table {} dropped successfully", tableName);
        } catch (Exception e) {
            logger.error("Error dropping table {}: {}", tableName, e.getMessage());
            throw new RuntimeException("Failed to drop table: " + tableName, e);
        }
    }
    
    /**
     * Get table metadata for a specific table
     */
    public Map<String, Object> getTableMetadata(String officeLocation, String buildingName, String floorId) {
        String tableName = generateTableName(officeLocation, buildingName, floorId);
        
        String metadataSQL = String.format(
            "SELECT COUNT(*) as record_count, MAX(updated_at) as last_updated " +
            "FROM %s WHERE office_location = ? AND building_name = ? AND floor_id = ?",
            tableName);
        
        try {
            return jdbcTemplate.queryForMap(metadataSQL, officeLocation, buildingName, floorId);
        } catch (Exception e) {
            logger.warn("Could not retrieve metadata for table {}: {}", tableName, e.getMessage());
            return Map.of("record_count", 0, "last_updated", null);
        }
    }
} 