package com.example.userservice.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    
    @Column(unique = true)
    private String cardNumber;  // Numéro de carte d'accès
    
    @Column(name = "pin_code")
    private String pinCode;     // Code confidentiel
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    private LocalDateTime subscriptionDate;
    private LocalDateTime lastLoginDate;
    
    // Véhicule actuellement loué (null si aucun)
    private String currentRentedVehicleId;
    
    public User() {}
    
    public User(String id, String firstName, String lastName, String email, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = UserStatus.PENDING;
        this.subscriptionDate = LocalDateTime.now();
    }
    
    // Getters et Setters
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
    
    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public LocalDateTime getSubscriptionDate() { return subscriptionDate; }
    public void setSubscriptionDate(LocalDateTime subscriptionDate) { this.subscriptionDate = subscriptionDate; }
    
    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }
    
    public String getCurrentRentedVehicleId() { return currentRentedVehicleId; }
    public void setCurrentRentedVehicleId(String currentRentedVehicleId) { this.currentRentedVehicleId = currentRentedVehicleId; }
    
    // Méthodes utilitaires
    public boolean hasActiveRental() {
        return currentRentedVehicleId != null && !currentRentedVehicleId.isEmpty();
    }
    
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 