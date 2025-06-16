package com.example.vehicule_service.controller;

import com.example.vehicule_service.Vehicule_Service;
import com.example.vehicule_service.entities.EtatVehicule;
import com.example.vehicule_service.entities.Position;
import com.example.vehicule_service.entities.Vehicule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiculeController.class)
class VehiculeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Vehicule_Service vehiculeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Vehicule testVehicule;
    private Vehicule rentedVehicule;

    @BeforeEach
    void setUp() {
        // Available vehicle
        testVehicule = new Vehicule();
        testVehicule.setId("VEH001");
        testVehicule.setMarque("Tesla");
        testVehicule.setModele("Model 3");
        testVehicule.setNombrePlaces(5);
        testVehicule.setEtat(EtatVehicule.OPERATIONNEL_EN_STATION);
        testVehicule.setPosition(new Position(48, 2));
        testVehicule.setNiveauCharge(100);
        testVehicule.setKilometrage(0);

        // Rented vehicle
        rentedVehicule = new Vehicule();
        rentedVehicule.setId("VEH002");
        rentedVehicule.setMarque("Renault");
        rentedVehicule.setModele("Zoe");
        rentedVehicule.setNombrePlaces(4);
        rentedVehicule.setEtat(EtatVehicule.OPERATIONNEL_EN_LOCATION);
        rentedVehicule.setPosition(new Position(49, 3));
        rentedVehicule.setNiveauCharge(80);
        rentedVehicule.setKilometrage(1500);
    }

    @Test
    void testListerVehicules() throws Exception {
        // Given
        List<Vehicule> vehicules = Arrays.asList(testVehicule, rentedVehicule);
        when(vehiculeService.listerVehicules()).thenReturn(vehicules);

        // When & Then
        mockMvc.perform(get("/vehicules"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("VEH001"))
                .andExpect(jsonPath("$[1].id").value("VEH002"));
    }

    @Test
    void testListerVehiculesDisponibles() throws Exception {
        // Given
        List<Vehicule> vehiculesDisponibles = Arrays.asList(testVehicule);
        when(vehiculeService.listerVehiculesDisponibles()).thenReturn(vehiculesDisponibles);

        // When & Then
        mockMvc.perform(get("/vehicules/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("VEH001"))
                .andExpect(jsonPath("$[0].etat").value("OPERATIONNEL_EN_STATION"));
    }

    @Test
    void testObtenirVehicule_Found() throws Exception {
        // Given
        when(vehiculeService.obtenirVehicule("VEH001")).thenReturn(Optional.of(testVehicule));

        // When & Then
        mockMvc.perform(get("/vehicules/VEH001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("VEH001"))
                .andExpect(jsonPath("$.marque").value("Tesla"))
                .andExpect(jsonPath("$.modele").value("Model 3"));
    }

    @Test
    void testObtenirVehicule_NotFound() throws Exception {
        // Given
        when(vehiculeService.obtenirVehicule("VEH999")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/vehicules/VEH999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testVerifierDisponibilite_Available() throws Exception {
        // Given
        when(vehiculeService.isVehiculeDisponible("VEH001")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/vehicules/VEH001/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testVerifierDisponibilite_NotAvailable() throws Exception {
        // Given
        when(vehiculeService.isVehiculeDisponible("VEH002")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/vehicules/VEH002/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void testCreerVehicule_Success() throws Exception {
        // Given
        when(vehiculeService.creerVehicule(any(Vehicule.class))).thenReturn(testVehicule);

        // When & Then
        mockMvc.perform(post("/vehicules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVehicule)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("VEH001"));
    }

    @Test
    void testCreerVehicule_Conflict() throws Exception {
        // Given
        when(vehiculeService.creerVehicule(any(Vehicule.class)))
                .thenThrow(new IllegalArgumentException("Véhicule avec cet ID existe déjà"));

        // When & Then
        mockMvc.perform(post("/vehicules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVehicule)))
                .andExpect(status().isConflict());
    }

    @Test
    void testChangerStatutVehicule_Success() throws Exception {
        // Given
        testVehicule.setEtat(EtatVehicule.OPERATIONNEL_EN_LOCATION);
        when(vehiculeService.changerStatutVehicule("VEH001", EtatVehicule.OPERATIONNEL_EN_LOCATION))
                .thenReturn(testVehicule);

        Map<String, String> request = new HashMap<>();
        request.put("status", "OPERATIONNEL_EN_LOCATION");

        // When & Then
        mockMvc.perform(put("/vehicules/VEH001/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.etat").value("OPERATIONNEL_EN_LOCATION"));
    }

    @Test
    void testChangerStatutVehicule_InvalidStatus() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("status", "INVALID_STATUS");

        // When & Then
        mockMvc.perform(put("/vehicules/VEH001/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChangerStatutVehicule_VehiculeNotFound() throws Exception {
        // Given
        when(vehiculeService.changerStatutVehicule(anyString(), any(EtatVehicule.class)))
                .thenThrow(new IllegalArgumentException("Véhicule non trouvé"));

        Map<String, String> request = new HashMap<>();
        request.put("status", "OPERATIONNEL_EN_LOCATION");

        // When & Then
        mockMvc.perform(put("/vehicules/VEH999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCommencerLocation_Success() throws Exception {
        // Given
        testVehicule.setEtat(EtatVehicule.OPERATIONNEL_EN_LOCATION);
        when(vehiculeService.commencerLocation("VEH001")).thenReturn(testVehicule);

        // When & Then
        mockMvc.perform(put("/vehicules/VEH001/rent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.etat").value("OPERATIONNEL_EN_LOCATION"));
    }

    @Test
    void testCommencerLocation_VehiculeNotFound() throws Exception {
        // Given
        when(vehiculeService.commencerLocation("VEH999"))
                .thenThrow(new IllegalArgumentException("Véhicule non trouvé"));

        // When & Then
        mockMvc.perform(put("/vehicules/VEH999/rent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTerminerLocation_Success() throws Exception {
        // Given
        rentedVehicule.setEtat(EtatVehicule.OPERATIONNEL_EN_STATION);
        when(vehiculeService.terminerLocation("VEH002")).thenReturn(rentedVehicule);

        // When & Then
        mockMvc.perform(put("/vehicules/VEH002/return"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.etat").value("OPERATIONNEL_EN_STATION"));
    }

    @Test
    void testTerminerLocation_VehiculeNotFound() throws Exception {
        // Given
        when(vehiculeService.terminerLocation("VEH999"))
                .thenThrow(new IllegalArgumentException("Véhicule non trouvé"));

        // When & Then
        mockMvc.perform(put("/vehicules/VEH999/return"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSupprimerVehicule_Success() throws Exception {
        // Given
        when(vehiculeService.supprimerVehicule("VEH001")).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/vehicules/VEH001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void testSupprimerVehicule_NotFound() throws Exception {
        // Given
        when(vehiculeService.supprimerVehicule("VEH999")).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/vehicules/VEH999"))
                .andExpect(status().isNotFound());
    }
} 