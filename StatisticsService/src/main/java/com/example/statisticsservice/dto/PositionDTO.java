package com.example.statisticsservice.dto;

public class PositionDTO {
    private double latitude;
    private double longitude;

    public PositionDTO() {}

    public PositionDTO(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
} 