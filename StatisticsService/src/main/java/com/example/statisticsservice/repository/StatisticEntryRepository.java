package com.example.statisticsservice.repository;

import com.example.statisticsservice.entities.StatisticEntry;
import com.example.statisticsservice.entities.StatisticType;
import com.example.statisticsservice.entities.TimeGranularity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticEntryRepository extends JpaRepository<StatisticEntry, Long> {
    
    // Find statistics by type and granularity
    List<StatisticEntry> findByTypeAndGranularityOrderByTimestampDesc(
        StatisticType type, TimeGranularity granularity);
    
    // Find statistics for a specific entity
    List<StatisticEntry> findByEntityIdAndTypeAndGranularityOrderByTimestampDesc(
        String entityId, StatisticType type, TimeGranularity granularity);
    
    // Find statistics within a time range
    List<StatisticEntry> findByTypeAndGranularityAndTimestampBetweenOrderByTimestampDesc(
        StatisticType type, TimeGranularity granularity, 
        LocalDateTime startTime, LocalDateTime endTime);
    
    // Find entity-specific statistics within a time range
    List<StatisticEntry> findByEntityIdAndTypeAndGranularityAndTimestampBetweenOrderByTimestampDesc(
        String entityId, StatisticType type, TimeGranularity granularity,
        LocalDateTime startTime, LocalDateTime endTime);
    
    // Find latest statistic entry for a type and granularity
    Optional<StatisticEntry> findFirstByTypeAndGranularityOrderByTimestampDesc(
        StatisticType type, TimeGranularity granularity);
    
    // Find latest statistic entry for a specific entity
    Optional<StatisticEntry> findFirstByEntityIdAndTypeAndGranularityOrderByTimestampDesc(
        String entityId, StatisticType type, TimeGranularity granularity);
    
    // Get all global statistics (where entityId is null)
    List<StatisticEntry> findByEntityIdIsNullAndGranularityOrderByTimestampDesc(
        TimeGranularity granularity);
    
    // Get all entity-specific statistics
    List<StatisticEntry> findByEntityIdIsNotNullAndGranularityOrderByTimestampDesc(
        TimeGranularity granularity);
    
    // Custom query to get average values for a statistic type
    @Query("SELECT AVG(s.value) FROM StatisticEntry s WHERE s.type = :type AND s.granularity = :granularity AND s.timestamp BETWEEN :startTime AND :endTime")
    Double getAverageValue(@Param("type") StatisticType type, 
                          @Param("granularity") TimeGranularity granularity,
                          @Param("startTime") LocalDateTime startTime, 
                          @Param("endTime") LocalDateTime endTime);
    
    // Custom query to get sum of values for a statistic type
    @Query("SELECT SUM(s.value) FROM StatisticEntry s WHERE s.type = :type AND s.granularity = :granularity AND s.timestamp BETWEEN :startTime AND :endTime")
    Double getSumValue(@Param("type") StatisticType type, 
                      @Param("granularity") TimeGranularity granularity,
                      @Param("startTime") LocalDateTime startTime, 
                      @Param("endTime") LocalDateTime endTime);
    
    // Delete old statistics older than specified date
    void deleteByTimestampBefore(LocalDateTime timestamp);
} 