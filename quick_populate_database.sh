#!/bin/bash

echo "🗄️ Quick Database Population Script for University Examination"
echo "=============================================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Base URLs
API_GATEWAY="http://localhost:8080"
USER_SERVICE="http://localhost:8083"
VEHICLE_SERVICE="http://localhost:8081"
STATION_SERVICE="http://localhost:8082"
STATISTICS_SERVICE="http://localhost:8084"

echo ""
print_info "Creating Users..."

# Create User 1 - Alice
print_info "Creating Alice..."
ALICE_RESULT=$(curl -s -X POST $USER_SERVICE/users/subscription \
  -H "Content-Type: application/json" \
  -d '{
    "id": "user001",
    "firstName": "Alice",
    "lastName": "Johnson",
    "email": "alice.johnson@test.com",
    "phoneNumber": "0612345678"
  }')

if echo "$ALICE_RESULT" | grep -q "id"; then
    curl -s -X PUT $USER_SERVICE/users/user001/activate > /dev/null
    print_success "Alice created and activated"
elif echo "$ALICE_RESULT" | grep -q "existe déjà"; then
    print_warning "Alice already exists"
    # Try to activate existing user
    curl -s -X PUT $USER_SERVICE/users/user001/activate > /dev/null 2>&1
else
    print_error "Failed to create Alice"
fi

# Create User 2 - Bob
print_info "Creating Bob..."
BOB_RESULT=$(curl -s -X POST $USER_SERVICE/users/subscription \
  -H "Content-Type: application/json" \
  -d '{
    "id": "user002",
    "firstName": "Bob",
    "lastName": "Smith",
    "email": "bob.smith@test.com",
    "phoneNumber": "0987654321"
  }')

if echo "$BOB_RESULT" | grep -q "id"; then
    curl -s -X PUT $USER_SERVICE/users/user002/activate > /dev/null
    print_success "Bob created and activated"
elif echo "$BOB_RESULT" | grep -q "existe déjà"; then
    print_warning "Bob already exists"
    curl -s -X PUT $USER_SERVICE/users/user002/activate > /dev/null 2>&1
else
    print_error "Failed to create Bob"
fi

echo ""
print_info "Creating Stations..."

# Create Station 1 - Downtown
print_info "Creating Downtown Station..."
STATION1_RESULT=$(curl -s -X POST $STATION_SERVICE/stations \
  -H "Content-Type: application/json" \
  -d '{
    "id": "station001",
    "position": {"x": 10, "y": 20},
    "capaciteGlobale": 5
  }')

if echo "$STATION1_RESULT" | grep -q "id"; then
    print_success "Downtown Station created (capacity: 5)"
elif echo "$STATION1_RESULT" | grep -q "existe déjà"; then
    print_warning "Downtown Station already exists"
else
    print_error "Failed to create Downtown Station"
fi

# Create Station 2 - Airport
print_info "Creating Airport Station..."
STATION2_RESULT=$(curl -s -X POST $STATION_SERVICE/stations \
  -H "Content-Type: application/json" \
  -d '{
    "id": "station002",
    "position": {"x": 50, "y": 60},
    "capaciteGlobale": 3
  }')

if echo "$STATION2_RESULT" | grep -q "id"; then
    print_success "Airport Station created (capacity: 3)"
elif echo "$STATION2_RESULT" | grep -q "existe déjà"; then
    print_warning "Airport Station already exists"
else
    print_error "Failed to create Airport Station"
fi

echo ""
print_info "Creating Vehicles..."

# Create Vehicle 1 - Tesla Model 3
print_info "Creating Tesla Model 3..."
VEHICLE1_RESULT=$(curl -s -X POST $VEHICLE_SERVICE/vehicules \
  -H "Content-Type: application/json" \
  -d '{
    "id": "vehicle001",
    "marque": "Tesla",
    "modele": "Model 3",
    "nombrePlaces": 4,
    "kilometrage": 0.0,
    "etat": "OPERATIONNEL_EN_STATION",
    "position": {"x": 0, "y": 0},
    "niveauCharge": 85
  }')

if echo "$VEHICLE1_RESULT" | grep -q "id"; then
    print_success "Tesla Model 3 created (85% charge)"
elif echo "$VEHICLE1_RESULT" | grep -q "existe déjà"; then
    print_warning "Tesla Model 3 already exists"
else
    print_error "Failed to create Tesla Model 3"
fi

# Create Vehicle 2 - BMW i3
print_info "Creating BMW i3..."
VEHICLE2_RESULT=$(curl -s -X POST $VEHICLE_SERVICE/vehicules \
  -H "Content-Type: application/json" \
  -d '{
    "id": "vehicle002",
    "marque": "BMW",
    "modele": "i3",
    "nombrePlaces": 4,
    "kilometrage": 0.0,
    "etat": "OPERATIONNEL_EN_STATION",
    "position": {"x": 0, "y": 0},
    "niveauCharge": 92
  }')

if echo "$VEHICLE2_RESULT" | grep -q "id"; then
    print_success "BMW i3 created (92% charge)"
elif echo "$VEHICLE2_RESULT" | grep -q "existe déjà"; then
    print_warning "BMW i3 already exists"
else
    print_error "Failed to create BMW i3"
fi

echo ""
print_info "Assigning Vehicles to Stations..."

# Assign vehicles to stations
print_info "Placing Tesla at Downtown Station..."
ASSIGN1_RESULT=$(curl -s -X POST $STATION_SERVICE/stations/station001/vehicles/vehicle001/add)
if echo "$ASSIGN1_RESULT" | grep -q '"success":true'; then
    print_success "Tesla assigned to Downtown Station"
elif echo "$ASSIGN1_RESULT" | grep -q "déjà"; then
    print_warning "Tesla already at a station"
else
    print_error "Failed to assign Tesla to station"
fi

print_info "Placing BMW at Airport Station..."
ASSIGN2_RESULT=$(curl -s -X POST $STATION_SERVICE/stations/station002/vehicles/vehicle002/add)
if echo "$ASSIGN2_RESULT" | grep -q '"success":true'; then
    print_success "BMW assigned to Airport Station"
elif echo "$ASSIGN2_RESULT" | grep -q "déjà"; then
    print_warning "BMW already at a station"
else
    print_error "Failed to assign BMW to station"
fi

echo ""
print_info "Triggering Statistics Collection..."
curl -s -X POST $STATISTICS_SERVICE/statistics/collect > /dev/null
print_success "Statistics collection triggered"

echo ""
print_info "Verifying Data Creation..."

# Count created entities
USER_COUNT=$(curl -s $API_GATEWAY/api/users 2>/dev/null | grep -o '"id":' | wc -l | tr -d ' ')
STATION_COUNT=$(curl -s $API_GATEWAY/api/stations 2>/dev/null | grep -o '"id":' | wc -l | tr -d ' ')
VEHICLE_COUNT=$(curl -s $API_GATEWAY/api/vehicules 2>/dev/null | grep -o '"id":' | wc -l | tr -d ' ')

print_success "Total: $USER_COUNT users, $STATION_COUNT stations, $VEHICLE_COUNT vehicles"

echo ""
echo "🎉 Database Population Complete!"
echo ""
echo "📊 Quick Test Commands:"
echo "  curl $API_GATEWAY/api/users"
echo "  curl $API_GATEWAY/api/stations"
echo "  curl $API_GATEWAY/api/vehicules"
echo "  curl $API_GATEWAY/api/statistics/health"
echo ""
echo "🔑 User Credentials (check above for card numbers and PINs):"
echo "  Alice: user001"
echo "  Bob: user002"
echo ""
echo "✅ Ready for university examination!" 