package com.example.statisticsservice.service;

import com.example.statisticsservice.dto.EntityStatisticsResponse;
import com.example.statisticsservice.dto.GlobalStatisticsResponse;
import com.example.statisticsservice.entities.StatisticEntry;
import com.example.statisticsservice.entities.StatisticType;
import com.example.statisticsservice.entities.TimeGranularity;
import com.example.statisticsservice.repository.StatisticEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StatisticsQueryService {

    @Autowired
    private StatisticEntryRepository statisticEntryRepository;

    @Cacheable(value = "globalStatistics", key = "#granularity")
    public GlobalStatisticsResponse getGlobalStatistics(TimeGranularity granularity) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get latest statistics for each type
        int totalVehicles = getLatestStatisticValue(StatisticType.TOTAL_VEHICLES, granularity).intValue();
        int operationalVehicles = getLatestStatisticValue(StatisticType.OPERATIONAL_VEHICLES, granularity).intValue();
        double averageVehicleMileage = getLatestStatisticValue(StatisticType.AVERAGE_VEHICLE_MILEAGE, granularity);
        int totalStations = getLatestStatisticValue(StatisticType.TOTAL_STATIONS, granularity).intValue();
        double averageStationOccupancy = getLatestStatisticValue(StatisticType.AVERAGE_STATION_OCCUPANCY, granularity);
        double totalRentalHours = getLatestStatisticValue(StatisticType.TOTAL_RENTAL_HOURS, granularity);
        int activeUsers = getLatestStatisticValue(StatisticType.ACTIVE_USERS, granularity).intValue();

        return new GlobalStatisticsResponse(
            totalVehicles,
            operationalVehicles,
            averageVehicleMileage,
            totalStations,
            averageStationOccupancy,
            totalRentalHours,
            activeUsers,
            now
        );
    }

    private Double getLatestStatisticValue(StatisticType type, TimeGranularity granularity) {
        Optional<StatisticEntry> latest = statisticEntryRepository
            .findFirstByTypeAndGranularityOrderByTimestampDesc(type, granularity);
        return latest.map(StatisticEntry::getValue).orElse(0.0);
    }

    public EntityStatisticsResponse getEntityStatistics(String entityId, String entityType, TimeGranularity granularity) {
        Map<String, Object> statistics = new HashMap<>();
        LocalDateTime lastUpdated = LocalDateTime.now();

        switch (entityType.toUpperCase()) {
            case "VEHICLE":
                statistics = getVehicleStatistics(entityId, granularity);
                break;
            case "STATION":
                statistics = getStationStatistics(entityId, granularity);
                break;
            case "USER":
                statistics = getUserStatistics(entityId, granularity);
                break;
        }

        return new EntityStatisticsResponse(entityId, entityType, statistics, lastUpdated);
    }

    private Map<String, Object> getVehicleStatistics(String vehicleId, TimeGranularity granularity) {
        Map<String, Object> stats = new HashMap<>();
        
        Optional<StatisticEntry> mileage = statisticEntryRepository
            .findFirstByEntityIdAndTypeAndGranularityOrderByTimestampDesc(
                vehicleId, StatisticType.VEHICLE_MILEAGE, granularity);
        
        Optional<StatisticEntry> chargeLevel = statisticEntryRepository
            .findFirstByEntityIdAndTypeAndGranularityOrderByTimestampDesc(
                vehicleId, StatisticType.VEHICLE_CHARGE_LEVEL, granularity);

        mileage.ifPresent(entry -> stats.put("mileage", entry.getValue()));
        chargeLevel.ifPresent(entry -> stats.put("chargeLevel", entry.getValue()));
        
        return stats;
    }

    private Map<String, Object> getStationStatistics(String stationId, TimeGranularity granularity) {
        Map<String, Object> stats = new HashMap<>();
        
        Optional<StatisticEntry> occupancyRate = statisticEntryRepository
            .findFirstByEntityIdAndTypeAndGranularityOrderByTimestampDesc(
                stationId, StatisticType.STATION_OCCUPANCY_RATE, granularity);
        
        Optional<StatisticEntry> vehicleCount = statisticEntryRepository
            .findFirstByEntityIdAndTypeAndGranularityOrderByTimestampDesc(
                stationId, StatisticType.STATION_VEHICLE_COUNT, granularity);

        occupancyRate.ifPresent(entry -> stats.put("occupancyRate", entry.getValue()));
        vehicleCount.ifPresent(entry -> stats.put("vehicleCount", entry.getValue()));

        return stats;
    }

    private Map<String, Object> getUserStatistics(String userId, TimeGranularity granularity) {
        Map<String, Object> stats = new HashMap<>();
        
        Optional<StatisticEntry> totalKilometers = statisticEntryRepository
            .findFirstByEntityIdAndTypeAndGranularityOrderByTimestampDesc(
                userId, StatisticType.USER_TOTAL_KILOMETERS, granularity);

        totalKilometers.ifPresent(entry -> stats.put("totalKilometers", entry.getValue()));

        return stats;
    }

    public List<StatisticEntry> getStatisticsHistory(StatisticType type, TimeGranularity granularity, 
                                                   LocalDateTime startTime, LocalDateTime endTime) {
        return statisticEntryRepository.findByTypeAndGranularityAndTimestampBetweenOrderByTimestampDesc(
            type, granularity, startTime, endTime);
    }

    public List<StatisticEntry> getEntityStatisticsHistory(String entityId, StatisticType type, 
                                                          TimeGranularity granularity, 
                                                          LocalDateTime startTime, LocalDateTime endTime) {
        return statisticEntryRepository.findByEntityIdAndTypeAndGranularityAndTimestampBetweenOrderByTimestampDesc(
            entityId, type, granularity, startTime, endTime);
    }

    public Double getAverageStatistic(StatisticType type, TimeGranularity granularity, 
                                    LocalDateTime startTime, LocalDateTime endTime) {
        return statisticEntryRepository.getAverageValue(type, granularity, startTime, endTime);
    }

    public Double getSumStatistic(StatisticType type, TimeGranularity granularity, 
                                LocalDateTime startTime, LocalDateTime endTime) {
        return statisticEntryRepository.getSumValue(type, granularity, startTime, endTime);
    }

    public List<StatisticEntry> getAllGlobalStatistics(TimeGranularity granularity) {
        return statisticEntryRepository.findByEntityIdIsNullAndGranularityOrderByTimestampDesc(granularity);
    }

    public List<StatisticEntry> getAllEntityStatistics(TimeGranularity granularity) {
        return statisticEntryRepository.findByEntityIdIsNotNullAndGranularityOrderByTimestampDesc(granularity);
    }
} 