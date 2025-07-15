package com.example.ResourceReserve.dto;

import com.example.ResourceReserve.entity.Booking.BookType;
import com.example.ResourceReserve.entity.Booking.RecurrenceType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BookingRequest {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("date")
    private String date;
    
    @JsonProperty("startTime")
    private String startTime;
    
    @JsonProperty("endTime")
    private String endTime;
    
    @JsonProperty("bookType")
    private BookType bookType;
    
    @JsonProperty("subType")
    private String subType;
    
    @JsonProperty("officeLocation")
    private String officeLocation;
    
    @JsonProperty("building")
    private String building;
    
    @JsonProperty("floor")
    private String floor;
    
    @JsonProperty("recurrence")
    private RecurrenceRequest recurrence;
    
    @JsonProperty("notes")
    private String notes;
    
    @JsonProperty("userId")
    private String userId;
    
    // Nested class for recurrence
    public static class RecurrenceRequest {
        @JsonProperty("type")
        private RecurrenceType type;
        
        @JsonProperty("endDate")
        private String endDate;
        
        @JsonProperty("customDates")
        private List<String> customDates;
        
        // Getters and Setters
        public RecurrenceType getType() {
            return type;
        }
        
        public void setType(RecurrenceType type) {
            this.type = type;
        }
        
        public String getEndDate() {
            return endDate;
        }
        
        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
        
        public List<String> getCustomDates() {
            return customDates;
        }
        
        public void setCustomDates(List<String> customDates) {
            this.customDates = customDates;
        }
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public BookType getBookType() {
        return bookType;
    }
    
    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }
    
    public String getSubType() {
        return subType;
    }
    
    public void setSubType(String subType) {
        this.subType = subType;
    }
    
    public String getOfficeLocation() {
        return officeLocation;
    }
    
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
    
    public String getBuilding() {
        return building;
    }
    
    public void setBuilding(String building) {
        this.building = building;
    }
    
    public String getFloor() {
        return floor;
    }
    
    public void setFloor(String floor) {
        this.floor = floor;
    }
    
    public RecurrenceRequest getRecurrence() {
        return recurrence;
    }
    
    public void setRecurrence(RecurrenceRequest recurrence) {
        this.recurrence = recurrence;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
} 