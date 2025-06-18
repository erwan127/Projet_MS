#!/bin/bash

# Vehicle Rental Demo Test Script
# This script automates the demonstration of vehicle rental from station

echo "=== Vehicle Rental Demo Script ==="
echo "Starting vehicle rental demonstration..."

# Base URLs
USER_SERVICE="http://localhost:8083"
VEHICLE_SERVICE="http://localhost:8082"
STATION_SERVICE="http://localhost:8081"
STATISTICS_SERVICE="http://localhost:8084"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_step() {
    echo -e "${BLUE}=== $1 ===${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Function to wait for user input
wait_for_input() {
    if [ "$1" = "auto" ]; then
        sleep 2
    else
        read -p "Press Enter to continue..."
    fi
}

# Check if services are running
check_services() {
    print_step "Checking Services Status"
    
    services=("8081:Station Service" "8082:Vehicle Service" "8083:User Service" "8084:Statistics Service")
    
    for service in "${services[@]}"; do
        port=$(echo $service | cut -d: -f1)
        name=$(echo $service | cut -d: -f2)
        
        if curl -s -o /dev/null -w "%{http_code}" http://localhost:$port | grep -q "200\|404"; then
            print_success "$name is running on port $port"
        else
            print_error "$name is not responding on port $port"
            echo "Please ensure all services are running with: docker-compose up -d"
            exit 1
        fi
    done
}

# Step 1: Create test data
create_test_data() {
    print_step "Creating Test Data"
    
    # Create user
    print_info "Creating test user Alice Demo..."
    USER_RESPONSE=$(curl -s -X POST $USER_SERVICE/users/subscription \
        -H "Content-Type: application/json" \
        -d '{
            "firstName": "Alice",
            "lastName": "Demo", 
            "email": "alice.demo@test.com",
            "phoneNumber": "0123456789"
        }')
    
    if echo "$USER_RESPONSE" | grep -q "id"; then
        USER_ID=$(echo "$USER_RESPONSE" | jq -r '.id')
        CARD_NUMBER=$(echo "$USER_RESPONSE" | jq -r '.cardNumber')
        PIN_CODE=$(echo "$USER_RESPONSE" | jq -r '.pinCode')
        print_success "User created: ID=$USER_ID, Card=$CARD_NUMBER, PIN=$PIN_CODE"
    else
        print_error "Failed to create user"
        echo "$USER_RESPONSE"
        exit 1
    fi
    
    # Activate user
    print_info "Activating user subscription..."
    curl -s -X PUT $USER_SERVICE/users/$USER_ID/activate
    print_success "User activated"
    
    # Create stations
    print_info "Creating test stations..."
    curl -s -X POST $STATION_SERVICE/stations \
        -H "Content-Type: application/json" \
        -d '{
            "id": "station001",
            "position": {"x": 10, "y": 20},
            "capaciteGlobale": 5,
            "vehiculeIds": ["vehicle001", "vehicle002"]
        }' > /dev/null
    
    curl -s -X POST $STATION_SERVICE/stations \
        -H "Content-Type: application/json" \
        -d '{
            "id": "station002", 
            "position": {"x": 50, "y": 60},
            "capaciteGlobale": 3,
            "vehiculeIds": []
        }' > /dev/null
    print_success "Stations created: station001 (pickup), station002 (return)"
    
    # Create vehicles
    print_info "Creating test vehicles..."
    curl -s -X POST $VEHICLE_SERVICE/vehicles \
        -H "Content-Type: application/json" \
        -d '{
            "id": "vehicle001",
            "marque": "Tesla",
            "modele": "Model 3", 
            "nombrePlaces": 4,
            "etat": "OPERATIONNEL_EN_STATION",
            "niveauCharge": 85
        }' > /dev/null
    
    curl -s -X POST $VEHICLE_SERVICE/vehicles \
        -H "Content-Type: application/json" \
        -d '{
            "id": "vehicle002",
            "marque": "BMW",
            "modele": "i3",
            "nombrePlaces": 4, 
            "etat": "OPERATIONNEL_EN_STATION",
            "niveauCharge": 92
        }' > /dev/null
    print_success "Vehicles created: vehicle001 (Tesla Model 3), vehicle002 (BMW i3)"
}

# Step 2: Demonstrate rental workflow
demonstrate_rental() {
    print_step "Demonstrating Vehicle Rental Workflow"
    
    # Authentication
    print_info "Testing user authentication..."
    AUTH_RESPONSE=$(curl -s -X POST $USER_SERVICE/users/authenticate \
        -H "Content-Type: application/json" \
        -d "{
            \"cardNumber\": \"$CARD_NUMBER\",
            \"pinCode\": \"$PIN_CODE\"
        }")
    
    if echo "$AUTH_RESPONSE" | grep -q '"authenticated":true'; then
        print_success "User authenticated successfully"
    else
        print_error "Authentication failed"
        echo "$AUTH_RESPONSE"
        exit 1
    fi
    
    wait_for_input $1
    
    # Check vehicle availability
    print_info "Checking if user can rent vehicle001..."
    CAN_RENT=$(curl -s $USER_SERVICE/users/card/$CARD_NUMBER/can-rent/vehicle001)
    if echo "$CAN_RENT" | grep -q '"canRent":true'; then
        print_success "User can rent vehicle001"
    else
        print_error "User cannot rent vehicle001"
        echo "$CAN_RENT"
    fi
    
    wait_for_input $1
    
    # Check vehicle location
    print_info "Verifying vehicle001 is at a station..."
    VEHICLE_STATION=$(curl -s $USER_SERVICE/users/vehicles/vehicle001/station)
    if echo "$VEHICLE_STATION" | grep -q "station001"; then
        print_success "Vehicle001 is at station001"
    else
        print_error "Vehicle001 location issue"
        echo "$VEHICLE_STATION"
    fi
    
    wait_for_input $1
    
    # Start rental
    print_step "STARTING RENTAL (Key Demo Point)"
    print_info "Starting rental of vehicle001..."
    RENTAL_START=$(curl -s -X POST $USER_SERVICE/users/card/$CARD_NUMBER/start-rental \
        -H "Content-Type: application/json" \
        -d '{"vehicleId": "vehicle001"}')
    
    if echo "$RENTAL_START" | grep -q '"success":true'; then
        print_success "Rental started successfully!"
        echo "$RENTAL_START" | jq .
    else
        print_error "Rental start failed"
        echo "$RENTAL_START"
        exit 1
    fi
    
    wait_for_input $1
    
    # Verify state changes
    print_step "Verifying State Changes After Rental Start"
    
    print_info "Checking vehicle was removed from station001..."
    STATION_CHECK=$(curl -s $STATION_SERVICE/stations/station001)
    if echo "$STATION_CHECK" | grep -q "vehicle001"; then
        print_error "Vehicle001 still at station001 - state error!"
    else
        print_success "Vehicle001 removed from station001"
    fi
    
    print_info "Checking vehicle status changed to LOUE..."
    VEHICLE_CHECK=$(curl -s $VEHICLE_SERVICE/vehicles/vehicle001)
    if echo "$VEHICLE_CHECK" | grep -q "LOUE"; then
        print_success "Vehicle001 status is now LOUE"
    else
        print_error "Vehicle001 status did not change properly"
        echo "$VEHICLE_CHECK"
    fi
    
    wait_for_input $1
    
    # Find return stations
    print_info "Finding available return stations..."
    RETURN_STATIONS=$(curl -s $USER_SERVICE/users/stations/with-space)
    echo "$RETURN_STATIONS" | jq .
    
    wait_for_input $1
    
    # End rental
    print_step "ENDING RENTAL (Completion Demo Point)"
    print_info "Ending rental at station002..."
    RENTAL_END=$(curl -s -X POST $USER_SERVICE/users/card/$CARD_NUMBER/end-rental \
        -H "Content-Type: application/json" \
        -d '{"stationId": "station002"}')
    
    if echo "$RENTAL_END" | grep -q '"success":true'; then
        print_success "Rental ended successfully!"
        echo "$RENTAL_END" | jq .
    else
        print_error "Rental end failed"
        echo "$RENTAL_END"
        exit 1
    fi
    
    wait_for_input $1
    
    # Verify return state changes
    print_step "Verifying State Changes After Rental End"
    
    print_info "Checking vehicle added to station002..."
    RETURN_STATION_CHECK=$(curl -s $STATION_SERVICE/stations/station002)
    if echo "$RETURN_STATION_CHECK" | grep -q "vehicle001"; then
        print_success "Vehicle001 added to station002"
    else
        print_error "Vehicle001 not found at station002"
        echo "$RETURN_STATION_CHECK"
    fi
    
    print_info "Checking vehicle status changed back to OPERATIONNEL_EN_STATION..."
    VEHICLE_FINAL_CHECK=$(curl -s $VEHICLE_SERVICE/vehicles/vehicle001)
    if echo "$VEHICLE_FINAL_CHECK" | grep -q "OPERATIONNEL_EN_STATION"; then
        print_success "Vehicle001 status is now OPERATIONNEL_EN_STATION"
    else
        print_error "Vehicle001 status did not change back properly"
        echo "$VEHICLE_FINAL_CHECK"
    fi
}

# Step 3: Demonstrate error scenarios
demonstrate_errors() {
    print_step "Demonstrating Error Scenarios"
    
    # Try to rent while already having a rental
    print_info "Testing error: User already has active rental..."
    # First start a rental
    curl -s -X POST $USER_SERVICE/users/card/$CARD_NUMBER/start-rental \
        -H "Content-Type: application/json" \
        -d '{"vehicleId": "vehicle002"}' > /dev/null
    
    # Then try to start another
    ERROR_RESPONSE=$(curl -s -X POST $USER_SERVICE/users/card/$CARD_NUMBER/start-rental \
        -H "Content-Type: application/json" \
        -d '{"vehicleId": "vehicle001"}')
    
    if echo "$ERROR_RESPONSE" | grep -q '"success":false'; then
        print_success "Error handling works - detected existing rental"
        echo "$ERROR_RESPONSE" | jq .
    else
        print_error "Error handling failed - should not allow multiple rentals"
    fi
    
    # End the rental for cleanup
    curl -s -X POST $USER_SERVICE/users/card/$CARD_NUMBER/end-rental \
        -H "Content-Type: application/json" \
        -d '{"stationId": "station001"}' > /dev/null
    
    wait_for_input $1
    
    # Test invalid authentication
    print_info "Testing error: Invalid authentication..."
    INVALID_AUTH=$(curl -s -X POST $USER_SERVICE/users/authenticate \
        -H "Content-Type: application/json" \
        -d '{
            "cardNumber": "INVALID",
            "pinCode": "0000"
        }')
    
    if echo "$INVALID_AUTH" | grep -q '"authenticated":false'; then
        print_success "Authentication error handling works"
        echo "$INVALID_AUTH" | jq .
    else
        print_error "Authentication should have failed"
    fi
}

# Step 4: Show statistics
show_statistics() {
    print_step "Demonstrating Statistics and Monitoring"
    
    print_info "Global system statistics..."
    curl -s $STATISTICS_SERVICE/api/statistics/global | jq .
    
    wait_for_input $1
    
    print_info "Vehicle001 statistics..."
    curl -s $STATISTICS_SERVICE/api/statistics/vehicle/vehicle001 | jq .
    
    wait_for_input $1
    
    print_info "Station001 statistics..."
    curl -s $STATISTICS_SERVICE/api/statistics/station/station001 | jq .
}

# Main execution
main() {
    # Check if jq is available
    if ! command -v jq &> /dev/null; then
        print_error "jq is required for this demo. Please install it: brew install jq (macOS) or apt-get install jq (Ubuntu)"
        exit 1
    fi
    
    # Check if running in auto mode
    AUTO_MODE=""
    if [ "$1" = "--auto" ]; then
        AUTO_MODE="auto"
        print_info "Running in automatic mode (no user interaction)"
    else
        print_info "Running in interactive mode (press Enter to continue between steps)"
    fi
    
    # Execute demo steps
    check_services
    wait_for_input $AUTO_MODE
    
    create_test_data
    wait_for_input $AUTO_MODE
    
    demonstrate_rental $AUTO_MODE
    wait_for_input $AUTO_MODE
    
    demonstrate_errors $AUTO_MODE
    wait_for_input $AUTO_MODE
    
    show_statistics $AUTO_MODE
    
    print_step "DEMONSTRATION COMPLETE"
    print_success "Vehicle rental workflow demonstrated successfully!"
    print_info "Frontend available at: http://localhost:3000"
    print_info "Eureka dashboard at: http://localhost:8761"
    print_info "Statistics dashboard at: http://localhost:8084/api/statistics/dashboard"
}

# Run the demo
main "$@" 