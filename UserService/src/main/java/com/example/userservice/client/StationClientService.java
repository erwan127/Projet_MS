package com.example.userservice.client;

import com.example.userservice.dto.StationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StationClientService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String STATION_SERVICE_URL = "http://STATION-SERVICE/stations";

    public Optional<StationDTO> findStationWithVehicle(String vehicleId) {
        try {
            String url = STATION_SERVICE_URL + "/vehicles/" + vehicleId + "/station";
            ResponseEntity<StationDTO> response = restTemplate.getForEntity(url, StationDTO.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de station pour le véhicule " + vehicleId + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean isVehicleAtStation(String stationId, String vehicleId) {
        try {
            String url = STATION_SERVICE_URL + "/" + stationId + "/vehicles/" + vehicleId + "/check";
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (Boolean) response.getBody().get("isPresent");
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du véhicule à la station: " + e.getMessage());
            return false;
        }
    }

    public boolean removeVehicleFromStation(String stationId, String vehicleId) {
        try {
            String url = STATION_SERVICE_URL + "/" + stationId + "/vehicles/" + vehicleId + "/remove";
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.DELETE, 
                null, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (Boolean) response.getBody().get("success");
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors du retrait du véhicule de la station: " + e.getMessage());
            return false;
        }
    }

    public boolean addVehicleToStation(String stationId, String vehicleId) {
        try {
            String url = STATION_SERVICE_URL + "/" + stationId + "/vehicles/" + vehicleId + "/add";
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                null, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (Boolean) response.getBody().get("success");
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du véhicule à la station: " + e.getMessage());
            return false;
        }
    }

    public List<StationDTO> getStationsWithVehicles() {
        try {
            String url = STATION_SERVICE_URL + "/with-vehicles";
            ResponseEntity<List<StationDTO>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<StationDTO>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            return List.of();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des stations avec véhicules: " + e.getMessage());
            return List.of();
        }
    }

    public List<StationDTO> getStationsWithAvailableSpace() {
        try {
            String url = STATION_SERVICE_URL + "/with-space";
            ResponseEntity<List<StationDTO>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<StationDTO>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            return List.of();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des stations avec places libres: " + e.getMessage());
            return List.of();
        }
    }

    public Optional<StationDTO> getStation(String stationId) {
        try {
            String url = STATION_SERVICE_URL + "/" + stationId;
            ResponseEntity<StationDTO> response = restTemplate.getForEntity(url, StationDTO.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de la station " + stationId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
} 