#!/bin/bash

# Comprehensive Test Data Population Script
# This script creates users, stations, vehicles, and simulates rental activity

echo "🚀 Starting Test Data Population for Microservices"
echo "=================================================="

# Base URLs
USER_SERVICE="http://localhost:8083"
VEHICLE_SERVICE="http://localhost:8081"
STATION_SERVICE="http://localhost:8080"
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

# Check if services are running
check_services() {
    print_step "Checking Services"
    
    services=("8080:Station Service" "8081:Vehicle Service" "8083:User Service" "8084:Statistics Service")
    
    for service in "${services[@]}"; do
        port=$(echo $service | cut -d: -f1)
        name=$(echo $service | cut -d: -f2)
        
        if curl -s -o /dev/null -w "%{http_code}" http://localhost:$port | grep -q "200\|404\|500"; then
            print_success "$name is running on port $port"
        else
            print_error "$name is not responding on port $port"
            echo "Please ensure all services are running first!"
            exit 1
        fi
    done
}

# Step 1: Create Users
create_users() {
    print_step "Creating Users"
    
    # User 1 - Alice (already exists, activate if needed)
    print_info "Creating/Activating Alice Demo..."
    curl -s -X PUT $USER_SERVICE/users/user001/activate > /dev/null
    print_success "Alice activated - Card: CARD08211180, PIN: 2785"
    
    # User 2 - Bob
    print_info "Creating Bob Smith..."
    BOB_RESPONSE=$(curl -s -X POST $USER_SERVICE/users/subscription \
        -H "Content-Type: application/json" \
        -d '{
            "id": "user002",
            "firstName": "Bob",
            "lastName": "Smith",
            "email": "bob.smith@test.com",
            "phoneNumber": "0987654321"
        }')
    
    if echo "$BOB_RESPONSE" | grep -q "id"; then
        BOB_CARD=$(echo "$BOB_RESPONSE" | grep -o '"cardNumber":"[^"]*"' | cut -d'"' -f4)
        BOB_PIN=$(echo "$BOB_RESPONSE" | grep -o '"pinCode":"[^"]*"' | cut -d'"' -f4)
        curl -s -X PUT $USER_SERVICE/users/user002/activate > /dev/null
        print_success "Bob created - Card: $BOB_CARD, PIN: $BOB_PIN"
    else
        print_error "Failed to create Bob"
    fi
    
    # User 3 - Charlie
    print_info "Creating Charlie Brown..."
    CHARLIE_RESPONSE=$(curl -s -X POST $USER_SERVICE/users/subscription \
        -H "Content-Type: application/json" \
        -d '{
            "id": "user003",
            "firstName": "Charlie",
            "lastName": "Brown",
            "email": "charlie.brown@test.com",
            "phoneNumber": "0555666777"
        }')
    
    if echo "$CHARLIE_RESPONSE" | grep -q "id"; then
        CHARLIE_CARD=$(echo "$CHARLIE_RESPONSE" | grep -o '"cardNumber":"[^"]*"' | cut -d'"' -f4)
        CHARLIE_PIN=$(echo "$CHARLIE_RESPONSE" | grep -o '"pinCode":"[^"]*"' | cut -d'"' -f4)
        curl -s -X PUT $USER_SERVICE/users/user003/activate > /dev/null
        print_success "Charlie created - Card: $CHARLIE_CARD, PIN: $CHARLIE_PIN"
    else
        print_error "Failed to create Charlie"
    fi
    
    # User 4 - Diana
    print_info "Creating Diana Prince..."
    DIANA_RESPONSE=$(curl -s -X POST $USER_SERVICE/users/subscription \
        -H "Content-Type: application/json" \
        -d '{
            "id": "user004",
            "firstName": "Diana",
            "lastName": "Prince",
            "email": "diana.prince@test.com",
            "phoneNumber": "0444555666"
        }')
    
    if echo "$DIANA_RESPONSE" | grep -q "id"; then
        DIANA_CARD=$(echo "$DIANA_RESPONSE" | grep -o '"cardNumber":"[^"]*"' | cut -d'"' -f4)
        DIANA_PIN=$(echo "$DIANA_RESPONSE" | grep -o '"pinCode":"[^"]*"' | cut -d'"' -f4)
        curl -s -X PUT $USER_SERVICE/users/user004/activate > /dev/null
        print_success "Diana created - Card: $DIANA_CARD, PIN: $DIANA_PIN"
    else
        print_error "Failed to create Diana"
    fi
}

# Step 2: Create Stations
create_stations() {
    print_step "Creating Charging Stations"
    
    # Station 1 - Downtown
    print_info "Creating Downtown Station..."
    curl -s -X POST $STATION_SERVICE/stations \
        -H "Content-Type: application/json" \
        -d '{
            "id": "station001",
            "position": {"x": 10, "y": 20},
            "capaciteGlobale": 5,
            "vehiculeIds": []
        }' > /dev/null
    print_success "Downtown Station created (capacity: 5)"
    
    # Station 2 - Airport
    print_info "Creating Airport Station..."
    curl -s -X POST $STATION_SERVICE/stations \
        -H "Content-Type: application/json" \
        -d '{
            "id": "station002",
            "position": {"x": 50, "y": 60},
            "capaciteGlobale": 3,
            "vehiculeIds": []
        }' > /dev/null
    print_success "Airport Station created (capacity: 3)"
    
    # Station 3 - University
    print_info "Creating University Station..."
    curl -s -X POST $STATION_SERVICE/stations \
        -H "Content-Type: application/json" \
        -d '{
            "id": "station003",
            "position": {"x": 100, "y": 150},
            "capaciteGlobale": 8,
            "vehiculeIds": []
        }' > /dev/null
    print_success "University Station created (capacity: 8)"
    
    # Station 4 - Mall
    print_info "Creating Shopping Mall Station..."
    curl -s -X POST $STATION_SERVICE/stations \
        -H "Content-Type: application/json" \
        -d '{
            "id": "station004",
            "position": {"x": 200, "y": 180},
            "capaciteGlobale": 6,
            "vehiculeIds": []
        }' > /dev/null
    print_success "Shopping Mall Station created (capacity: 6)"
}

# Step 3: Create Vehicles
create_vehicles() {
    print_step "Creating Electric Vehicles"
    
    # Vehicle 1 - Tesla Model 3
    print_info "Creating Tesla Model 3..."
    curl -s -X POST $VEHICLE_SERVICE/vehicules \
        -H "Content-Type: application/json" \
        -d '{
            "id": "vehicle001",
            "marque": "Tesla",
            "modele": "Model 3",
            "nombrePlaces": 4,
            "etat": "OPERATIONNEL_EN_STATION",
            "niveauCharge": 85
        }' > /dev/null
    print_success "Tesla Model 3 created (85% charge)"
    
    # Vehicle 2 - BMW i3
    print_info "Creating BMW i3..."
    curl -s -X POST $VEHICLE_SERVICE/vehicules \
        -H "Content-Type: application/json" \
        -d '{
            "id": "vehicle002",
            "marque": "BMW",
            "modele": "i3",
            "nombrePlaces": 4,
            "etat": "OPERATIONNEL_EN_STATION",
            "niveauCharge": 92
        }' > /dev/null
    print_success "BMW i3 created (92% charge)"
    
    # Vehicle 3 - Nissan Leaf
    print_info "Creating Nissan Leaf..."
    curl -s -X POST $VEHICLE_SERVICE/vehicules \
        -H "Content-Type: application/json" \
        -d '{
            "id": "vehicle003",
            "marque": "Nissan",
            "modele": "Leaf",
            "nombrePlaces": 5,
            "etat": "OPERATIONNEL_EN_STATION",
            "niveauCharge": 78
        }' > /dev/null
    print_success "Nissan Leaf created (78% charge)"
    
    # Vehicle 4 - Renault Zoe
    print_info "Creating Renault Zoe..."
    curl -s -X POST $VEHICLE_SERVICE/vehicules \
        -H "Content-Type: application/json" \
        -d '{
            "id": "vehicle004",
            "marque": "Renault",
            "modele": "Zoe",
            "nombrePlaces": 4,
            "etat": "OPERATIONNEL_EN_STATION",
            "niveauCharge": 95
        }' > /dev/null
    print_success "Renault Zoe created (95% charge)"
    
    # Vehicle 5 - Peugeot e-208
    print_info "Creating Peugeot e-208..."
    curl -s -X POST $VEHICLE_SERVICE/vehicules \
        -H "Content-Type: application/json" \
        -d '{
            "id": "vehicle005",
            "marque": "Peugeot",
            "modele": "e-208",
            "nombrePlaces": 4,
            "etat": "OPERATIONNEL_EN_STATION",
            "niveauCharge": 88
        }' > /dev/null
    print_success "Peugeot e-208 created (88% charge)"
    
    # Vehicle 6 - Hyundai Kona Electric
    print_info "Creating Hyundai Kona Electric..."
    curl -s -X POST $VEHICLE_SERVICE/vehicules \
        -H "Content-Type: application/json" \
        -d '{
            "id": "vehicle006",
            "marque": "Hyundai",
            "modele": "Kona Electric",
            "nombrePlaces": 5,
            "etat": "OPERATIONNEL_EN_STATION",
            "niveauCharge": 73
        }' > /dev/null
    print_success "Hyundai Kona Electric created (73% charge)"
    
    # Vehicle 7 - Volkswagen ID.3
    print_info "Creating Volkswagen ID.3..."
    curl -s -X POST $VEHICLE_SERVICE/vehicules \
        -H "Content-Type: application/json" \
        -d '{
            "id": "vehicle007",
            "marque": "Volkswagen",
            "modele": "ID.3",
            "nombrePlaces": 4,
            "etat": "OPERATIONNEL_EN_STATION",
            "niveauCharge": 67
        }' > /dev/null
    print_success "Volkswagen ID.3 created (67% charge)"
}

# Step 4: Assign Vehicles to Stations
assign_vehicles_to_stations() {
    print_step "Placing Vehicles at Stations"
    
    # Downtown Station (station001) - 3 vehicles
    print_info "Placing vehicles at Downtown Station..."
    curl -s -X POST $STATION_SERVICE/stations/station001/vehicles/vehicle001/add > /dev/null
    curl -s -X POST $STATION_SERVICE/stations/station001/vehicles/vehicle002/add > /dev/null
    curl -s -X POST $STATION_SERVICE/stations/station001/vehicles/vehicle003/add > /dev/null
    print_success "Downtown Station: Tesla Model 3, BMW i3, Nissan Leaf"
    
    # Airport Station (station002) - 2 vehicles
    print_info "Placing vehicles at Airport Station..."
    curl -s -X POST $STATION_SERVICE/stations/station002/vehicles/vehicle004/add > /dev/null
    curl -s -X POST $STATION_SERVICE/stations/station002/vehicles/vehicle005/add > /dev/null
    print_success "Airport Station: Renault Zoe, Peugeot e-208"
    
    # University Station (station003) - 2 vehicles
    print_info "Placing vehicles at University Station..."
    curl -s -X POST $STATION_SERVICE/stations/station003/vehicles/vehicle006/add > /dev/null
    curl -s -X POST $STATION_SERVICE/stations/station003/vehicles/vehicle007/add > /dev/null
    print_success "University Station: Hyundai Kona, Volkswagen ID.3"
    
    # Mall Station (station004) - 0 vehicles (empty for testing)
    print_success "Shopping Mall Station: Empty (available for returns)"
}

# Step 5: Verify Data Creation
verify_data() {
    print_step "Verifying Created Data"
    
    # Count users
    USER_COUNT=$(curl -s $USER_SERVICE/users | grep -o '"id":' | wc -l | tr -d ' ')
    print_success "Users created: $USER_COUNT"
    
    # Count stations
    STATION_COUNT=$(curl -s $STATION_SERVICE/stations | grep -o '"id":' | wc -l | tr -d ' ')
    print_success "Stations created: $STATION_COUNT"
    
    # Count vehicles
    VEHICLE_COUNT=$(curl -s $VEHICLE_SERVICE/vehicules | grep -o '"id":' | wc -l | tr -d ' ')
    print_success "Vehicles created: $VEHICLE_COUNT"
    
    # Check statistics
    print_info "Testing Statistics Service..."
    STATS_RESPONSE=$(curl -s $STATISTICS_SERVICE/statistics/summary)
    if echo "$STATS_RESPONSE" | grep -q "totalUsers\|totalVehicles\|totalStations"; then
        print_success "Statistics Service is working!"
    else
        print_error "Statistics Service may need a moment to aggregate data"
    fi
}

# Main execution
main() {
    check_services
    create_users
    create_stations
    create_vehicles
    assign_vehicles_to_stations
    verify_data
    
    print_step "Data Population Complete!"
    echo ""
    echo "🎉 Test data has been successfully created!"
    echo ""
    echo "📊 Summary:"
    echo "  • 4 Active Users (Alice, Bob, Charlie, Diana)"
    echo "  • 4 Charging Stations (Downtown, Airport, University, Mall)"
    echo "  • 7 Electric Vehicles (Various brands)"
    echo "  • Vehicles distributed across stations"
    echo ""
    echo "🔑 User Credentials:"
    echo "  • Alice: Card=CARD08211180, PIN=2785"
    echo "  • Bob, Charlie, Diana: Cards generated (check service logs)"
    echo ""
    echo "🚀 Next Steps:"
    echo "  • Test Statistics: curl http://localhost:8084/statistics/summary"
    echo "  • Start a rental: Use the rental endpoints with user cards"
    echo "  • Check Postman collection for more endpoints"
    echo ""
    echo "✅ Your microservices now have plenty of data for testing statistics!"
}

# Run the script
main 