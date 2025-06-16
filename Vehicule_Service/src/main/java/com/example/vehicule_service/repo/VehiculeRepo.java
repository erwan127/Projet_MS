package com.example.vehicule_service.repo;

import com.example.vehicule_service.entities.EtatVehicule;
import com.example.vehicule_service.entities.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculeRepo extends JpaRepository<Vehicule, String> {
    List<Vehicule> findByEtat(EtatVehicule etat);
}

