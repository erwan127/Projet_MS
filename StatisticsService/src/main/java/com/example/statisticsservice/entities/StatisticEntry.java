package com.example.statisticsservice.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistic_entries")
public class StatisticEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatisticType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeGranularity granularity;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Double value;

    // Optional: for entity-specific statistics
    private String entityId;
    private String entityType; // VEHICLE, STATION, USER

    private String description;

    public StatisticEntry() {}

    public StatisticEntry(StatisticType type, TimeGranularity granularity, 
                         LocalDateTime timestamp, Double value) {
        this.type = type;
        this.granularity = granularity;
        this.timestamp = timestamp;
        this.value = value;
    }

    public StatisticEntry(StatisticType type, TimeGranularity granularity, 
                         LocalDateTime timestamp, Double value, 
                         String entityId, String entityType) {
        this(type, granularity, timestamp, value);
        this.entityId = entityId;
        this.entityType = entityType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatisticType getType() { return type; }
    public void setType(StatisticType type) { this.type = type; }

    public TimeGranularity getGranularity() { return granularity; }
    public void setGranularity(TimeGranularity granularity) { this.granularity = granularity; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Utility methods
    public boolean isGlobalStatistic() {
        return entityId == null || entityId.isEmpty();
    }

    public boolean isEntitySpecific() {
        return entityId != null && !entityId.isEmpty();
    }
} 