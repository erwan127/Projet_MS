#!/bin/bash

# ==============================================================================
# StatisticsService Testing Script
# ==============================================================================
# This script tests all StatisticsService endpoints through both methods:
# 1. API Gateway (Port 8080) - RECOMMENDED for microservices
# 2. Direct Access (Port 8084) - For debugging/development
# ==============================================================================

echo "🔍 STATISTICSSERVICE TESTING SUITE"
echo "====================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
API_GATEWAY_URL="http://localhost:8080"
DIRECT_SERVICE_URL="http://localhost:8084"
STATS_PATH="/api/statistics"

# Test function
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local base_url=$4
    
    echo -e "\n${BLUE}🧪 Testing: $description${NC}"
    echo "   Method: $method"
    echo "   URL: $base_url$endpoint"
    echo "   Response:"
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -X GET "$base_url$endpoint" -H "Accept: application/json" 2>/dev/null)
    elif [ "$method" = "POST" ]; then
        response=$(curl -s -X POST "$base_url$endpoint" -H "Accept: application/json" 2>/dev/null)
    fi
    
    if [ $? -eq 0 ] && [ ! -z "$response" ]; then
        echo -e "   ${GREEN}✅ Success${NC}"
        echo "   $response" | head -3
    else
        echo -e "   ${RED}❌ Failed${NC}"
    fi
}

# Health Check
echo -e "\n${YELLOW}🏥 HEALTH CHECKS${NC}"
echo "=================="

test_endpoint "GET" "$STATS_PATH/health" "Health Check (API Gateway)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/health" "Health Check (Direct)" "$DIRECT_SERVICE_URL"

# Global Statistics
echo -e "\n${YELLOW}🌐 GLOBAL STATISTICS${NC}"
echo "======================"

test_endpoint "GET" "$STATS_PATH/global" "Global Statistics (Default - Daily)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/global?granularity=HOURLY" "Global Statistics (Hourly)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/global?granularity=WEEKLY" "Global Statistics (Weekly)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/global?granularity=MONTHLY" "Global Statistics (Monthly)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/global?granularity=YEARLY" "Global Statistics (Yearly)" "$API_GATEWAY_URL"

# Dashboard
echo -e "\n${YELLOW}📊 DASHBOARD${NC}"
echo "=============="

test_endpoint "GET" "$STATS_PATH/dashboard" "Dashboard Summary" "$API_GATEWAY_URL"

# Entity Statistics (Examples with mock IDs)
echo -e "\n${YELLOW}🚗 VEHICLE STATISTICS${NC}"
echo "======================="

test_endpoint "GET" "$STATS_PATH/vehicle/VEH001" "Vehicle Statistics (VEH001)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/vehicle/VEH001?granularity=WEEKLY" "Vehicle Statistics Weekly (VEH001)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/entity/VEH001?entityType=VEHICLE&granularity=DAILY" "Generic Vehicle Entity (VEH001)" "$API_GATEWAY_URL"

echo -e "\n${YELLOW}🚉 STATION STATISTICS${NC}"
echo "======================="

test_endpoint "GET" "$STATS_PATH/station/STA001" "Station Statistics (STA001)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/station/STA001?granularity=MONTHLY" "Station Statistics Monthly (STA001)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/entity/STA001?entityType=STATION&granularity=DAILY" "Generic Station Entity (STA001)" "$API_GATEWAY_URL"

echo -e "\n${YELLOW}👤 USER STATISTICS${NC}"
echo "===================="

test_endpoint "GET" "$STATS_PATH/user/USR001" "User Statistics (USR001)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/user/USR001?granularity=YEARLY" "User Statistics Yearly (USR001)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/entity/USR001?entityType=USER&granularity=DAILY" "Generic User Entity (USR001)" "$API_GATEWAY_URL"

# Historical Statistics (Examples with date ranges)
echo -e "\n${YELLOW}📈 HISTORICAL STATISTICS${NC}"
echo "=========================="

# Current date for testing
current_date=$(date -u +%Y-%m-%dT%H:%M:%S)
start_date="2024-01-01T00:00:00"
end_date="2024-12-31T23:59:59"

test_endpoint "GET" "$STATS_PATH/history/TOTAL_VEHICLES?granularity=DAILY&startTime=$start_date&endTime=$end_date" "Total Vehicles History" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/history/VEHICLE_MILEAGE?granularity=WEEKLY&startTime=$start_date&endTime=$end_date" "Vehicle Mileage History" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/history/STATION_OCCUPANCY_RATE?granularity=HOURLY&startTime=2024-01-15T00:00:00&endTime=2024-01-15T23:59:59" "Station Occupancy History" "$API_GATEWAY_URL"

# Manual Collection
echo -e "\n${YELLOW}🔄 MANUAL OPERATIONS${NC}"
echo "====================="

test_endpoint "POST" "$STATS_PATH/collect" "Trigger Manual Statistics Collection" "$API_GATEWAY_URL"

# Summary
echo -e "\n${YELLOW}📋 DIRECT ACCESS COMPARISON${NC}"
echo "================================="
echo "Testing same endpoint via direct access vs API Gateway:"

test_endpoint "GET" "$STATS_PATH/global" "Global Stats (API Gateway)" "$API_GATEWAY_URL"
test_endpoint "GET" "$STATS_PATH/global" "Global Stats (Direct)" "$DIRECT_SERVICE_URL"

echo -e "\n${GREEN}🎉 TESTING COMPLETE!${NC}"
echo "=============================="
echo "📝 SUMMARY:"
echo "   • API Gateway URL: $API_GATEWAY_URL$STATS_PATH"
echo "   • Direct Service URL: $DIRECT_SERVICE_URL$STATS_PATH"
echo "   • For microservices architecture, use API Gateway (Port 8080)"
echo "   • For debugging/development, use direct access (Port 8084)"
echo ""
echo "💡 Available Time Granularities: HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY"
echo "💡 Available Statistic Types: TOTAL_VEHICLES, OPERATIONAL_VEHICLES, AVERAGE_VEHICLE_MILEAGE, etc."
echo ""
echo "🔗 Example URLs:"
echo "   Global Stats: curl -X GET \"$API_GATEWAY_URL$STATS_PATH/global\""
echo "   Vehicle Stats: curl -X GET \"$API_GATEWAY_URL$STATS_PATH/vehicle/VEH001\""
echo "   Manual Collection: curl -X POST \"$API_GATEWAY_URL$STATS_PATH/collect\"" 