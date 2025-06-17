package com.example.statisticsservice.service;

import com.example.statisticsservice.client.StationServiceClient;
import com.example.statisticsservice.client.UserServiceClient;
import com.example.statisticsservice.client.VehicleServiceClient;
import com.example.statisticsservice.dto.StationDTO;
import com.example.statisticsservice.dto.UserDTO;
import com.example.statisticsservice.dto.VehicleDTO;
import com.example.statisticsservice.entities.StatisticEntry;
import com.example.statisticsservice.entities.StatisticType;
import com.example.statisticsservice.entities.TimeGranularity;
import com.example.statisticsservice.repository.StatisticEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StatisticsCollectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(StatisticsCollectionService.class);

    @Autowired
    private VehicleServiceClient vehicleServiceClient;

    @Autowired
    private StationServiceClient stationServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private StatisticEntryRepository statisticEntryRepository;

    // Collect global statistics every hour
    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 ms
    public void collectHourlyGlobalStatistics() {
        logger.info("Starting hourly global statistics collection");
        collectGlobalStatistics(TimeGranularity.HOURLY);
    }

    // Collect global statistics every day
    @Scheduled(cron = "0 0 0 * * ?") // Daily at midnight
    public void collectDailyGlobalStatistics() {
        logger.info("Starting daily global statistics collection");
        collectGlobalStatistics(TimeGranularity.DAILY);
    }

    // Collect global statistics every week
    @Scheduled(cron = "0 0 0 ? * SUN") // Weekly on Sunday at midnight
    public void collectWeeklyGlobalStatistics() {
        logger.info("Starting weekly global statistics collection");
        collectGlobalStatistics(TimeGranularity.WEEKLY);
    }

    // Collect global statistics every month
    @Scheduled(cron = "0 0 0 1 * ?") // Monthly on the 1st at midnight
    public void collectMonthlyGlobalStatistics() {
        logger.info("Starting monthly global statistics collection");
        collectGlobalStatistics(TimeGranularity.MONTHLY);
    }

    private void collectGlobalStatistics(TimeGranularity granularity) {
        try {
            LocalDateTime timestamp = LocalDateTime.now();

            // Vehicle statistics
            collectVehicleStatistics(granularity, timestamp);

            // Station statistics
            collectStationStatistics(granularity, timestamp);

            // User statistics
            collectUserStatistics(granularity, timestamp);

            logger.info("Completed {} global statistics collection", granularity);
        } catch (Exception e) {
            logger.error("Error collecting {} global statistics: {}", granularity, e.getMessage(), e);
        }
    }

    private void collectVehicleStatistics(TimeGranularity granularity, LocalDateTime timestamp) {
        try {
            List<VehicleDTO> allVehicles = vehicleServiceClient.getAllVehicles();
            List<VehicleDTO> operationalVehicles = vehicleServiceClient.getOperationalVehicles();

            // Total vehicles
            saveStatistic(StatisticType.TOTAL_VEHICLES, granularity, timestamp, 
                         (double) allVehicles.size());

            // Operational vehicles
            saveStatistic(StatisticType.OPERATIONAL_VEHICLES, granularity, timestamp, 
                         (double) operationalVehicles.size());

            // Average vehicle mileage
            double averageMileage = allVehicles.stream()
                    .mapToDouble(VehicleDTO::getKilometrage)
                    .average()
                    .orElse(0.0);
            saveStatistic(StatisticType.AVERAGE_VEHICLE_MILEAGE, granularity, timestamp, averageMileage);

            // Individual vehicle statistics
            for (VehicleDTO vehicle : allVehicles) {
                saveEntityStatistic(StatisticType.VEHICLE_MILEAGE, granularity, timestamp,
                                  (double) vehicle.getKilometrage(), vehicle.getId(), "VEHICLE");
                
                saveEntityStatistic(StatisticType.VEHICLE_CHARGE_LEVEL, granularity, timestamp,
                                  (double) vehicle.getNiveauCharge(), vehicle.getId(), "VEHICLE");
            }

            logger.debug("Collected vehicle statistics: {} total, {} operational", 
                        allVehicles.size(), operationalVehicles.size());
        } catch (Exception e) {
            logger.error("Error collecting vehicle statistics: {}", e.getMessage(), e);
        }
    }

    private void collectStationStatistics(TimeGranularity granularity, LocalDateTime timestamp) {
        try {
            List<StationDTO> allStations = stationServiceClient.getAllStations();

            // Total stations
            saveStatistic(StatisticType.TOTAL_STATIONS, granularity, timestamp, 
                         (double) allStations.size());

            // Average station occupancy
            double averageOccupancy = allStations.stream()
                    .mapToDouble(StationDTO::getTauxOccupation)
                    .average()
                    .orElse(0.0);
            saveStatistic(StatisticType.AVERAGE_STATION_OCCUPANCY, granularity, timestamp, averageOccupancy);

            // Individual station statistics
            for (StationDTO station : allStations) {
                saveEntityStatistic(StatisticType.STATION_OCCUPANCY_RATE, granularity, timestamp,
                                  station.getTauxOccupation(), station.getId(), "STATION");
                
                saveEntityStatistic(StatisticType.STATION_VEHICLE_COUNT, granularity, timestamp,
                                  (double) station.getNombreVehiculesGares(), station.getId(), "STATION");
                
                saveEntityStatistic(StatisticType.STATION_AVAILABLE_SPACES, granularity, timestamp,
                                  (double) station.getNombrePlacesLibres(), station.getId(), "STATION");
            }

            logger.debug("Collected station statistics: {} total stations", allStations.size());
        } catch (Exception e) {
            logger.error("Error collecting station statistics: {}", e.getMessage(), e);
        }
    }

    private void collectUserStatistics(TimeGranularity granularity, LocalDateTime timestamp) {
        try {
            List<UserDTO> activeUsers = userServiceClient.getActiveUsers();
            List<UserDTO> usersWithRentals = userServiceClient.getUsersWithActiveRentals();

            // Active users
            saveStatistic(StatisticType.ACTIVE_USERS, granularity, timestamp, 
                         (double) activeUsers.size());

            // Calculate total rental hours (simplified - would need rental history in real system)
            double totalRentalHours = usersWithRentals.size() * 1.0; // Placeholder calculation
            saveStatistic(StatisticType.TOTAL_RENTAL_HOURS, granularity, timestamp, totalRentalHours);

            logger.debug("Collected user statistics: {} active users, {} with rentals", 
                        activeUsers.size(), usersWithRentals.size());
        } catch (Exception e) {
            logger.error("Error collecting user statistics: {}", e.getMessage(), e);
        }
    }

    private void saveStatistic(StatisticType type, TimeGranularity granularity, 
                              LocalDateTime timestamp, Double value) {
        StatisticEntry entry = new StatisticEntry(type, granularity, timestamp, value);
        statisticEntryRepository.save(entry);
    }

    private void saveEntityStatistic(StatisticType type, TimeGranularity granularity, 
                                   LocalDateTime timestamp, Double value, 
                                   String entityId, String entityType) {
        StatisticEntry entry = new StatisticEntry(type, granularity, timestamp, value, entityId, entityType);
        statisticEntryRepository.save(entry);
    }

    // Cleanup old statistics - run monthly
    @Scheduled(cron = "0 0 0 1 * ?")
    public void cleanupOldStatistics() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusYears(2);
            statisticEntryRepository.deleteByTimestampBefore(cutoffDate);
            logger.info("Cleaned up statistics older than {}", cutoffDate);
        } catch (Exception e) {
            logger.error("Error cleaning up old statistics: {}", e.getMessage(), e);
        }
    }
} 