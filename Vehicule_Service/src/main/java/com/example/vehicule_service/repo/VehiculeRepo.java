package com.example.vehicule_service.repo;

import com.example.vehicule_service.entities.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculeRepo extends JpaRepository<Vehicule, String> {
}

