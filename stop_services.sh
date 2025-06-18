#!/bin/bash

# Stop Microservices Script
echo "=== Stopping All Microservices ==="

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

print_info() {
    echo -e "${GREEN}ℹ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="logs/$service_name.pid"
    
    if [ -f "$pid_file" ]; then
        local PID=$(cat $pid_file)
        if ps -p $PID > /dev/null; then
            print_info "Stopping $service_name (PID: $PID)..."
            kill $PID
            print_success "$service_name stopped"
        else
            print_info "$service_name was not running"
        fi
        rm -f $pid_file
    else
        print_info "No PID file found for $service_name"
    fi
}

# Stop all services
stop_service "eureka"
stop_service "Station Service"
stop_service "Vehicle Service" 
stop_service "User Service"
stop_service "Statistics Service"
stop_service "API Gateway"

# Also kill any remaining Maven processes
print_info "Killing any remaining Maven processes..."
pkill -f "spring-boot:run" 2>/dev/null || true

print_success "All services stopped!"
print_info "Database containers are still running. Use 'docker-compose -f docker-compose-db-only.yml down' to stop them." 