#!/bin/bash

echo "🚀 Quick Data Population for Statistics Testing"
echo "==============================================="

# Users
echo "👥 Creating Users..."
curl -X POST http://localhost:8083/users/subscription -H "Content-Type: application/json" -d '{"id": "user001","firstName": "Alice","lastName": "Demo","email": "alice.demo@test.com","phoneNumber": "0123456789"}' 
curl -X PUT http://localhost:8083/users/user001/activate

curl -X POST http://localhost:8083/users/subscription -H "Content-Type: application/json" -d '{"id": "user002","firstName": "Bob","lastName": "Smith","email": "bob.smith@test.com","phoneNumber": "0987654321"}'
curl -X PUT http://localhost:8083/users/user002/activate

curl -X POST http://localhost:8083/users/subscription -H "Content-Type: application/json" -d '{"id": "user003","firstName": "Charlie","lastName": "Brown","email": "charlie.brown@test.com","phoneNumber": "0555666777"}'
curl -X PUT http://localhost:8083/users/user003/activate

echo -e "\n🚗 Creating Stations..."
curl -X POST http://localhost:8082/stations -H "Content-Type: application/json" -d '{"id": "station001","position": {"x": 10, "y": 20},"capaciteGlobale": 5,"vehiculeIds": []}'

curl -X POST http://localhost:8082/stations -H "Content-Type: application/json" -d '{"id": "station002","position": {"x": 50, "y": 60},"capaciteGlobale": 3,"vehiculeIds": []}'

curl -X POST http://localhost:8082/stations -H "Content-Type: application/json" -d '{"id": "station003","position": {"x": 100, "y": 150},"capaciteGlobale": 8,"vehiculeIds": []}'

echo -e "\n🚙 Creating Vehicles..."
curl -X POST http://localhost:8081/vehicules -H "Content-Type: application/json" -d '{"id": "vehicle001","marque": "Tesla","modele": "Model 3","nombrePlaces": 4,"etat": "OPERATIONNEL_EN_STATION","niveauCharge": 85,"position": {"x": 10, "y": 20}}'

curl -X POST http://localhost:8081/vehicules -H "Content-Type: application/json" -d '{"id": "vehicle002","marque": "BMW","modele": "i3","nombrePlaces": 4,"etat": "OPERATIONNEL_EN_STATION","niveauCharge": 92,"position": {"x": 50, "y": 60}}'

curl -X POST http://localhost:8081/vehicules -H "Content-Type: application/json" -d '{"id": "vehicle003","marque": "Nissan","modele": "Leaf","nombrePlaces": 5,"etat": "OPERATIONNEL_EN_STATION","niveauCharge": 78,"position": {"x": 100, "y": 150}}'

curl -X POST http://localhost:8081/vehicules -H "Content-Type: application/json" -d '{"id": "vehicle004","marque": "Renault","modele": "Zoe","nombrePlaces": 4,"etat": "OPERATIONNEL_EN_STATION","niveauCharge": 95,"position": {"x": 200, "y": 180}}'

echo -e "\n🔗 Assigning Vehicles to Stations..."
curl -X POST http://localhost:8082/stations/station001/vehicles/vehicle001/add
curl -X POST http://localhost:8082/stations/station002/vehicles/vehicle002/add  
curl -X POST http://localhost:8082/stations/station003/vehicles/vehicle003/add

echo -e "\n✅ Data Population Complete!"
echo -e "\n📊 Test Statistics with:"
echo "curl http://localhost:8084/statistics/summary"
echo "Or via API Gateway:"
echo "curl http://localhost:8080/api/statistics/summary" 