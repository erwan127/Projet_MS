package com.example.vehicule_service.controller;

import com.example.vehicule_service.Vehicule_Service;
import com.example.vehicule_service.entities.EtatVehicule;
import com.example.vehicule_service.entities.Vehicule;
import com.example.vehicule_service.repo.VehiculeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

    @GetMapping("/available")
    public ResponseEntity<List<Vehicule>> listerVehiculesDisponibles() {
        List<Vehicule> vehicules = vehiculeService.listerVehiculesDisponibles();
        return ResponseEntity.ok(vehicules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicule> obtenirVehicule(@PathVariable String id) {
        Optional<Vehicule> vehicule = vehiculeService.obtenirVehicule(id);
        return vehicule.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/available")
    public ResponseEntity<Map<String, Boolean>> verifierDisponibilite(@PathVariable String id) {
        boolean available = vehiculeService.isVehiculeDisponible(id);
        return ResponseEntity.ok(Map.of("available", available));
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

    @PutMapping("/{id}/status")
    public ResponseEntity<Vehicule> changerStatutVehicule(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String statut = request.get("status");
            EtatVehicule nouvelEtat = EtatVehicule.valueOf(statut);
            Vehicule vehicule = vehiculeService.changerStatutVehicule(id, nouvelEtat);
            return ResponseEntity.ok(vehicule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/rent")
    public ResponseEntity<Vehicule> commencerLocation(@PathVariable String id) {
        try {
            Vehicule vehicule = vehiculeService.commencerLocation(id);
            return ResponseEntity.ok(vehicule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Vehicule> terminerLocation(@PathVariable String id) {
        try {
            Vehicule vehicule = vehiculeService.terminerLocation(id);
            return ResponseEntity.ok(vehicule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> supprimerVehicule(@PathVariable String id) {
        boolean supprime = vehiculeService.supprimerVehicule(id);
        return supprime ? ResponseEntity.ok(true)
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicule> modifierVehicule(@PathVariable String id, @RequestBody Vehicule vehicule) {
        try {
            Optional<Vehicule> existingVehicule = vehiculeService.obtenirVehicule(id);
            if (existingVehicule.isPresent()) {
                vehicule.setId(id);
                Vehicule updatedVehicule = vehiculeService.creerVehicule(vehicule);
                return ResponseEntity.ok(updatedVehicule);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
