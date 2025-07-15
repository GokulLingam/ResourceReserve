package com.example.ResourceReserve.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dynamic_floor_plan_table")
public class DynamicFloorPlanTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "building_name", nullable = false)
    private String buildingName;
    
    @Column(name = "office_location", nullable = false)
    private String officeLocation;
    
    @Column(name = "floor_id", nullable = false)
    private String floorId;
    
    @Column(name = "plan_json", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String planJson;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public DynamicFloorPlanTable() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public DynamicFloorPlanTable(String buildingName, String officeLocation, String floorId, String planJson) {
        this.buildingName = buildingName;
        this.officeLocation = officeLocation;
        this.floorId = floorId;
        this.planJson = planJson;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getBuildingName() {
        return buildingName;
    }
    
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
    
    public String getOfficeLocation() {
        return officeLocation;
    }
    
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
    
    public String getFloorId() {
        return floorId;
    }
    
    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }
    
    public String getPlanJson() {
        return planJson;
    }
    
    public void setPlanJson(String planJson) {
        this.planJson = planJson;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Method to generate table name
    public String generateTableName() {
        return officeLocation + "_" + buildingName + "_Floor" + floorId + "_table";
    }
} 