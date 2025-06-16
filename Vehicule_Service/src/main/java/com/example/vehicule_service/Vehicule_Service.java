package com.example.vehicule_service;

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
}
