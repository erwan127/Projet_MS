package com.example.statisticsservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class EntityStatisticsResponse {
    private String entityId;
    private String entityType;
    private Map<String, Object> statistics;
    private LocalDateTime lastUpdated;

    public EntityStatisticsResponse() {}

    public EntityStatisticsResponse(String entityId, String entityType, 
                                  Map<String, Object> statistics, LocalDateTime lastUpdated) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.statistics = statistics;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Map<String, Object> getStatistics() { return statistics; }
    public void setStatistics(Map<String, Object> statistics) { this.statistics = statistics; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
} 