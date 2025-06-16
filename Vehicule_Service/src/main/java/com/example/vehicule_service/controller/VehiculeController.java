package com.example.vehicule_service.controller;

import com.example.vehicule_service.Vehicule_Service;
import com.example.vehicule_service.entities.Vehicule;
import com.example.vehicule_service.repo.VehiculeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vehicules")
@CrossOrigin(origins = "*")
public class VehiculeController {

    @Autowired
    private Vehicule_Service vehiculeService;

    @GetMapping
    public ResponseEntity<List<Vehicule>> listerVehicules() {
        List<Vehicule> vehicules = vehiculeService.listerVehicules();
        return ResponseEntity.ok(vehicules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicule> obtenirVehicule(@PathVariable String id) {
        Optional<Vehicule> vehicule = vehiculeService.obtenirVehicule(id);
        return vehicule.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Vehicule> creerVehicule(@RequestBody Vehicule vehicule) {
        try {
            Vehicule nouveauVehicule = vehiculeService.creerVehicule(vehicule);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauVehicule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> supprimerVehicule(@PathVariable String id) {
        boolean supprime = vehiculeService.supprimerVehicule(id);
        return supprime ? ResponseEntity.ok(true)
                : ResponseEntity.notFound().build();
    }
}
