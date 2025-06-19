package com.example.statisticsservice.client;

import com.example.statisticsservice.dto.StationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "station-service", url = "http://localhost:8081")
public interface StationServiceClient {
    
    @GetMapping("/api/stations")
    List<StationDTO> getAllStations();
    
    @GetMapping("/api/stations/{id}")
    StationDTO getStationById(@PathVariable("id") String id);
    
    @GetMapping("/api/stations/occupancy")
    List<StationDTO> getStationsWithOccupancy();
} 