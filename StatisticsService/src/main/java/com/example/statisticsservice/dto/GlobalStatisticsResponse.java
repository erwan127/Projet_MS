package com.example.statisticsservice.dto;

import java.time.LocalDateTime;

public class GlobalStatisticsResponse {
    private int totalVehicles;
    private int operationalVehicles;
    private double averageVehicleMileage;
    private int totalStations;
    private double averageStationOccupancy;
    private double totalRentalHours;
    private int activeUsers;
    private LocalDateTime lastUpdated;

    public GlobalStatisticsResponse() {}

    public GlobalStatisticsResponse(int totalVehicles, int operationalVehicles, 
                                  double averageVehicleMileage, int totalStations, 
                                  double averageStationOccupancy, double totalRentalHours, 
                                  int activeUsers, LocalDateTime lastUpdated) {
        this.totalVehicles = totalVehicles;
        this.operationalVehicles = operationalVehicles;
        this.averageVehicleMileage = averageVehicleMileage;
        this.totalStations = totalStations;
        this.averageStationOccupancy = averageStationOccupancy;
        this.totalRentalHours = totalRentalHours;
        this.activeUsers = activeUsers;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(int totalVehicles) { this.totalVehicles = totalVehicles; }

    public int getOperationalVehicles() { return operationalVehicles; }
    public void setOperationalVehicles(int operationalVehicles) { this.operationalVehicles = operationalVehicles; }

    public double getAverageVehicleMileage() { return averageVehicleMileage; }
    public void setAverageVehicleMileage(double averageVehicleMileage) { this.averageVehicleMileage = averageVehicleMileage; }

    public int getTotalStations() { return totalStations; }
    public void setTotalStations(int totalStations) { this.totalStations = totalStations; }

    public double getAverageStationOccupancy() { return averageStationOccupancy; }
    public void setAverageStationOccupancy(double averageStationOccupancy) { this.averageStationOccupancy = averageStationOccupancy; }

    public double getTotalRentalHours() { return totalRentalHours; }
    public void setTotalRentalHours(double totalRentalHours) { this.totalRentalHours = totalRentalHours; }

    public int getActiveUsers() { return activeUsers; }
    public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
} 