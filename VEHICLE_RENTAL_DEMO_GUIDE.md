# Vehicle Rental Demonstration Guide

## System Overview
Your microservice architecture includes:
- **UserService** (port 8083): Manages users and rental workflow
- **VehicleService** (port 8082): Manages vehicle states and availability
- **StationService** (port 8081): Manages charging stations and vehicle locations
- **API Gateway** (port 8080): Routes all requests
- **Frontend** (port 3000): React-based user interface
- **Eureka** (port 8761): Service discovery
- **Statistics Service** (port 8084): Tracks usage analytics

## Pre-Demonstration Setup

### 1. Start All Services
```bash
cd Projet_MS
docker-compose up -d
```

Wait for all services to be healthy (check logs):
```bash
docker-compose logs -f
```

### 2. Verify Services are Running
Check each service:
```bash
curl http://localhost:8761  # Eureka Dashboard
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/stations  # Station Service
curl http://localhost:8082/vehicles  # Vehicle Service  
curl http://localhost:8083/users  # User Service
curl http://localhost:8084/api/statistics/global  # Statistics Service
```

## Demonstration Scenario: Complete Vehicle Rental Workflow

### Step 1: Create Test Data

#### Create a Test User
```bash
curl -X POST http://localhost:8083/users/subscription \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Demo", 
    "email": "alice.demo@test.com",
    "phoneNumber": "0123456789"
  }'
```

**Expected Response:** User object with generated `id`, `cardNumber`, and `pinCode`
```json
{
  "id": "user123",
  "firstName": "Alice",
  "lastName": "Demo",
  "email": "alice.demo@test.com",
  "phoneNumber": "0123456789",
  "cardNumber": "CARD123456",
  "pinCode": "1234",
  "subscriptionStatus": "PENDING",
  "currentRentedVehicleId": null
}
```

#### Activate User Subscription
```bash
curl -X PUT http://localhost:8083/users/{userId}/activate
```

#### Create Test Stations
```bash
# Station 1 - Pickup station
curl -X POST http://localhost:8081/stations \
  -H "Content-Type: application/json" \
  -d '{
    "id": "station001",
    "position": {"x": 10, "y": 20},
    "capaciteGlobale": 5,
    "vehiculeIds": ["vehicle001", "vehicle002"]
  }'

# Station 2 - Return station  
curl -X POST http://localhost:8081/stations \
  -H "Content-Type: application/json" \
  -d '{
    "id": "station002", 
    "position": {"x": 50, "y": 60},
    "capaciteGlobale": 3,
    "vehiculeIds": []
  }'
```

#### Create Test Vehicles
```bash
# Vehicle 1 - Available for rental
curl -X POST http://localhost:8082/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "id": "vehicle001",
    "marque": "Tesla",
    "modele": "Model 3", 
    "nombrePlaces": 4,
    "etat": "OPERATIONNEL_EN_STATION",
    "niveauCharge": 85
  }'

# Vehicle 2 - Backup vehicle
curl -X POST http://localhost:8082/vehicles \
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

### Step 2: Demonstrate Vehicle Rental Process

#### 2.1 User Authentication
```bash
curl -X POST http://localhost:8083/users/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "CARD123456",
    "pinCode": "1234"
  }'
```

**Expected:** `{"authenticated": true, "message": "Authentification réussie"}`

#### 2.2 Check Available Stations with Vehicles
```bash
curl http://localhost:8083/users/stations/with-vehicles
```

**Show:** List of stations containing available vehicles

#### 2.3 Check if User Can Rent a Specific Vehicle
```bash
curl http://localhost:8083/users/card/CARD123456/can-rent/vehicle001
```

**Expected:** `{"canRent": true, "message": "Utilisateur peut louer ce véhicule"}`

#### 2.4 Verify Vehicle is at Station
```bash
curl http://localhost:8083/users/vehicles/vehicle001/station
```

**Show:** Station details with vehicle001 in the vehiculeIds list

#### 2.5 Start the Rental (KEY DEMONSTRATION POINT)
```bash
curl -X POST http://localhost:8083/users/card/CARD123456/start-rental \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": "vehicle001"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Location démarrée avec succès depuis la station de recharge",
  "user": {
    "id": "user123",
    "currentRentedVehicleId": "vehicle001",
    "subscriptionStatus": "ACTIVE"
  }
}
```

#### 2.6 Verify Rental State Changes

**Check vehicle was removed from station:**
```bash
curl http://localhost:8081/stations/station001
```
**Show:** vehicle001 should NOT be in vehiculeIds list anymore

**Check vehicle status changed:**
```bash
curl http://localhost:8082/vehicles/vehicle001
```
**Show:** Vehicle `etat` should now be "LOUE"

**Check user has active rental:**
```bash
curl http://localhost:8083/users/card/CARD123456/current-vehicle
```
**Show:** Current rented vehicle details

#### 2.7 Find Available Return Stations
```bash
curl http://localhost:8083/users/stations/with-space
```

**Show:** Stations with available parking space

#### 2.8 End the Rental (COMPLETION DEMONSTRATION)
```bash
curl -X POST http://localhost:8083/users/card/CARD123456/end-rental \
  -H "Content-Type: application/json" \
  -d '{
    "stationId": "station002"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Location terminée avec succès à la station de recharge",
  "user": {
    "id": "user123", 
    "currentRentedVehicleId": null,
    "subscriptionStatus": "ACTIVE"
  }
}
```

#### 2.9 Verify Return State Changes

**Check vehicle added to return station:**
```bash
curl http://localhost:8081/stations/station002
```
**Show:** vehicle001 should now be in station002's vehiculeIds list

**Check vehicle status changed back:**
```bash
curl http://localhost:8082/vehicles/vehicle001  
```
**Show:** Vehicle `etat` should now be "OPERATIONNEL_EN_STATION"

## Error Scenario Demonstrations

### 1. User Already Has Active Rental
Try to start another rental while one is active:
```bash
curl -X POST http://localhost:8083/users/card/CARD123456/start-rental \
  -H "Content-Type: application/json" \
  -d '{"vehicleId": "vehicle002"}'
```

**Expected:** Error message about existing active rental

### 2. Vehicle Not at Station
Remove vehicle from station and try to rent:
```bash
curl -X POST http://localhost:8081/stations/station001/vehicles/vehicle002/remove

curl -X POST http://localhost:8083/users/card/CARD123456/start-rental \
  -H "Content-Type: application/json" \
  -d '{"vehicleId": "vehicle002"}'
```

**Expected:** Error about vehicle not being at a charging station

### 3. Invalid User Authentication
Try with wrong credentials:
```bash
curl -X POST http://localhost:8083/users/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "INVALID",
    "pinCode": "0000"
  }'
```

**Expected:** `{"authenticated": false, "message": "Authentification échouée"}`

## Frontend Demonstration

### Access the Web Interface
1. Open browser to `http://localhost:3000`
2. Navigate through the UI to show:
   - Available stations with vehicles
   - Vehicle details and status
   - User rental interface
   - Real-time updates after rental actions

## Statistics and Monitoring

### View System Statistics
```bash
curl http://localhost:8084/api/statistics/dashboard
```

**Show:** 
- Total vehicles and their statuses
- Station occupancy rates  
- Active rentals
- User activity metrics

### Vehicle-Specific Statistics
```bash
curl http://localhost:8084/api/statistics/vehicle/vehicle001
```

### Station-Specific Statistics
```bash
curl http://localhost:8084/api/statistics/station/station001
```

## Key Points for Demonstration

1. **Microservice Architecture**: Each service handles its own domain (Users, Vehicles, Stations)
2. **Service Communication**: Services communicate via REST APIs through the API Gateway
3. **State Management**: Rental process updates multiple services atomically
4. **Data Consistency**: Vehicle location and status remain consistent across services
5. **Error Handling**: Comprehensive validation and error responses
6. **Real-time Updates**: Changes reflect immediately across all services
7. **Scalability**: Each service can be scaled independently
8. **Monitoring**: Statistics service provides insights into system usage

## Troubleshooting Commands

If services aren't responding:
```bash
# Check service status
docker-compose ps

# View service logs
docker-compose logs vehicle-service
docker-compose logs user-service
docker-compose logs station-service

# Restart specific service
docker-compose restart vehicle-service

# Full system restart
docker-compose down && docker-compose up -d
```

## Cleanup After Demo
```bash
# Stop all services
docker-compose down

# Remove volumes (optional - clears all data)
docker-compose down -v
```

This comprehensive guide covers the complete vehicle rental workflow from station pickup to station return, demonstrating the full microservice architecture and its capabilities. 