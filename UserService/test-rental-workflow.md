# Manual Test Script for Car Rental Workflow

This document provides comprehensive test scenarios to verify the car rental workflow implementation.

## Prerequisites

Ensure all three services are running:
- UserService (port 8081)
- Vehicule_Service (port 8082) 
- StationService (port 8083)

## Test Setup Data

### Create Test User
```http
POST http://localhost:8081/users/subscription
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john.doe@test.com",
  "phoneNumber": "123456789"
}
```

### Activate User Subscription
```http
PUT http://localhost:8081/users/{userId}/activate
```

### Create Test Station
```http
POST http://localhost:8083/stations
Content-Type: application/json

{
  "id": "station001",
  "position": {
    "x": 10,
    "y": 20
  },
  "capaciteGlobale": 5,
  "vehiculeIds": ["vehicle001", "vehicle002"]
}
```

### Create Test Vehicle
```http
POST http://localhost:8082/vehicles
Content-Type: application/json

{
  "id": "vehicle001",
  "marque": "Tesla",
  "modele": "Model 3",
  "nombrePlaces": 4,
  "etat": "OPERATIONNEL_EN_STATION",
  "niveauCharge": 100
}
```

## Test Scenarios

### ✅ Test 1: Complete Successful Rental Workflow

#### Step 1: Check User Authentication
```http
POST http://localhost:8081/users/authenticate
Content-Type: application/json

{
  "cardNumber": "{generated_card_number}",
  "pinCode": "{generated_pin_code}"
}
```
**Expected Result:** `{"authenticated": true, "message": "Authentification réussie"}`

#### Step 2: Check Vehicle Availability
```http
GET http://localhost:8081/users/card/{cardNumber}/can-rent/vehicle001
```
**Expected Result:** `{"canRent": true, "message": "Utilisateur peut louer ce véhicule"}`

#### Step 3: Check Vehicle is at Station
```http
GET http://localhost:8081/users/vehicles/vehicle001/station
```
**Expected Result:** Station details with vehicle001 in vehiculeIds

#### Step 4: Start Rental
```http
POST http://localhost:8081/users/card/{cardNumber}/start-rental
Content-Type: application/json

{
  "vehicleId": "vehicle001"
}
```
**Expected Result:** 
```json
{
  "success": true,
  "message": "Location démarrée avec succès depuis la station de recharge",
  "user": {
    "currentRentedVehicleId": "vehicle001"
  }
}
```

#### Step 5: Verify Vehicle Removed from Station
```http
GET http://localhost:8083/stations/station001
```
**Expected Result:** vehicle001 should NOT be in vehiculeIds list

#### Step 6: Verify Vehicle Status Changed
```http
GET http://localhost:8082/vehicles/vehicle001
```
**Expected Result:** Vehicle etat should be "LOUE"

#### Step 7: Check Available Return Stations
```http
GET http://localhost:8081/users/stations/with-space
```
**Expected Result:** List of stations with available space

#### Step 8: End Rental at Another Station
```http
POST http://localhost:8081/users/card/{cardNumber}/end-rental
Content-Type: application/json

{
  "stationId": "station002"
}
```
**Expected Result:**
```json
{
  "success": true,
  "message": "Location terminée avec succès à la station de recharge",
  "user": {
    "currentRentedVehicleId": null
  }
}
```

#### Step 9: Verify Vehicle Added to Return Station
```http
GET http://localhost:8083/stations/station002
```
**Expected Result:** vehicle001 should be in vehiculeIds list

### ❌ Test 2: Error Scenarios

#### Test 2.1: User Not Found
```http
POST http://localhost:8081/users/card/INVALID_CARD/start-rental
Content-Type: application/json

{
  "vehicleId": "vehicle001"
}
```
**Expected Result:** 
```json
{
  "success": false,
  "message": "Utilisateur non trouvé avec la carte: INVALID_CARD",
  "error": "VALIDATION_ERROR"
}
```

#### Test 2.2: Vehicle Not at Station
Move vehicle001 out of station first, then:
```http
POST http://localhost:8081/users/card/{cardNumber}/start-rental
Content-Type: application/json

{
  "vehicleId": "vehicle001"
}
```
**Expected Result:**
```json
{
  "success": false,
  "message": "Le véhicule doit être dans une station de recharge pour commencer la location",
  "error": "STATE_ERROR"
}
```

#### Test 2.3: User Already Has Active Rental
Start a rental first, then try to start another:
```http
POST http://localhost:8081/users/card/{cardNumber}/start-rental
Content-Type: application/json

{
  "vehicleId": "vehicle002"
}
```
**Expected Result:**
```json
{
  "success": false,
  "message": "Utilisateur a déjà un véhicule en location",
  "error": "STATE_ERROR"
}
```

#### Test 2.4: End Rental Without Station ID
```http
POST http://localhost:8081/users/card/{cardNumber}/end-rental
Content-Type: application/json

{}
```
**Expected Result:**
```json
{
  "success": false,
  "message": "L'ID de la station de recharge est requis pour terminer la location",
  "error": "MISSING_STATION_ID"
}
```

#### Test 2.5: Return to Full Station
Fill station to capacity first, then:
```http
POST http://localhost:8081/users/card/{cardNumber}/end-rental
Content-Type: application/json

{
  "stationId": "full_station_id"
}
```
**Expected Result:**
```json
{
  "success": false,
  "message": "La station de recharge n'a pas de place disponible",
  "error": "STATE_ERROR"
}
```

### 🔄 Test 3: Rollback Scenarios

#### Test 3.1: Simulate Vehicle Service Failure
Stop VehicleService temporarily, then:
```http
POST http://localhost:8081/users/card/{cardNumber}/start-rental
Content-Type: application/json

{
  "vehicleId": "vehicle001"
}
```
**Expected Result:** Error + vehicle should remain in original station

#### Test 3.2: Simulate Station Service Failure
Stop StationService temporarily during rental end:
```http
POST http://localhost:8081/users/card/{cardNumber}/end-rental
Content-Type: application/json

{
  "stationId": "station002"
}
```
**Expected Result:** Error + user should keep current rental

### 📊 Test 4: Additional Features

#### Test 4.1: Get Stations with Vehicles
```http
GET http://localhost:8081/users/stations/with-vehicles
```
**Expected Result:** List of stations containing vehicles

#### Test 4.2: Get User's Current Vehicle
```http
GET http://localhost:8081/users/card/{cardNumber}/current-vehicle
```
**Expected Result:** Current rented vehicle details or 404 if none

#### Test 4.3: Check Station Return Capacity
```http
GET http://localhost:8081/users/stations/{stationId}/can-return
```
**Expected Result:** 
```json
{
  "canReturn": true/false,
  "stationId": "station_id",
  "message": "Appropriate message"
}
```

## Test Verification Checklist

- [ ] ✅ User can start rental only from charging station
- [ ] ✅ Vehicle is removed from station when rental starts  
- [ ] ✅ User can end rental only at charging station with space
- [ ] ✅ Vehicle is added to station when rental ends
- [ ] ✅ Only one vehicle per user at a time
- [ ] ✅ Proper error messages for all failure scenarios
- [ ] ✅ Database consistency maintained across all operations
- [ ] ✅ Rollback mechanisms work correctly
- [ ] ✅ Service communication functions properly
- [ ] ✅ Authentication works before rentals

## Performance Tests

### Load Test Scenario
1. Create 10 users
2. Create 10 vehicles in stations
3. Simultaneously start 10 rentals
4. Verify no race conditions
5. End all rentals at different stations

### Concurrent Access Test
1. Two users try to rent same vehicle simultaneously
2. Only one should succeed
3. Station vehicle count should be consistent

## Database State Verification

After each test, verify database consistency:

### UserService Database
```sql
SELECT card_number, current_rented_vehicle_id, status FROM users;
```

### StationService Database  
```sql
SELECT station_id, vehicule_id FROM station_vehicules;
```

### VehicleService Database
```sql
SELECT id, etat FROM vehicules;
```

All data should be consistent across services after each operation. 