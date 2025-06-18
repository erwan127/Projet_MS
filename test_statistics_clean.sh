#!/bin/bash

# ==============================================================================
# Clean StatisticsService Testing Script for Postman Demo
# ==============================================================================

echo "🔍 STATISTICSSERVICE CLEAN TESTING"
echo "==================================="

BASE_URL="http://localhost:8080"
STATS_PATH="/api/statistics"

echo -e "\n📊 TESTING STATISTICSSERVICE ENDPOINTS"
echo "======================================="

echo -e "\n1️⃣ HEALTH CHECK"
echo "URL: GET $BASE_URL$STATS_PATH/health"
echo "Purpose: Verify service is running"
echo "Response:"
curl -s "$BASE_URL$STATS_PATH/health" 2>/dev/null | tail -1
echo ""

echo -e "\n2️⃣ GLOBAL STATISTICS (Default - Daily)"
echo "URL: GET $BASE_URL$STATS_PATH/global"
echo "Purpose: Get overall system statistics"
echo "Response:"
curl -s "$BASE_URL$STATS_PATH/global" -H "Accept: application/json" 2>/dev/null | tail -1 | jq . 2>/dev/null || curl -s "$BASE_URL$STATS_PATH/global" 2>/dev/null | tail -1
echo ""

echo -e "\n3️⃣ GLOBAL STATISTICS (Weekly)"
echo "URL: GET $BASE_URL$STATS_PATH/global?granularity=WEEKLY"
echo "Purpose: Get weekly system statistics"
echo "Response:"
curl -s "$BASE_URL$STATS_PATH/global?granularity=WEEKLY" -H "Accept: application/json" 2>/dev/null | tail -1 | jq . 2>/dev/null || curl -s "$BASE_URL$STATS_PATH/global?granularity=WEEKLY" 2>/dev/null | tail -1
echo ""

echo -e "\n4️⃣ DASHBOARD SUMMARY"
echo "URL: GET $BASE_URL$STATS_PATH/dashboard"
echo "Purpose: Get dashboard overview"
echo "Response:"
curl -s "$BASE_URL$STATS_PATH/dashboard" -H "Accept: application/json" 2>/dev/null | tail -1 | jq . 2>/dev/null || curl -s "$BASE_URL$STATS_PATH/dashboard" 2>/dev/null | tail -1
echo ""

echo -e "\n5️⃣ VEHICLE STATISTICS"
echo "URL: GET $BASE_URL$STATS_PATH/vehicle/VEH001"
echo "Purpose: Get statistics for vehicle VEH001"
echo "Response:"
curl -s "$BASE_URL$STATS_PATH/vehicle/VEH001" -H "Accept: application/json" 2>/dev/null | tail -1 | jq . 2>/dev/null || curl -s "$BASE_URL$STATS_PATH/vehicle/VEH001" 2>/dev/null | tail -1
echo ""

echo -e "\n6️⃣ STATION STATISTICS"
echo "URL: GET $BASE_URL$STATS_PATH/station/STA001"
echo "Purpose: Get statistics for station STA001"
echo "Response:"
curl -s "$BASE_URL$STATS_PATH/station/STA001" -H "Accept: application/json" 2>/dev/null | tail -1 | jq . 2>/dev/null || curl -s "$BASE_URL$STATS_PATH/station/STA001" 2>/dev/null | tail -1
echo ""

echo -e "\n7️⃣ USER STATISTICS"
echo "URL: GET $BASE_URL$STATS_PATH/user/USR001"
echo "Purpose: Get statistics for user USR001"
echo "Response:"
curl -s "$BASE_URL$STATS_PATH/user/USR001" -H "Accept: application/json" 2>/dev/null | tail -1 | jq . 2>/dev/null || curl -s "$BASE_URL$STATS_PATH/user/USR001" 2>/dev/null | tail -1
echo ""

echo -e "\n8️⃣ HISTORICAL STATISTICS"
echo "URL: GET $BASE_URL$STATS_PATH/history/TOTAL_VEHICLES?granularity=DAILY&startTime=2024-01-01T00:00:00&endTime=2024-12-31T23:59:59"
echo "Purpose: Get historical data for total vehicles"
echo "Response:"
curl -s "$BASE_URL$STATS_PATH/history/TOTAL_VEHICLES?granularity=DAILY&startTime=2024-01-01T00:00:00&endTime=2024-12-31T23:59:59" -H "Accept: application/json" 2>/dev/null | tail -1 | jq . 2>/dev/null || curl -s "$BASE_URL$STATS_PATH/history/TOTAL_VEHICLES?granularity=DAILY&startTime=2024-01-01T00:00:00&endTime=2024-12-31T23:59:59" 2>/dev/null | tail -1
echo ""

echo -e "\n9️⃣ MANUAL STATISTICS COLLECTION"
echo "URL: POST $BASE_URL$STATS_PATH/collect"
echo "Purpose: Trigger manual statistics collection"
echo "Response:"
curl -s -X POST "$BASE_URL$STATS_PATH/collect" -H "Accept: application/json" 2>/dev/null | tail -1
echo ""

echo -e "\n✅ TESTING COMPLETE!"
echo "===================="
echo ""
echo "📝 POSTMAN SETUP SUMMARY:"
echo "========================="
echo "• Import the collection file: Postman_StatisticsService_Collection.json"
echo "• Set base_url variable: $BASE_URL"
echo "• Set stats_path variable: $STATS_PATH"
echo "• Test each endpoint by clicking 'Send'"
echo ""
echo "🔗 KEY URLS FOR POSTMAN:"
echo "========================"
echo "Health:      $BASE_URL$STATS_PATH/health"
echo "Global:      $BASE_URL$STATS_PATH/global"
echo "Dashboard:   $BASE_URL$STATS_PATH/dashboard"
echo "Vehicle:     $BASE_URL$STATS_PATH/vehicle/{vehicleId}"
echo "Station:     $BASE_URL$STATS_PATH/station/{stationId}"
echo "User:        $BASE_URL$STATS_PATH/user/{userId}"
echo "History:     $BASE_URL$STATS_PATH/history/{type}?granularity=DAILY&startTime=...&endTime=..."
echo "Collect:     $BASE_URL$STATS_PATH/collect (POST)" 