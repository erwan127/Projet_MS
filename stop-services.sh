#!/bin/bash

echo "🛑 Stopping Microservices Application"
echo "======================================"

# Check if PID file exists
if [ ! -f .service_pids ]; then
    echo "⚠️  No service PID file found. Trying to find running services..."
    
    # Try to find and kill Spring Boot applications
    echo "🔍 Looking for Spring Boot applications..."
    pids=$(jps | grep -E "(Application|Eureka|Station|Vehicle|User|APIGateway)" | awk '{print $1}')
    
    if [ -z "$pids" ]; then
        echo "✅ No Spring Boot services found running"
        exit 0
    fi
    
    echo "Found PIDs: $pids"
    for pid in $pids; do
        echo "Killing process $pid..."
        kill $pid
    done
    
    sleep 5
    
    # Force kill if still running
    for pid in $pids; do
        if kill -0 $pid 2>/dev/null; then
            echo "Force killing process $pid..."
            kill -9 $pid
        fi
    done
    
else
    # Read PIDs from file and stop services
    echo "📋 Reading service PIDs from file..."
    source .service_pids
    
    services=("EUREKA_PID" "STATION_PID" "VEHICLE_PID" "USER_PID" "GATEWAY_PID")
    
    for service_var in "${services[@]}"; do
        pid=${!service_var}
        if [ -n "$pid" ]; then
            service_name=$(echo $service_var | sed 's/_PID//')
            if kill -0 $pid 2>/dev/null; then
                echo "🛑 Stopping $service_name (PID: $pid)..."
                kill $pid
            else
                echo "⚠️  $service_name (PID: $pid) was not running"
            fi
        fi
    done
    
    echo "⏳ Waiting for services to stop gracefully..."
    sleep 10
    
    # Force kill any remaining processes
    for service_var in "${services[@]}"; do
        pid=${!service_var}
        if [ -n "$pid" ] && kill -0 $pid 2>/dev/null; then
            service_name=$(echo $service_var | sed 's/_PID//')
            echo "🔨 Force stopping $service_name (PID: $pid)..."
            kill -9 $pid
        fi
    done
    
    # Remove PID file
    rm -f .service_pids
fi

echo ""
echo "🔍 Checking remaining Java processes..."
remaining=$(jps | grep -E "(Application|Eureka|Station|Vehicle|User|APIGateway)" || true)
if [ -n "$remaining" ]; then
    echo "⚠️  Some services may still be running:"
    echo "$remaining"
else
    echo "✅ All microservices stopped successfully"
fi

echo ""
echo "🗄️  MySQL containers are still running. To stop them run:"
echo "   docker-compose down"
echo ""
echo "📝 Service logs are preserved in the logs/ directory"
echo "✅ Shutdown complete!" 