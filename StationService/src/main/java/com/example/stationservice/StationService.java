package com.example.stationservice;

import com.example.stationservice.entities.Station;
import com.example.stationservice.repo.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    public List<Station> listerStations() {
        return stationRepository.findAll();
    }

    public Optional<Station> obtenirStation(String id) {
        return stationRepository.findById(id);
    }

    public Station creerStation(Station station) {
        if (stationRepository.existsById(station.getId())) {
            throw new IllegalArgumentException("Station avec cet ID existe déjà");
        }
        return stationRepository.save(station);
    }

    public boolean supprimerStation(String id) {
        if (stationRepository.existsById(id)) {
            stationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean isVehicleAtStation(String stationId, String vehicleId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);
        return stationOpt.map(station -> station.getVehiculeIds().contains(vehicleId)).orElse(false);
    }

    public Optional<Station> findStationWithVehicle(String vehicleId) {
        return stationRepository.findAll().stream()
                .filter(station -> station.getVehiculeIds().contains(vehicleId))
                .findFirst();
    }

    @Transactional
    public boolean removeVehicleFromStation(String stationId, String vehicleId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);
        if (stationOpt.isPresent()) {
            Station station = stationOpt.get();
            if (station.getVehiculeIds().contains(vehicleId)) {
                station.getVehiculeIds().remove(vehicleId);
                stationRepository.save(station);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean addVehicleToStation(String stationId, String vehicleId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);
        if (stationOpt.isPresent()) {
            Station station = stationOpt.get();
            if (station.getNombrePlacesLibres() > 0 && !station.getVehiculeIds().contains(vehicleId)) {
                station.getVehiculeIds().add(vehicleId);
                stationRepository.save(station);
                return true;
            }
        }
        return false;
    }

    public List<Station> getStationsWithAvailableSpace() {
        return stationRepository.findAll().stream()
                .filter(station -> station.getNombrePlacesLibres() > 0)
                .toList();
    }

    public List<Station> getStationsWithVehicles() {
        return stationRepository.findAll().stream()
                .filter(station -> station.getNombreVehiculesGares() > 0)
                .toList();
    }

    // REMOVED: startRental and endRental methods
    // These operations should only be performed through the UserService 
    // with proper user authentication and validation.
    // 
    // The StationService should only handle internal operations like:
    // - addVehicleToStation() 
    // - removeVehicleFromStation()
    // 
    // Rental operations require user verification which is handled by UserService
}
