package com.example.statisticsservice.controller;

import com.example.statisticsservice.dto.EntityStatisticsResponse;
import com.example.statisticsservice.dto.GlobalStatisticsResponse;
import com.example.statisticsservice.entities.StatisticEntry;
import com.example.statisticsservice.entities.StatisticType;
import com.example.statisticsservice.entities.TimeGranularity;
import com.example.statisticsservice.service.StatisticsCollectionService;
import com.example.statisticsservice.service.StatisticsQueryService;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:3000")
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsQueryService statisticsQueryService;

    @Autowired
    private StatisticsCollectionService statisticsCollectionService;

    /**
     * Get global statistics
     */
    @GetMapping("/global")
    @Timed(value = "statistics.global", description = "Time taken to get global statistics")
    public ResponseEntity<GlobalStatisticsResponse> getGlobalStatistics(
            @RequestParam(defaultValue = "DAILY") TimeGranularity granularity) {
        try {
            logger.info("Fetching global statistics with granularity: {}", granularity);
            GlobalStatisticsResponse response = statisticsQueryService.getGlobalStatistics(granularity);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching global statistics: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get entity-specific statistics
     */
    @GetMapping("/entity/{entityId}")
    @Timed(value = "statistics.entity", description = "Time taken to get entity statistics")
    public ResponseEntity<EntityStatisticsResponse> getEntityStatistics(
            @PathVariable String entityId,
            @RequestParam String entityType,
            @RequestParam(defaultValue = "DAILY") TimeGranularity granularity) {
        try {
            logger.info("Fetching statistics for entity: {} of type: {} with granularity: {}", 
                       entityId, entityType, granularity);
            EntityStatisticsResponse response = statisticsQueryService.getEntityStatistics(
                entityId, entityType, granularity);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching entity statistics: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get statistics history for a specific type
     */
    @GetMapping("/history/{type}")
    public ResponseEntity<List<StatisticEntry>> getStatisticsHistory(
            @PathVariable StatisticType type,
            @RequestParam(defaultValue = "DAILY") TimeGranularity granularity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            logger.info("Fetching statistics history for type: {} from {} to {}", 
                       type, startTime, endTime);
            List<StatisticEntry> history = statisticsQueryService.getStatisticsHistory(
                type, granularity, startTime, endTime);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error fetching statistics history: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get vehicle statistics
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<EntityStatisticsResponse> getVehicleStatistics(
            @PathVariable String vehicleId,
            @RequestParam(defaultValue = "DAILY") TimeGranularity granularity) {
        return getEntityStatistics(vehicleId, "VEHICLE", granularity);
    }

    /**
     * Get station statistics
     */
    @GetMapping("/station/{stationId}")
    public ResponseEntity<EntityStatisticsResponse> getStationStatistics(
            @PathVariable String stationId,
            @RequestParam(defaultValue = "DAILY") TimeGranularity granularity) {
        return getEntityStatistics(stationId, "STATION", granularity);
    }

    /**
     * Get user statistics
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<EntityStatisticsResponse> getUserStatistics(
            @PathVariable String userId,
            @RequestParam(defaultValue = "DAILY") TimeGranularity granularity) {
        return getEntityStatistics(userId, "USER", granularity);
    }

    /**
     * Trigger manual statistics collection
     */
    @PostMapping("/collect")
    public ResponseEntity<String> triggerStatisticsCollection() {
        try {
            logger.info("Manual statistics collection triggered");
            statisticsCollectionService.collectHourlyGlobalStatistics();
            return ResponseEntity.ok("Statistics collection triggered successfully");
        } catch (Exception e) {
            logger.error("Error triggering statistics collection: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error triggering statistics collection");
        }
    }

    /**
     * Get statistics dashboard summary
     */
    @GetMapping("/dashboard")
    public ResponseEntity<GlobalStatisticsResponse> getDashboardSummary() {
        try {
            logger.info("Fetching dashboard summary");
            GlobalStatisticsResponse response = statisticsQueryService.getGlobalStatistics(TimeGranularity.DAILY);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching dashboard summary: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Statistics Service is running");
    }
} 