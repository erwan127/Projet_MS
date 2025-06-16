package com.example.stationservice.controller;

import com.example.stationservice.StationService;
import com.example.stationservice.entities.Position;
import com.example.stationservice.entities.Station;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationService stationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Station testStation;
    private Station stationWithVehicles;

    @BeforeEach
    void setUp() {
        // Test station with no vehicles
        testStation = new Station();
        testStation.setId("STAT001");
        testStation.setPosition(new Position(48, 2));
        testStation.setCapaciteGlobale(5);
        testStation.setVehiculeIds(new ArrayList<>());

        // Test station with vehicles
        stationWithVehicles = new Station();
        stationWithVehicles.setId("STAT002");
        stationWithVehicles.setPosition(new Position(49, 3));
        stationWithVehicles.setCapaciteGlobale(5);
        List<String> vehicleIds = new ArrayList<>();
        vehicleIds.add("VEH001");
        vehicleIds.add("VEH002");
        stationWithVehicles.setVehiculeIds(vehicleIds);
    }

    @Test
    void testListerStations() throws Exception {
        // Given
        List<Station> stations = Arrays.asList(testStation, stationWithVehicles);
        when(stationService.listerStations()).thenReturn(stations);

        // When & Then
        mockMvc.perform(get("/stations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("STAT001"))
                .andExpect(jsonPath("$[1].id").value("STAT002"));
    }

    @Test
    void testObtenirStation_Found() throws Exception {
        // Given
        when(stationService.obtenirStation("STAT001")).thenReturn(Optional.of(testStation));

        // When & Then
        mockMvc.perform(get("/stations/STAT001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("STAT001"))
                .andExpect(jsonPath("$.capaciteGlobale").value(5));
    }

    @Test
    void testObtenirStation_NotFound() throws Exception {
        // Given
        when(stationService.obtenirStation("STAT999")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/stations/STAT999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreerStation_Success() throws Exception {
        // Given
        when(stationService.creerStation(any(Station.class))).thenReturn(testStation);

        // When & Then
        mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStation)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("STAT001"));
    }

    @Test
    void testCreerStation_Conflict() throws Exception {
        // Given
        when(stationService.creerStation(any(Station.class)))
                .thenThrow(new IllegalArgumentException("Station avec cet ID existe déjà"));

        // When & Then
        mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStation)))
                .andExpect(status().isConflict());
    }

    @Test
    void testSupprimerStation_Success() throws Exception {
        // Given
        when(stationService.supprimerStation("STAT001")).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/stations/STAT001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void testSupprimerStation_NotFound() throws Exception {
        // Given
        when(stationService.supprimerStation("STAT999")).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/stations/STAT999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCheckVehicleAtStation_Present() throws Exception {
        // Given
        when(stationService.isVehicleAtStation("STAT002", "VEH001")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/stations/STAT002/vehicles/VEH001/check"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vehicleId").value("VEH001"))
                .andExpect(jsonPath("$.stationId").value("STAT002"))
                .andExpect(jsonPath("$.isPresent").value(true));
    }

    @Test
    void testCheckVehicleAtStation_NotPresent() throws Exception {
        // Given
        when(stationService.isVehicleAtStation("STAT001", "VEH999")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/stations/STAT001/vehicles/VEH999/check"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vehicleId").value("VEH999"))
                .andExpect(jsonPath("$.stationId").value("STAT001"))
                .andExpect(jsonPath("$.isPresent").value(false));
    }

    @Test
    void testFindStationWithVehicle_Found() throws Exception {
        // Given
        when(stationService.findStationWithVehicle("VEH001")).thenReturn(Optional.of(stationWithVehicles));

        // When & Then
        mockMvc.perform(get("/stations/vehicles/VEH001/station"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("STAT002"));
    }

    @Test
    void testFindStationWithVehicle_NotFound() throws Exception {
        // Given
        when(stationService.findStationWithVehicle("VEH999")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/stations/vehicles/VEH999/station"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveVehicleFromStation_Success() throws Exception {
        // Given
        when(stationService.removeVehicleFromStation("STAT002", "VEH001")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/stations/STAT002/vehicles/VEH001/remove"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.vehicleId").value("VEH001"))
                .andExpect(jsonPath("$.stationId").value("STAT002"));
    }

    @Test
    void testRemoveVehicleFromStation_Failed() throws Exception {
        // Given
        when(stationService.removeVehicleFromStation("STAT001", "VEH999")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/stations/STAT001/vehicles/VEH999/remove"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.vehicleId").value("VEH999"))
                .andExpect(jsonPath("$.stationId").value("STAT001"));
    }

    @Test
    void testAddVehicleToStation_Success() throws Exception {
        // Given
        when(stationService.addVehicleToStation("STAT001", "VEH005")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/stations/STAT001/vehicles/VEH005/add"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.vehicleId").value("VEH005"))
                .andExpect(jsonPath("$.stationId").value("STAT001"));
    }

    @Test
    void testAddVehicleToStation_Failed() throws Exception {
        // Given
        when(stationService.addVehicleToStation("STAT003", "VEH005")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/stations/STAT003/vehicles/VEH005/add"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.vehicleId").value("VEH005"))
                .andExpect(jsonPath("$.stationId").value("STAT003"));
    }

    @Test
    void testGetStationsWithVehicles() throws Exception {
        // Given
        List<Station> stationsWithVehicles = Arrays.asList(stationWithVehicles);
        when(stationService.getStationsWithVehicles()).thenReturn(stationsWithVehicles);

        // When & Then
        mockMvc.perform(get("/stations/with-vehicles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("STAT002"));
    }

    @Test
    void testGetStationsWithAvailableSpace() throws Exception {
        // Given
        List<Station> stationsWithSpace = Arrays.asList(testStation, stationWithVehicles);
        when(stationService.getStationsWithAvailableSpace()).thenReturn(stationsWithSpace);

        // When & Then
        mockMvc.perform(get("/stations/with-space"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }
} 