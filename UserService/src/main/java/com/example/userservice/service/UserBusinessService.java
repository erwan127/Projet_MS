package com.example.userservice.service;

import com.example.userservice.entities.User;
import com.example.userservice.entities.UserStatus;
import com.example.userservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserBusinessService {

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    // Lister tous les utilisateurs
    public List<User> listerUtilisateurs() {
        return userRepository.findAll();
    }

    // Lister les utilisateurs actifs
    public List<User> listerUtilisateursActifs() {
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }

    // Obtenir un utilisateur par ID
    public Optional<User> obtenirUtilisateur(String id) {
        return userRepository.findById(id);
    }

    // Obtenir un utilisateur par numéro de carte
    public Optional<User> obtenirUtilisateurParCarte(String cardNumber) {
        return userRepository.findByCardNumber(cardNumber);
    }

    // Créer un abonnement utilisateur
    public User creerAbonnement(User user) {
        // Vérifications
        if (userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("Utilisateur avec cet ID existe déjà");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        // Générer numéro de carte et code PIN
        user.setCardNumber(genererNumeroCarte());
        user.setPinCode(genererCodePin());
        user.setStatus(UserStatus.PENDING);
        user.setSubscriptionDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    // Activer un abonnement
    public User activerAbonnement(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        User user = userOpt.get();
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    // Suspendre un abonnement
    public User suspendrAbonnement(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        User user = userOpt.get();
        if (user.hasActiveRental()) {
            throw new IllegalStateException("Impossible de suspendre : véhicule en cours de location");
        }

        user.setStatus(UserStatus.SUSPENDED);
        return userRepository.save(user);
    }

    // Authentifier un utilisateur avec carte et PIN
    public boolean authentifierUtilisateur(String cardNumber, String pinCode) {
        Optional<User> userOpt = userRepository.findByCardNumber(cardNumber);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            return false;
        }

        boolean isAuthenticated = user.getPinCode().equals(pinCode);
        if (isAuthenticated) {
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);
        }

        return isAuthenticated;
    }

    // Vérifier si un utilisateur peut louer un véhicule
    public boolean peutLouerVehicule(String cardNumber) {
        Optional<User> userOpt = userRepository.findByCardNumber(cardNumber);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return user.isActive() && !user.hasActiveRental();
    }

    // Marquer un véhicule comme loué par un utilisateur
    public User commencerLocation(String cardNumber, String vehicleId) {
        Optional<User> userOpt = userRepository.findByCardNumber(cardNumber);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            throw new IllegalStateException("Utilisateur non actif");
        }

        if (user.hasActiveRental()) {
            throw new IllegalStateException("Utilisateur a déjà un véhicule en location");
        }

        user.setCurrentRentedVehicleId(vehicleId);
        return userRepository.save(user);
    }

    // Terminer la location d'un véhicule
    public User terminerLocation(String cardNumber) {
        Optional<User> userOpt = userRepository.findByCardNumber(cardNumber);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        User user = userOpt.get();
        if (!user.hasActiveRental()) {
            throw new IllegalStateException("Aucun véhicule en location");
        }

        user.setCurrentRentedVehicleId(null);
        return userRepository.save(user);
    }

    // Supprimer un utilisateur
    public boolean supprimerUtilisateur(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        if (user.hasActiveRental()) {
            throw new IllegalStateException("Impossible de supprimer : véhicule en cours de location");
        }

        userRepository.deleteById(id);
        return true;
    }

    // Sauvegarder un utilisateur (utilitaire pour les mises à jour)
    public User sauvegarderUtilisateur(User user) {
        return userRepository.save(user);
    }

    // Méthodes privées utilitaires
    private String genererNumeroCarte() {
        String cardNumber;
        do {
            cardNumber = String.format("CARD%08d", random.nextInt(100000000));
        } while (userRepository.existsByCardNumber(cardNumber));
        
        return cardNumber;
    }

    private String genererCodePin() {
        return String.format("%04d", random.nextInt(10000));
    }

    // Rechercher des utilisateurs par nom
    public List<User> rechercherUtilisateurs(String searchTerm) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            searchTerm, searchTerm
        );
    }

    // Obtenir l'utilisateur qui a loué un véhicule spécifique
    public Optional<User> obtenirUtilisateurParVehiculeLoue(String vehicleId) {
        return userRepository.findByCurrentRentedVehicleId(vehicleId);
    }
} 