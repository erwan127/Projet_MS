package com.example.userservice.repo;

import com.example.userservice.entities.User;
import com.example.userservice.entities.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // Recherche par numéro de carte
    Optional<User> findByCardNumber(String cardNumber);
    
    // Recherche par email
    Optional<User> findByEmail(String email);
    
    // Recherche par statut
    List<User> findByStatus(UserStatus status);
    
    // Vérifier si un utilisateur a un véhicule en location
    Optional<User> findByCurrentRentedVehicleId(String vehicleId);
    
    // Recherche par nom complet (ignorer la casse)
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    
    // Vérifier l'existence d'une carte
    boolean existsByCardNumber(String cardNumber);
    
    // Vérifier l'existence d'un email
    boolean existsByEmail(String email);
} 