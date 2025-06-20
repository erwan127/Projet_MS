package com.example.stationservice.controller;

import com.example.stationservice.entities.Station;
import com.example.stationservice.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/stations")
@CrossOrigin(origins = "*")
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping
    public ResponseEntity<List<Station>> listerStations() {
        List<Station> stations = stationService.listerStations();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> obtenirStation(@PathVariable String id) {
        Optional<Station> station = stationService.obtenirStation(id);
        return station.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Station> creerStation(@RequestBody Station station) {
        try {
            Station nouvelleStation = stationService.creerStation(station);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleStation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> supprimerStation(@PathVariable String id) {
        boolean supprime = stationService.supprimerStation(id);
        return supprime ? ResponseEntity.ok(true)
                : ResponseEntity.notFound().build();
    }

    // Endpoints pour la gestion des véhicules
    @GetMapping("/{stationId}/vehicles/{vehicleId}/check")
    public ResponseEntity<Map<String, Object>> checkVehicleAtStation(@PathVariable String stationId, @PathVariable String vehicleId) {
        boolean isPresent = stationService.isVehicleAtStation(stationId, vehicleId);
        
        Map<String, Object> response = Map.of(
            "vehicleId", vehicleId,
            "stationId", stationId,
            "isPresent", isPresent
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicles/{vehicleId}/station")
    public ResponseEntity<Station> findStationWithVehicle(@PathVariable String vehicleId) {
        Optional<Station> station = stationService.findStationWithVehicle(vehicleId);
        return station.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{stationId}/vehicles/{vehicleId}/remove")
    public ResponseEntity<Map<String, Object>> removeVehicleFromStation(@PathVariable String stationId, @PathVariable String vehicleId) {
        boolean removed = stationService.removeVehicleFromStation(stationId, vehicleId);
        
        Map<String, Object> response = Map.of(
            "success", removed,
            "message", removed ? "Véhicule retiré de la station avec succès" : "Échec du retrait du véhicule de la station",
            "vehicleId", vehicleId,
            "stationId", stationId
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{stationId}/vehicles/{vehicleId}/add")
    public ResponseEntity<Map<String, Object>> addVehicleToStation(@PathVariable String stationId, @PathVariable String vehicleId) {
        boolean added = stationService.addVehicleToStation(stationId, vehicleId);
        
        Map<String, Object> response = Map.of(
            "success", added,
            "message", added ? "Véhicule ajouté à la station avec succès" : "Échec de l'ajout du véhicule à la station",
            "vehicleId", vehicleId,
            "stationId", stationId
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-vehicles")
    public ResponseEntity<List<Station>> getStationsWithVehicles() {
        List<Station> stations = stationService.getStationsWithVehicles();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/with-space")
    public ResponseEntity<List<Station>> getStationsWithAvailableSpace() {
        List<Station> stations = stationService.getStationsWithAvailableSpace();
        return ResponseEntity.ok(stations);
    }

    // GET endpoint to list vehicles at a station
    @GetMapping("/{stationId}/vehicles")
    public ResponseEntity<List<String>> getVehiclesAtStation(@PathVariable String stationId) {
        Optional<Station> station = stationService.obtenirStation(stationId);
        if (station.isPresent()) {
            List<String> vehicleIds = station.get().getVehiculeIds();
            return ResponseEntity.ok(vehicleIds);
        }
        return ResponseEntity.notFound().build();
    }

    // REMOVED: These endpoints have been removed because rental operations must be initiated
    // through the UserService with proper user authentication and validation.
    // Direct station-level rental operations without user association violate 
    // the Version 2 requirements for user management.
    
    // If you need to start/end rentals, use:
    // POST /users/card/{cardNumber}/start-rental
    // POST /users/card/{cardNumber}/end-rental
    
    // The StationService should only handle internal operations for adding/removing vehicles
    // which are already available through:
    // POST /{stationId}/vehicles/{vehicleId}/add
    // DELETE /{stationId}/vehicles/{vehicleId}/remove

}
