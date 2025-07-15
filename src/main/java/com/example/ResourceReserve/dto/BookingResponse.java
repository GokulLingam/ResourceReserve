package com.example.ResourceReserve.dto;

import com.example.ResourceReserve.entity.Booking;
import com.example.ResourceReserve.entity.Booking.BookType;
import com.example.ResourceReserve.entity.Booking.RecurrenceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @JsonProperty("startTime")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @JsonProperty("endTime")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    
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
    
    @JsonProperty("recurrenceType")
    private RecurrenceType recurrenceType;
    
    @JsonProperty("endDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @JsonProperty("customDates")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private List<LocalDate> customDates;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("notes")
    private String notes;
    
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    public static BookingResponse fromEntity(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .date(booking.getDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .bookType(booking.getBookType())
                .subType(booking.getSubType())
                .officeLocation(booking.getOfficeLocation())
                .building(booking.getBuilding())
                .floor(booking.getFloor())
                .recurrenceType(booking.getRecurrenceType())
                .endDate(booking.getEndDate())
                .customDates(booking.getCustomDates())
                .status(booking.getStatus())
                .userId(booking.getUserId())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
} 