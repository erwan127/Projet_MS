package com.example.userservice.controller;

import com.example.userservice.dto.StationDTO;
import com.example.userservice.dto.VehicleDTO;
import com.example.userservice.service.RentalService;
import com.example.userservice.service.UserBusinessService;
import com.example.userservice.entities.User;
import com.example.userservice.client.StationClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserBusinessService userService;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private StationClientService stationClientService;

    // Lister tous les utilisateurs
    @GetMapping
    public ResponseEntity<List<User>> listerUtilisateurs() {
        List<User> users = userService.listerUtilisateurs();
        return ResponseEntity.ok(users);
    }

    // Lister seulement les utilisateurs actifs
    @GetMapping("/active")
    public ResponseEntity<List<User>> listerUtilisateursActifs() {
        List<User> users = userService.listerUtilisateursActifs();
        return ResponseEntity.ok(users);
    }

    // Obtenir un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<User> obtenirUtilisateur(@PathVariable String id) {
        Optional<User> user = userService.obtenirUtilisateur(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtenir un utilisateur par numéro de carte
    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<User> obtenirUtilisateurParCarte(@PathVariable String cardNumber) {
        Optional<User> user = userService.obtenirUtilisateurParCarte(cardNumber);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Créer un abonnement utilisateur
    @PostMapping("/subscription")
    public ResponseEntity<User> creerAbonnement(@RequestBody User user) {
        try {
            User newUser = userService.creerAbonnement(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Activer un abonnement
    @PutMapping("/{id}/activate")
    public ResponseEntity<User> activerAbonnement(@PathVariable String id) {
        try {
            User activatedUser = userService.activerAbonnement(id);
            return ResponseEntity.ok(activatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Suspendre un abonnement
    @PutMapping("/{id}/suspend")
    public ResponseEntity<User> suspendrAbonnement(@PathVariable String id) {
        try {
            User suspendedUser = userService.suspendrAbonnement(id);
            return ResponseEntity.ok(suspendedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Authentifier un utilisateur
    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> authentifierUtilisateur(@RequestBody Map<String, String> credentials) {
        String cardNumber = credentials.get("cardNumber");
        String pinCode = credentials.get("pinCode");
        
        boolean isAuthenticated = userService.authentifierUtilisateur(cardNumber, pinCode);
        
        Map<String, Object> response = Map.of(
            "authenticated", isAuthenticated,
            "message", isAuthenticated ? "Authentification réussie" : "Authentification échouée"
        );
        
        return ResponseEntity.ok(response);
    }

    // Vérifier si un utilisateur peut louer un véhicule
    @GetMapping("/card/{cardNumber}/can-rent")
    public ResponseEntity<Map<String, Object>> peutLouerVehicule(@PathVariable String cardNumber) {
        boolean canRent = userService.peutLouerVehicule(cardNumber);
        
        Map<String, Object> response = Map.of(
            "canRent", canRent,
            "message", canRent ? "Utilisateur peut louer un véhicule" : "Utilisateur ne peut pas louer un véhicule"
        );
        
        return ResponseEntity.ok(response);
    }

    // Vérifier si un utilisateur peut louer un véhicule spécifique
    @GetMapping("/card/{cardNumber}/can-rent/{vehicleId}")
    public ResponseEntity<Map<String, Object>> peutLouerVehiculeSpecifique(@PathVariable String cardNumber, @PathVariable String vehicleId) {
        boolean canRent = rentalService.canUserRentVehicle(cardNumber, vehicleId);
        
        Map<String, Object> response = Map.of(
            "canRent", canRent,
            "message", canRent ? "Utilisateur peut louer ce véhicule" : "Utilisateur ne peut pas louer ce véhicule"
        );
        
        return ResponseEntity.ok(response);
    }

    // Commencer une location avec validation complète depuis une station
    @PostMapping("/card/{cardNumber}/start-rental")
    public ResponseEntity<Map<String, Object>> commencerLocation(@PathVariable String cardNumber, @RequestBody Map<String, String> request) {
        try {
            String vehicleId = request.get("vehicleId");
            User user = rentalService.startRental(cardNumber, vehicleId);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Location démarrée avec succès depuis la station de recharge",
                "user", user
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", e.getMessage(),
                "error", "VALIDATION_ERROR"
            );
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", e.getMessage(),
                "error", "STATE_ERROR"
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "Erreur système lors de la location",
                "error", "SYSTEM_ERROR"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Terminer une location avec validation complète vers une station de recharge
    @PostMapping("/card/{cardNumber}/end-rental")
    public ResponseEntity<Map<String, Object>> terminerLocation(@PathVariable String cardNumber, @RequestBody Map<String, String> request) {
        try {
            String stationId = request.get("stationId");
            if (stationId == null || stationId.trim().isEmpty()) {
                Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "L'ID de la station de recharge est requis pour terminer la location",
                    "error", "MISSING_STATION_ID"
                );
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = rentalService.endRental(cardNumber, stationId);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Location terminée avec succès à la station de recharge",
                "user", user
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", e.getMessage(),
                "error", "VALIDATION_ERROR"
            );
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", e.getMessage(),
                "error", "STATE_ERROR"
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "Erreur système lors de la fin de location",
                "error", "SYSTEM_ERROR"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtenir le véhicule actuellement loué par un utilisateur
    @GetMapping("/card/{cardNumber}/current-vehicle")
    public ResponseEntity<VehicleDTO> obtenirVehiculeActuel(@PathVariable String cardNumber) {
        Optional<VehicleDTO> vehicle = rentalService.getUserCurrentVehicle(cardNumber);
        return vehicle.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // NOUVEAUX ENDPOINTS pour la gestion des stations
    
    // Obtenir toutes les stations avec des véhicules disponibles
    @GetMapping("/stations/with-vehicles")
    public ResponseEntity<List<StationDTO>> getStationsWithVehicles() {
        List<StationDTO> stations = stationClientService.getStationsWithVehicles();
        return ResponseEntity.ok(stations);
    }

    // Obtenir toutes les stations avec des places libres
    @GetMapping("/stations/with-space")
    public ResponseEntity<List<StationDTO>> getStationsWithAvailableSpace() {
        List<StationDTO> stations = stationClientService.getStationsWithAvailableSpace();
        return ResponseEntity.ok(stations);
    }

    // Obtenir la station où se trouve un véhicule
    @GetMapping("/vehicles/{vehicleId}/station")
    public ResponseEntity<StationDTO> getVehicleStation(@PathVariable String vehicleId) {
        Optional<StationDTO> station = rentalService.getVehicleStation(vehicleId);
        return station.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Vérifier si on peut rendre un véhicule à une station
    @GetMapping("/stations/{stationId}/can-return")
    public ResponseEntity<Map<String, Object>> canReturnVehicleToStation(@PathVariable String stationId) {
        boolean canReturn = rentalService.canReturnVehicleToStation(stationId);
        
        Map<String, Object> response = Map.of(
            "canReturn", canReturn,
            "stationId", stationId,
            "message", canReturn ? "Véhicule peut être rendu à cette station" : "Station pleine ou inexistante"
        );
        
        return ResponseEntity.ok(response);
    }

    // Rechercher des utilisateurs
    @GetMapping("/search")
    public ResponseEntity<List<User>> rechercherUtilisateurs(@RequestParam String q) {
        List<User> users = userService.rechercherUtilisateurs(q);
        return ResponseEntity.ok(users);
    }

    // Obtenir l'utilisateur qui a loué un véhicule
    @GetMapping("/vehicle/{vehicleId}/renter")
    public ResponseEntity<User> obtenirUtilisateurParVehiculeLoue(@PathVariable String vehicleId) {
        Optional<User> user = userService.obtenirUtilisateurParVehiculeLoue(vehicleId);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> supprimerUtilisateur(@PathVariable String id) {
        try {
            boolean deleted = userService.supprimerUtilisateur(id);
            return deleted ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
} 