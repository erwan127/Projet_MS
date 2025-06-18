#!/bin/bash

# Start Microservices Script
echo "=== Starting All Microservices ==="

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to start a service in background
start_service() {
    local service_dir=$1
    local service_name=$2
    local port=$3
    
    print_info "Starting $service_name on port $port..."
    cd $service_dir
    
    # Build and run in background - fix the logging paths with proper quoting
    local log_file="../logs/${service_name// /_}.log"  # Replace spaces with underscores
    local pid_file="../logs/${service_name// /_}.pid"  # Replace spaces with underscores
    nohup mvn spring-boot:run > "$log_file" 2>&1 &
    SERVICE_PID=$!
    echo $SERVICE_PID > "$pid_file"
    
    print_success "$service_name started (PID: $SERVICE_PID)"
    cd ..
}

# Create logs directory if it doesn't exist
mkdir -p logs

# Start Eureka Service Discovery first
print_info "Starting Eureka Service Discovery..."
cd Eureka
nohup mvn spring-boot:run > ../logs/eureka.log 2>&1 &
EUREKA_PID=$!
echo $EUREKA_PID > ../logs/eureka.pid
print_success "Eureka started (PID: $EUREKA_PID)"
cd ..

# Wait for Eureka to start
print_info "Waiting for Eureka to initialize..."
sleep 20

# Start other services
start_service "Vehicule_Service" "Vehicle Service" "8081"
sleep 5

start_service "StationService" "Station Service" "8082"
sleep 5

start_service "UserService" "User Service" "8083"
sleep 5

start_service "StatisticsService" "Statistics Service" "8084"
sleep 5

start_service "APIGateway" "API Gateway" "8080"

print_success "All services started!"
print_info "Check logs in the 'logs' directory"
print_info "To stop services, run: ./stop_services.sh"

echo ""
echo "Service URLs:"
echo "- Eureka Dashboard: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- Vehicle Service: http://localhost:8081"
echo "- Station Service: http://localhost:8082"
echo "- User Service: http://localhost:8083"
echo "- Statistics Service: http://localhost:8084" 