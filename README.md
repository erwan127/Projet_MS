##  **Ajouts depuis la présentation :**

1. **Version 2** - En plus de commencer et finir à une station, la location se fait maintenant en vérifiant l'abonnement de l'utilisateur. 
2. **Version 3** - Seules les statistiques globales fonctionnaient lors de la présentation, nous avons terminé de corriger les statistiques globales (par heure et par jour), statistiques pour véhicules et stations, statistiques pour le "dashboard" et le nombre de véhicules total


##  **1. Start the databases:**
```bash
# Run this command inside Projet_MS
docker-compose -f docker-compose-db-only.yml up -d

```
##  **2. Add Data:**
```bash
# Run the population script
./quick_populate_database.sh

```

##  **3. Start all services:**
```bash
# Run the start script
./start_services.sh

```
##  **4. Check status:**
```bash
# Run the check script. Please, wait about 30s to 1min to make sure all services are started !
./check-status.sh

```

##  **5. Test with our Postman Collection:**
Import `Requests_Postman.json` in Postman


##  **6. Once you're done, stop all services:**
```bash
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

---

## Merci !
## Erwan FAVRE et Eva BARDEAU
## M2 MIAGE - S10 2024-2025 - Microservices sur Cloud
---

