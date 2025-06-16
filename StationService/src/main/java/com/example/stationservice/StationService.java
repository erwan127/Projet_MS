package com.example.stationservice;

import com.example.stationservice.entities.Station;
import com.example.stationservice.repo.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
