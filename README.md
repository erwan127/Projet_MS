
##  **1. Add Data:**
```bash
# Run the population script
./quick_populate_database.sh

```

##  **2. Start all services:**
```bash
# Run the start script
./start_services.sh

```
##  **3. Check status:**
```bash
# Run the check script. Please, wait about 30s to 1min to make sure all services are started !
./check-status.sh

```

## 🎯 **4. Test with our Postman Collection:**
Import `New Collection.postman_collection.json` for comprehensive testing


##  **5. Once you're done, stop all services:**
```bash
# Run the check script. Please, wait about 30s to 1min to make sure all services are started !
./stop_services.sh

```

## 🧪 **Quick API Tests:**

```bash
# Check all data
curl http://localhost:8080/api/users
curl http://localhost:8080/api/stations  
curl http://localhost:8080/api/vehicules

# Statistics
curl http://localhost:8080/api/statistics/health
curl "http://localhost:8080/api/statistics/global?granularity=HOURLY"

# Vehicle Rental Test
curl -X POST http://localhost:8082/stations/station001/vehicles/vehicle001/start-rental \
  -H "Content-Type: application/json" \
  -d '{"userId": "user001"}'
```

---

## 📝 **Key Features Demonstrated:**

1. **Microservices Architecture** - 5 independent services
2. **Service Discovery** - Eureka registration
3. **API Gateway** - Single entry point with routing
4. **Database Persistence** - MySQL with JPA/Hibernate
5. **Business Logic** - Vehicle rental workflows
6. **Cross-service Communication** - REST APIs and Feign clients
7. **Version 3** - Nous avons réalisé le projet jusqu'à la version 3 incluse

## Merci !
## Erwan FAVRE et Eva BARDEAU
## M2 MIAGE - S10 2024-2025 - Microservices sur Cloud
---

