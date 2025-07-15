package com.example.ResourceReserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorPlanResponse {
    
    private List<Seat> seats;
    private List<DeskArea> deskAreas;
    private OfficeLayout officeLayout;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Seat {
        private String id;
        private double x;
        private double y;
        private String status;
        private String type;
        private List<String> equipment;
        private int rotation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeskArea {
        private String id;
        private String name;
        private double x;
        private double y;
        private double width;
        private double height;
        private String type;
        private int rotation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfficeLayout {
        private double x;
        private double y;
        private double width;
        private double height;
        private String fillColor;
        private double fillOpacity;
        private String strokeColor;
        private int strokeWidth;
    }
} 