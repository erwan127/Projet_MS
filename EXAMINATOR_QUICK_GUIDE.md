# 🎓 Quick Guide for University Examinator

## 🚀 **READY TO TEST - All Systems Operational**

### **Current Status:**
✅ **Java 17** properly configured  
✅ **All microservices** running and healthy  
✅ **Databases** populated with test data  
✅ **API Gateway** routing correctly  
✅ **Statistics Service** collecting data  

---

## 📊 **Available Test Data:**

### **Users (4 total):**
- **Alice Johnson** - Card: CARD05793015, PIN: 1252
- **Bob Smith** - Card: CARD29096530, PIN: 4154  
- **Charlie Brown** - Card: CARD75700323, PIN: 5971
- **Diana Prince** - Card: CARD86200916, PIN: 9673

### **Stations (2 total):**
- **station001** - Downtown (capacity: 5, has Tesla)
- **station002** - Airport (capacity: 3, has BMW)

### **Vehicles (2 total):**
- **vehicle001** - Tesla Model 3 (85% charge) at Downtown Station
- **vehicle002** - BMW i3 (92% charge) at Airport Station

---

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

## 🔧 **If You Need Fresh Data:**

```bash
# Run the population script
./quick_populate_database.sh

# Or manually repopulate
# (See UNIVERSITY_TESTING_GUIDE.md for detailed commands)
```

---

## 📝 **Key Features Demonstrated:**

1. **Microservices Architecture** - 5 independent services
2. **Service Discovery** - Eureka registration
3. **API Gateway** - Single entry point with routing
4. **Database Persistence** - MySQL with JPA/Hibernate
5. **Business Logic** - Vehicle rental workflows
6. **Real-time Statistics** - Data aggregation and monitoring
7. **Cross-service Communication** - REST APIs and Feign clients

---

## 🎯 **Postman Collection:**
Import `New Collection.postman_collection.json` for comprehensive testing

**Ready for evaluation! 🚀** 