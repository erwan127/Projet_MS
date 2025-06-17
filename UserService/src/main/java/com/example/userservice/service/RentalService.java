package com.example.userservice.service;

import com.example.userservice.client.StationClientService;
import com.example.userservice.client.VehicleClientService;
import com.example.userservice.dto.StationDTO;
import com.example.userservice.dto.VehicleDTO;
import com.example.userservice.entities.User;
import com.example.userservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RentalService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleClientService vehicleClientService;

    @Autowired
    private StationClientService stationClientService;

    @Autowired
    private UserBusinessService userBusinessService;

    @Transactional
    public User startRental(String cardNumber, String vehicleId) {
        // 1. Valider l'utilisateur
        Optional<User> userOpt = userRepository.findByCardNumber(cardNumber);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec la carte: " + cardNumber);
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            throw new IllegalStateException("Utilisateur non actif");
        }

        if (user.hasActiveRental()) {
            throw new IllegalStateException("Utilisateur a déjà un véhicule en location");
        }

        // 2. Vérifier la disponibilité du véhicule
        Optional<VehicleDTO> vehicleOpt = vehicleClientService.getVehicle(vehicleId);
        if (vehicleOpt.isEmpty()) {
            throw new IllegalArgumentException("Véhicule non trouvé: " + vehicleId);
        }

        VehicleDTO vehicle = vehicleOpt.get();
        if (!vehicle.isDisponible()) {
            throw new IllegalStateException("Véhicule non disponible pour la location");
        }

        // 3. NOUVEAU: Vérifier que le véhicule est dans une station de recharge
        Optional<StationDTO> stationOpt = stationClientService.findStationWithVehicle(vehicleId);
        if (stationOpt.isEmpty()) {
            throw new IllegalStateException("Le véhicule doit être dans une station de recharge pour commencer la location");
        }

        StationDTO station = stationOpt.get();

        // 4. Vérifier encore une fois la disponibilité en temps réel
        if (!vehicleClientService.isVehicleAvailable(vehicleId)) {
            throw new IllegalStateException("Véhicule plus disponible");
        }

        // 5. Retirer le véhicule de la station de recharge
        boolean removedFromStation = stationClientService.removeVehicleFromStation(station.getId(), vehicleId);
        if (!removedFromStation) {
            throw new RuntimeException("Échec de la suppression du véhicule de la station de recharge");
        }

        try {
            // 6. Commencer la location côté véhicule
            boolean vehicleRented = vehicleClientService.startRental(vehicleId);
            if (!vehicleRented) {
                // Rollback: remettre le véhicule dans la station
                stationClientService.addVehicleToStation(station.getId(), vehicleId);
                throw new RuntimeException("Échec de la mise à jour du statut du véhicule");
            }

            // 7. Mettre à jour l'utilisateur
            user.setCurrentRentedVehicleId(vehicleId);
            User savedUser = userRepository.save(user);
            return savedUser;
        } catch (Exception e) {
            // Rollback complet
            stationClientService.addVehicleToStation(station.getId(), vehicleId);
            vehicleClientService.endRental(vehicleId);
            throw new RuntimeException("Erreur lors de l'enregistrement de la location utilisateur", e);
        }
    }

    @Transactional
    public User endRental(String cardNumber, String stationId) {
        // 1. Valider l'utilisateur
        Optional<User> userOpt = userRepository.findByCardNumber(cardNumber);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec la carte: " + cardNumber);
        }

        User user = userOpt.get();
        if (!user.hasActiveRental()) {
            throw new IllegalStateException("Aucun véhicule en location pour cet utilisateur");
        }

        String vehicleId = user.getCurrentRentedVehicleId();

        // 2. NOUVEAU: Vérifier que la station de destination existe et a de la place
        Optional<StationDTO> stationOpt = stationClientService.getStation(stationId);
        if (stationOpt.isEmpty()) {
            throw new IllegalArgumentException("Station de recharge non trouvée: " + stationId);
        }

        StationDTO station = stationOpt.get();
        if (!station.hasAvailableSpace()) {
            throw new IllegalStateException("La station de recharge n'a pas de place disponible");
        }

        // 3. Ajouter le véhicule à la station de recharge
        boolean addedToStation = stationClientService.addVehicleToStation(stationId, vehicleId);
        if (!addedToStation) {
            throw new RuntimeException("Échec de l'ajout du véhicule à la station de recharge");
        }

        try {
            // 4. Terminer la location côté véhicule
            boolean vehicleReturned = vehicleClientService.endRental(vehicleId);
            if (!vehicleReturned) {
                // Rollback: retirer le véhicule de la station
                stationClientService.removeVehicleFromStation(stationId, vehicleId);
                throw new RuntimeException("Échec de la mise à jour du statut du véhicule");
            }

            // 5. Mettre à jour l'utilisateur
            user.setCurrentRentedVehicleId(null);
            User savedUser = userRepository.save(user);
            return savedUser;
        } catch (Exception e) {
            // Rollback: retirer le véhicule de la station
            stationClientService.removeVehicleFromStation(stationId, vehicleId);
            vehicleClientService.startRental(vehicleId);
            throw new RuntimeException("Erreur lors de la fin de location utilisateur", e);
        }
    }

    // Méthode de compatibilité pour l'ancienne API sans stationId
    @Transactional
    public User endRental(String cardNumber) {
        throw new IllegalArgumentException("La restitution d'un véhicule nécessite maintenant de spécifier une station de recharge. Utilisez endRental(cardNumber, stationId)");
    }

    public boolean canUserRentVehicle(String cardNumber, String vehicleId) {
        // Vérifier l'utilisateur
        if (!userBusinessService.peutLouerVehicule(cardNumber)) {
            return false;
        }

        // Vérifier le véhicule
        if (!vehicleClientService.isVehicleAvailable(vehicleId)) {
            return false;
        }

        // NOUVEAU: Vérifier que le véhicule est dans une station
        Optional<StationDTO> stationOpt = stationClientService.findStationWithVehicle(vehicleId);
        return stationOpt.isPresent();
    }

    public Optional<VehicleDTO> getUserCurrentVehicle(String cardNumber) {
        Optional<User> userOpt = userRepository.findByCardNumber(cardNumber);
        if (userOpt.isEmpty() || !userOpt.get().hasActiveRental()) {
            return Optional.empty();
        }

        String vehicleId = userOpt.get().getCurrentRentedVehicleId();
        return vehicleClientService.getVehicle(vehicleId);
    }

    // NOUVELLES MÉTHODES pour la gestion des stations
    public Optional<StationDTO> getVehicleStation(String vehicleId) {
        return stationClientService.findStationWithVehicle(vehicleId);
    }

    public boolean canReturnVehicleToStation(String stationId) {
        Optional<StationDTO> stationOpt = stationClientService.getStation(stationId);
        return stationOpt.map(StationDTO::hasAvailableSpace).orElse(false);
    }
} 