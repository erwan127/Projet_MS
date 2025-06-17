#!/bin/bash

echo "🚀 Starting Microservices Application"
echo "======================================"

# Function to check if a port is available
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "⚠️  Port $port is already in use"
        return 1
    else
        echo "✅ Port $port is available"
        return 0
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=0
    
    echo "⏳ Waiting for $service_name to be ready..."
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s $url > /dev/null 2>&1; then
            echo "✅ $service_name is ready!"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo "   Attempt $attempt/$max_attempts - waiting..."
        sleep 2
    done
    
    echo "❌ $service_name failed to start within expected time"
    return 1
}

# Check if MySQL containers are running
echo "🗄️  Checking MySQL containers..."
if ! docker ps | grep -q mysql_vehicule_db; then
    echo "❌ MySQL containers not running. Starting them..."
    docker-compose up -d
    sleep 10
fi

echo "📋 Checking port availability..."
check_port 8761 || { echo "❌ Eureka port busy. Please stop the service running on port 8761"; exit 1; }
check_port 8080 || { echo "❌ StationService port busy. Please stop the service running on port 8080"; exit 1; }
check_port 8081 || { echo "❌ VehicleService port busy. Please stop the service running on port 8081"; exit 1; }
check_port 8082 || { echo "❌ APIGateway port busy. Please stop the service running on port 8082"; exit 1; }
check_port 8083 || { echo "❌ UserService port busy. Please stop the service running on port 8083"; exit 1; }

echo ""
echo "🔧 Building all services..."

# Build all services
services=("Eureka" "StationService" "Vehicule_Service" "UserService" "APIGateway")

for service in "${services[@]}"; do
    echo "Building $service..."
    cd $service
    if ! ./mvnw clean install -DskipTests -q; then
        echo "❌ Failed to build $service"
        exit 1
    fi
    cd ..
    echo "✅ $service built successfully"
done

echo ""
echo "🚀 Starting services in order..."

# Start Eureka Server
echo "Starting Eureka Server..."
cd Eureka
nohup ./mvnw spring-boot:run > ../logs/eureka.log 2>&1 &
EUREKA_PID=$!
cd ..
echo "Eureka PID: $EUREKA_PID"

# Wait for Eureka to be ready
wait_for_service "http://localhost:8761/eureka/apps" "Eureka Server"

# Start Station Service
echo "Starting Station Service..."
cd StationService
nohup ./mvnw spring-boot:run > ../logs/station.log 2>&1 &
STATION_PID=$!
cd ..
echo "Station Service PID: $STATION_PID"

# Start Vehicle Service
echo "Starting Vehicle Service..."
cd Vehicule_Service
nohup ./mvnw spring-boot:run > ../logs/vehicle.log 2>&1 &
VEHICLE_PID=$!
cd ..
echo "Vehicle Service PID: $VEHICLE_PID"

# Start User Service
echo "Starting User Service..."
cd UserService
nohup ./mvnw spring-boot:run > ../logs/user.log 2>&1 &
USER_PID=$!
cd ..
echo "User Service PID: $USER_PID"

# Wait a bit for services to register with Eureka
echo "⏳ Waiting for services to register with Eureka..."
sleep 10

# Start API Gateway
echo "Starting API Gateway..."
cd APIGateway
nohup ./mvnw spring-boot:run > ../logs/gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..
echo "API Gateway PID: $GATEWAY_PID"

# Create logs directory if it doesn't exist
mkdir -p logs

# Save PIDs to a file for later cleanup
echo "EUREKA_PID=$EUREKA_PID" > .service_pids
echo "STATION_PID=$STATION_PID" >> .service_pids
echo "VEHICLE_PID=$VEHICLE_PID" >> .service_pids
echo "USER_PID=$USER_PID" >> .service_pids
echo "GATEWAY_PID=$GATEWAY_PID" >> .service_pids

echo ""
echo "🎉 All services started!"
echo "======================================"
echo "📊 Service URLs:"
echo "   Eureka Server: http://localhost:8761"
echo "   Station Service: http://localhost:8080"
echo "   Vehicle Service: http://localhost:8081"
echo "   User Service: http://localhost:8083"
echo "   API Gateway: http://localhost:8082"
echo ""
echo "📝 Logs are available in the logs/ directory"
echo "🛑 To stop all services, run: ./stop-services.sh"
echo ""
echo "⏳ Waiting for all services to be fully ready..."
sleep 15

echo "🔍 Checking service status..."
jps | grep -E "(Application|Service)" || echo "Services may still be starting..."

echo "✅ Startup complete! Check the logs if any service is not responding." 