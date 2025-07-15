package com.example.ResourceReserve.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DynamicFloorPlanRequest {
    
    @NotBlank(message = "Office location is required")
    @JsonProperty("office_location")
    private String officeLocation;
    
    @NotBlank(message = "Building name is required")
    @JsonProperty("building_name")
    private String buildingName;
    
    @NotBlank(message = "Floor ID is required")
    @JsonProperty("floor_id")
    private String floorId;
    
    @NotNull(message = "Plan JSON data is required")
    @JsonProperty("plan_json")
    private String planJson;
    
    // Default constructor
    public DynamicFloorPlanRequest() {}
    
    // Constructor with parameters
    public DynamicFloorPlanRequest(String officeLocation, String buildingName, String floorId, String planJson) {
        this.officeLocation = officeLocation;
        this.buildingName = buildingName;
        this.floorId = floorId;
        this.planJson = planJson;
    }
    
    // Getters and Setters
    public String getOfficeLocation() {
        return officeLocation;
    }
    
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
    
    public String getBuildingName() {
        return buildingName;
    }
    
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
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
    
    @Override
    public String toString() {
        return "DynamicFloorPlanRequest{" +
                "officeLocation='" + officeLocation + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", floorId='" + floorId + '\'' +
                ", planJson='" + planJson + '\'' +
                '}';
    }
} 