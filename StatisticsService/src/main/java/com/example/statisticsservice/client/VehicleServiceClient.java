package com.example.statisticsservice.client;

import com.example.statisticsservice.dto.VehicleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "vehicule-service", url = "http://localhost:8082")
public interface VehicleServiceClient {
    
    @GetMapping("/api/vehicules")
    List<VehicleDTO> getAllVehicles();
    
    @GetMapping("/api/vehicules/{id}")
    VehicleDTO getVehicleById(@PathVariable("id") String id);
    
    @GetMapping("/api/vehicules/operational")
    List<VehicleDTO> getOperationalVehicles();
    
    @GetMapping("/api/vehicules/status/{status}")
    List<VehicleDTO> getVehiclesByStatus(@PathVariable("status") String status);
} 