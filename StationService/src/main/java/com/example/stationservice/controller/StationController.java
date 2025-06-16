package com.example.stationservice.controller;

import com.example.stationservice.entities.Station;
import com.example.stationservice.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}
