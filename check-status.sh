#!/bin/bash

echo "🔍 Checking Microservices Status"
echo "=================================="

# Function to check service health
check_service() {
    local name=$1
    local port=$2
    local health_path=${3:-"/actuator/health"}
    local url="http://localhost:$port$health_path"
    
    if curl -s "$url" > /dev/null 2>&1; then
        echo "✅ $name (port $port) - HEALTHY"
        return 0
    else
        echo "❌ $name (port $port) - NOT RESPONDING"
        return 1
    fi
}

# Check MySQL containers
echo "🗄️  Database Status:"
docker ps --filter "name=mysql" --format "table {{.Names}}\t{{.Status}}" | grep -v NAMES || echo "   ❌ No MySQL containers running"

echo ""
echo "🚀 Microservice Status:"

# Check each service with CORRECT ports
check_service "Eureka Server" 8761 "/eureka/apps"
check_service "API Gateway" 8080
check_service "Vehicle Service" 8081  
check_service "Station Service" 8082
check_service "User Service" 8083
check_service "Statistics Service" 8084

echo ""
echo "📊 Java Processes:"
jps | grep -E "(Application|Eureka|Station|Vehicle|User|APIGateway)" || echo "   ❌ No microservices running"

echo ""
echo "🌐 Service URLs:"
echo "   📋 Eureka Dashboard: http://localhost:8761"
echo "   🌉 API Gateway: http://localhost:8080"
echo "   🚙 Vehicle Service: http://localhost:8081/vehicules" 
echo "   🚗 Station Service: http://localhost:8082/stations"
echo "   👤 User Service: http://localhost:8083/users"
echo "   📊 Statistics Service: http://localhost:8084/statistics"

echo ""
if [ -f ".service_pids" ]; then
    echo "📝 Service PIDs file exists - services managed by start-services.sh"
else
    echo "⚠️  No PID file found - services may have been started manually"
fi

echo ""
echo "💡 Commands:"
echo "   Start all: ./start-services.sh"
echo "   Stop all:  ./stop-services.sh"
echo "   This check: ./check-status.sh" 