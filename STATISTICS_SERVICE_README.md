g# Statistics Service - Version 3

## Overview
The Statistics Service is a comprehensive microservice that provides statistical data and analytics for the entire system. It collects, stores, and serves statistics for vehicles, stations, and users with historical data tracking across different time granularities.

## Features

### Global Statistics
- **Total vehicles** in the system
- **Operational vehicles** count
- **Average vehicle mileage** across all vehicles
- **Total stations** in the network
- **Average station occupancy rate**
- **Total rental hours** system-wide
- **Active users** count

### Entity-Specific Statistics

#### Vehicle Statistics
- Individual vehicle mileage
- Vehicle mileage since last maintenance
- Vehicle usage hours
- Vehicle charge level
- Vehicle operational status

#### Station Statistics
- Station occupancy rate
- Number of vehicles currently at station
- Available parking spaces
- Station utilization patterns

#### User Statistics
- Total kilometers traveled by user
- Total rental hours per user
- Number of rentals per user
- User activity patterns

### Time Granularity
Statistics are collected and stored at multiple time intervals:
- **Hourly**: Real-time statistics collection
- **Daily**: End-of-day aggregated statistics
- **Weekly**: Weekly summaries (collected on Sundays)
- **Monthly**: Monthly reports (collected on 1st of each month)
- **Yearly**: Annual statistics for long-term trends

## API Endpoints

### Global Statistics
```
GET /api/statistics/global?granularity=DAILY
```

### Entity-Specific Statistics
```
GET /api/statistics/entity/{entityId}?entityType=VEHICLE&granularity=DAILY
GET /api/statistics/vehicle/{vehicleId}?granularity=DAILY
GET /api/statistics/station/{stationId}?granularity=DAILY  
GET /api/statistics/user/{userId}?granularity=DAILY
```

### Historical Data
```
GET /api/statistics/history/{type}?granularity=DAILY&startTime=2024-01-01T00:00:00&endTime=2024-01-31T23:59:59
```

### Dashboard
```
GET /api/statistics/dashboard
```

### Manual Collection
```
POST /api/statistics/collect
```

## Configuration
The service runs on port 8084 and connects to MySQL database on port 3309.

## Deployment
Use Docker Compose to deploy the complete system including the Statistics Service.