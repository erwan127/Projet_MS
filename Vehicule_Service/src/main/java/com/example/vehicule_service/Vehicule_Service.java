package com.example.vehicule_service;

import com.example.vehicule_service.entities.EtatVehicule;
import com.example.vehicule_service.entities.Vehicule;
import com.example.vehicule_service.repo.VehiculeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Vehicule_Service {

    @Autowired
    private VehiculeRepo vehiculeRepository;

    public List<Vehicule> listerVehicules() {
        return vehiculeRepository.findAll();
    }

    public Optional<Vehicule> obtenirVehicule(String id) {
        return vehiculeRepository.findById(id);
    }

    public Vehicule creerVehicule(Vehicule vehicule) {
        if (vehiculeRepository.existsById(vehicule.getId())) {
            throw new IllegalArgumentException("Véhicule avec cet ID existe déjà");
        }
        return vehiculeRepository.save(vehicule);
    }

    public boolean supprimerVehicule(String id) {
        if (vehiculeRepository.existsById(id)) {
            vehiculeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Méthodes pour la gestion des locations
    public List<Vehicule> listerVehiculesDisponibles() {
        return vehiculeRepository.findByEtat(EtatVehicule.OPERATIONNEL_EN_STATION);
    }

    public boolean isVehiculeDisponible(String vehiculeId) {
        Optional<Vehicule> vehicule = vehiculeRepository.findById(vehiculeId);
        return vehicule.isPresent() && vehicule.get().getEtat() == EtatVehicule.OPERATIONNEL_EN_STATION;
    }

    public Vehicule changerStatutVehicule(String vehiculeId, EtatVehicule nouvelEtat) {
        Optional<Vehicule> vehiculeOpt = vehiculeRepository.findById(vehiculeId);
        if (vehiculeOpt.isEmpty()) {
            throw new IllegalArgumentException("Véhicule non trouvé avec l'ID: " + vehiculeId);
        }

        Vehicule vehicule = vehiculeOpt.get();
        vehicule.setEtat(nouvelEtat);
        return vehiculeRepository.save(vehicule);
    }

    public Vehicule commencerLocation(String vehiculeId) {
        return changerStatutVehicule(vehiculeId, EtatVehicule.OPERATIONNEL_EN_LOCATION);
    }

    public Vehicule terminerLocation(String vehiculeId) {
        return changerStatutVehicule(vehiculeId, EtatVehicule.OPERATIONNEL_EN_STATION);
    }
}
