#!/bin/bash

# Database Setup Script for Microservices
echo "=== Setting up MySQL Databases for Microservices ==="

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    print_error "MySQL is not installed. Installing via Homebrew..."
    if ! command -v brew &> /dev/null; then
        print_error "Homebrew is not installed. Please install Homebrew first."
        exit 1
    fi
    brew install mysql
    brew services start mysql
    print_success "MySQL installed and started"
else
    print_info "MySQL is already installed"
fi

# Start MySQL service if not running
if ! brew services list | grep mysql | grep started > /dev/null; then
    print_info "Starting MySQL service..."
    brew services start mysql
    sleep 5
    print_success "MySQL service started"
fi

# Function to create database if not exists
create_database() {
    local db_name=$1
    local port=$2
    
    print_info "Creating database: $db_name"
    
    # Create database
    mysql -u root -e "CREATE DATABASE IF NOT EXISTS $db_name;" 2>/dev/null || {
        print_error "Failed to create database $db_name. Trying with password..."
        mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS $db_name;"
    }
    
    print_success "Database $db_name ready"
}

# Create required databases
print_info "Creating databases for microservices..."
create_database "vehicule_DB"
create_database "station_DB" 
create_database "user_DB"
create_database "statistics_DB"

print_success "All databases created successfully!"
print_info "You can now start the microservices with: ./start_services.sh"

echo ""
echo "Database Configuration:"
echo "- vehicule_DB: localhost:3306"
echo "- station_DB: localhost:3307 (configured in app but using default port 3306)"
echo "- user_DB: localhost:3308 (configured in app but using default port 3306)" 
echo "- statistics_DB: localhost:3309 (configured in app but using default port 3306)"
echo ""
echo "Note: All databases are created on the default MySQL port (3306)."
echo "The services are configured to use different ports, but they'll connect to the default MySQL instance." 