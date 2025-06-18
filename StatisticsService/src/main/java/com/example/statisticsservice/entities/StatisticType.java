package com.example.statisticsservice.entities;

public enum StatisticType {
    // Global statistics
    TOTAL_VEHICLES,
    OPERATIONAL_VEHICLES,
    AVERAGE_VEHICLE_MILEAGE,
    TOTAL_STATIONS,
    AVERAGE_STATION_OCCUPANCY,
    TOTAL_RENTAL_HOURS,
    ACTIVE_USERS,
    
    // Vehicle-specific statistics
    VEHICLE_MILEAGE,
    VEHICLE_MILEAGE_SINCE_MAINTENANCE,
    VEHICLE_USAGE_HOURS,
    VEHICLE_CHARGE_LEVEL,
    
    // Station-specific statistics
    STATION_OCCUPANCY_RATE,
    STATION_VEHICLE_COUNT,
    STATION_AVAILABLE_SPACES,
    
    // User-specific statistics
    USER_TOTAL_KILOMETERS,
    USER_RENTAL_HOURS,
    USER_RENTAL_COUNT
} 