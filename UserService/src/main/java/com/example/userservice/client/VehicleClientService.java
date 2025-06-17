package com.example.userservice.client;

import com.example.userservice.dto.VehicleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class VehicleClientService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String VEHICLE_SERVICE_URL = "http://vehicule-service/vehicules";

    public Optional<VehicleDTO> getVehicle(String vehicleId) {
        try {
            ResponseEntity<VehicleDTO> response = restTemplate.getForEntity(
                VEHICLE_SERVICE_URL + "/" + vehicleId, 
                VehicleDTO.class
            );
            return response.getStatusCode().is2xxSuccessful() ? 
                Optional.of(response.getBody()) : Optional.empty();
        } catch (RestClientException e) {
            return Optional.empty();
        }
    }

    public boolean isVehicleAvailable(String vehicleId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                VEHICLE_SERVICE_URL + "/" + vehicleId + "/available", 
                Map.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Boolean.TRUE.equals(response.getBody().get("available"));
            }
            return false;
        } catch (RestClientException e) {
            return false;
        }
    }

    public boolean startRental(String vehicleId) {
        try {
            ResponseEntity<VehicleDTO> response = restTemplate.exchange(
                VEHICLE_SERVICE_URL + "/" + vehicleId + "/rent",
                HttpMethod.PUT,
                null,
                VehicleDTO.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            return false;
        }
    }

    public boolean endRental(String vehicleId) {
        try {
            ResponseEntity<VehicleDTO> response = restTemplate.exchange(
                VEHICLE_SERVICE_URL + "/" + vehicleId + "/return",
                HttpMethod.PUT,
                null,
                VehicleDTO.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            return false;
        }
    }
} 