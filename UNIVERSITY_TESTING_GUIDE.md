# 🎓 University Testing Guide - Vehicle Rental Microservices

## For Examinator Review and Testing

### 📋 Project Overview
This is a microservices-based vehicle rental system built with Spring Boot featuring:
- Eureka Service Discovery
- API Gateway
- Vehicle Service (port 8081)
- Station Service (port 8082)  
- User Service (port 8083)
- Statistics Service (port 8084)
- MySQL Databases for data persistence
- Java 17 runtime environment

---

## 🚀 Quick Start Guide

### 1. Start All Services
```bash
# Start MySQL databases
docker-compose up -d

# Start all microservices
./start-services.sh

# Check service status
./check-status.sh
```

### 2. Verify Services Are Running
All services should show ✅ HEALTHY status:
- Eureka Server: http://localhost:8761
- API Gateway: http://localhost:8080  
- Vehicle Service: http://localhost:8081
- Station Service: http://localhost:8082
- User Service: http://localhost:8083
- Statistics Service: http://localhost:8084

---

## 🗄️ Database Population Commands

### Step 1: Create Users
```bash
# Create Alice
curl -X POST http://localhost:8083/users/subscription \
  -H "Content-Type: application/json" \
  -d '{
    "id": "user001",
    "firstName": "Alice",
    "lastName": "Johnson",
    "email": "alice.johnson@test.com",
    "phoneNumber": "0612345678"
  }'

curl -X PUT http://localhost:8083/users/user001/activate

# Create Bob
curl -X POST http://localhost:8083/users/subscription \
  -H "Content-Type: application/json" \
  -d '{
    "id": "user002",
    "firstName": "Bob", 
    "lastName": "Smith",
    "email": "bob.smith@test.com",
    "phoneNumber": "0987654321"
  }'

curl -X PUT http://localhost:8083/users/user002/activate
```

### Step 2: Create Stations
```bash
# Downtown Station
curl -X POST http://localhost:8082/stations \
  -H "Content-Type: application/json" \
  -d '{
    "id": "station001",
    "position": {"x": 10, "y": 20},
    "capaciteGlobale": 5
  }'

# Airport Station
curl -X POST http://localhost:8082/stations \
  -H "Content-Type: application/json" \
  -d '{
    "id": "station002",
    "position": {"x": 50, "y": 60},
    "capaciteGlobale": 3
  }'
```

### Step 3: Create Vehicles
```bash
# Tesla Model 3
curl -X POST http://localhost:8081/vehicules \
  -H "Content-Type: application/json" \
  -d '{
    "id": "vehicle001",
    "marque": "Tesla",
    "modele": "Model 3",
    "nombrePlaces": 4,
    "etat": "OPERATIONNEL_EN_STATION",
    "niveauCharge": 85
  }'

# BMW i3
curl -X POST http://localhost:8081/vehicules \
  -H "Content-Type: application/json" \
  -d '{
    "id": "vehicle002",
    "marque": "BMW",
    "modele": "i3",
    "nombrePlaces": 4,
    "etat": "OPERATIONNEL_EN_STATION",
    "niveauCharge": 92
  }'
```

### Step 4: Assign Vehicles to Stations
```bash
# Place vehicles at Downtown Station
curl -X POST http://localhost:8082/stations/station001/vehicles/vehicle001/add
curl -X POST http://localhost:8082/stations/station001/vehicles/vehicle002/add
```

---

## 🧪 API Testing Commands

### Vehicle Service Tests
```bash
# Get all vehicles
curl http://localhost:8080/api/vehicules

# Get specific vehicle
curl http://localhost:8080/api/vehicules/vehicle001
```

### Station Service Tests  
```bash
# Get all stations
curl http://localhost:8080/api/stations

# Get specific station
curl http://localhost:8080/api/stations/station001
```

### User Service Tests
```bash
# Get all users
curl http://localhost:8080/api/users

# Get specific user
curl http://localhost:8080/api/users/user001
```

### Statistics Service Tests
```bash
# Trigger manual statistics collection
curl -X POST http://localhost:8080/api/statistics/collect

# Get global statistics (hourly)
curl "http://localhost:8080/api/statistics/global?granularity=HOURLY"

# Get dashboard summary
curl http://localhost:8080/api/statistics/dashboard

# Check service health
curl http://localhost:8080/api/statistics/health
```

---

## 🚗 Vehicle Rental Workflow Testing

### Complete Rental Simulation
```bash
# 1. Check available vehicles at a station
curl http://localhost:8082/stations/station001

# 2. Start a rental from station
curl -X POST http://localhost:8082/stations/station001/vehicles/vehicle001/start-rental \
  -H "Content-Type: application/json" \
  -d '{"userId": "user001"}'

# 3. Check vehicle status during rental
curl http://localhost:8081/vehicules/vehicle001

# 4. End rental and return vehicle
curl -X POST http://localhost:8082/stations/station001/vehicles/vehicle001/end-rental \
  -H "Content-Type: application/json" \
  -d '{"userId": "user001", "kilometrage": 25.5}'

# 5. Verify vehicle is back at station
curl http://localhost:8082/stations/station001
```

---

## 🗄️ Database Direct Access Commands

### Check Database Contents
```bash
# Station Database
docker exec -it mysql_station_db mysql -u admin -padmin station_DB -e "SELECT * FROM stations;"

# Vehicle Database  
docker exec -it mysql_vehicule_db mysql -u admin -padmin vehicule_DB -e "SELECT * FROM vehicules;"

# User Database
docker exec -it mysql_user_db mysql -u admin -padmin user_DB -e "SELECT * FROM users;"

# Statistics Database
docker exec -it mysql_statistics_db mysql -u admin -padmin statistics_DB -e "SELECT * FROM statistic_entries LIMIT 10;"
```

---

## 📊 Postman Collection

Import the provided Postman collection: `New Collection.postman_collection.json`

Key Test Sequences:
1. Health Checks - Verify all services are running
2. Data Population - Create users, stations, vehicles
3. Basic CRUD Operations - Test create, read, update, delete
4. Rental Workflows - Complete rental simulation
5. Statistics Testing - Various statistics endpoints

---

## 🔧 Troubleshooting

### If Services Don't Start:
```bash
# Check Java version (should be 17)
java -version

# Stop all services and restart
./stop-services.sh
./start-services.sh

# Check logs
tail -f logs/station-service.log
```

### If APIs Return Empty Data:
```bash
# Check if databases are connected
./check-status.sh

# Manually populate using the commands above
./populate_test_data.sh
```

---

## 📝 Expected Results

After Population:
- 2+ Stations with different capacities
- 2+ Vehicles distributed across stations
- 2+ Active Users with valid credentials
- Statistics showing real-time system metrics

Working Features:
- ✅ Service Discovery via Eureka
- ✅ API Gateway routing
- ✅ CRUD operations for all entities
- ✅ Vehicle rental workflows
- ✅ Real-time statistics collection
- ✅ Database persistence
- ✅ Cross-service communication

---

## 🎯 Evaluation Points

This project demonstrates:
1. Microservices Architecture - Independent, loosely coupled services
2. Service Discovery - Eureka for service registration/discovery  
3. API Gateway - Single entry point with routing
4. Data Persistence - MySQL databases with JPA/Hibernate
5. Inter-Service Communication - REST APIs and Feign clients
6. Business Logic - Vehicle rental domain implementation
7. Monitoring - Statistics collection and aggregation
8. Error Handling - Graceful error responses
9. Configuration Management - Environment-specific configs
10. Testing - Comprehensive API testing suite

**Note:** This testing guide provides all necessary commands for a comprehensive evaluation of the microservices application. 