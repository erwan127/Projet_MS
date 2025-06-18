package com.example.statisticsservice.dto;

import java.time.LocalDateTime;

public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String cardNumber;
    private String status;
    private LocalDateTime subscriptionDate;
    private LocalDateTime lastLoginDate;
    private String currentRentedVehicleId;

    public UserDTO() {}

    public UserDTO(String id, String firstName, String lastName, String email, 
                  String phoneNumber, String cardNumber, String status, 
                  LocalDateTime subscriptionDate, LocalDateTime lastLoginDate, 
                  String currentRentedVehicleId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cardNumber = cardNumber;
        this.status = status;
        this.subscriptionDate = subscriptionDate;
        this.lastLoginDate = lastLoginDate;
        this.currentRentedVehicleId = currentRentedVehicleId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubscriptionDate() { return subscriptionDate; }
    public void setSubscriptionDate(LocalDateTime subscriptionDate) { this.subscriptionDate = subscriptionDate; }

    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public String getCurrentRentedVehicleId() { return currentRentedVehicleId; }
    public void setCurrentRentedVehicleId(String currentRentedVehicleId) { this.currentRentedVehicleId = currentRentedVehicleId; }

    // Utility methods
    public boolean hasActiveRental() {
        return currentRentedVehicleId != null && !currentRentedVehicleId.isEmpty();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
} 