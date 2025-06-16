package com.example.stationservice;

import com.example.stationservice.entities.Position;
import com.example.stationservice.entities.Station;
import com.example.stationservice.repo.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    private Station testStation;
    private Station stationWithVehicles;
    private Station fullStation;

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

        // Full station
        fullStation = new Station();
        fullStation.setId("STAT003");
        fullStation.setPosition(new Position(50, 4));
        fullStation.setCapaciteGlobale(2);
        List<String> fullVehicleIds = new ArrayList<>();
        fullVehicleIds.add("VEH003");
        fullVehicleIds.add("VEH004");
        fullStation.setVehiculeIds(fullVehicleIds);
    }

    @Test
    void testListerStations_Success() {
        // Given
        List<Station> stations = Arrays.asList(testStation, stationWithVehicles);
        when(stationRepository.findAll()).thenReturn(stations);

        // When
        List<Station> result = stationService.listerStations();

        // Then
        assertEquals(2, result.size());
        assertEquals(testStation.getId(), result.get(0).getId());
        assertEquals(stationWithVehicles.getId(), result.get(1).getId());
        verify(stationRepository).findAll();
    }

    @Test
    void testObtenirStation_Found() {
        // Given
        String stationId = "STAT001";
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(testStation));

        // When
        Optional<Station> result = stationService.obtenirStation(stationId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testStation.getId(), result.get().getId());
        verify(stationRepository).findById(stationId);
    }

    @Test
    void testObtenirStation_NotFound() {
        // Given
        String stationId = "STAT999";
        when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

        // When
        Optional<Station> result = stationService.obtenirStation(stationId);

        // Then
        assertFalse(result.isPresent());
        verify(stationRepository).findById(stationId);
    }

    @Test
    void testCreerStation_Success() {
        // Given
        when(stationRepository.existsById(testStation.getId())).thenReturn(false);
        when(stationRepository.save(testStation)).thenReturn(testStation);

        // When
        Station result = stationService.creerStation(testStation);

        // Then
        assertEquals(testStation.getId(), result.getId());
        verify(stationRepository).existsById(testStation.getId());
        verify(stationRepository).save(testStation);
    }

    @Test
    void testCreerStation_AlreadyExists() {
        // Given
        when(stationRepository.existsById(testStation.getId())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> stationService.creerStation(testStation));
        
        assertEquals("Station avec cet ID existe déjà", exception.getMessage());
        verify(stationRepository).existsById(testStation.getId());
        verify(stationRepository, never()).save(any());
    }

    @Test
    void testSupprimerStation_Success() {
        // Given
        String stationId = "STAT001";
        when(stationRepository.existsById(stationId)).thenReturn(true);

        // When
        boolean result = stationService.supprimerStation(stationId);

        // Then
        assertTrue(result);
        verify(stationRepository).existsById(stationId);
        verify(stationRepository).deleteById(stationId);
    }

    @Test
    void testSupprimerStation_NotFound() {
        // Given
        String stationId = "STAT999";
        when(stationRepository.existsById(stationId)).thenReturn(false);

        // When
        boolean result = stationService.supprimerStation(stationId);

        // Then
        assertFalse(result);
        verify(stationRepository).existsById(stationId);
        verify(stationRepository, never()).deleteById(any());
    }

    @Test
    void testIsVehicleAtStation_VehiclePresent() {
        // Given
        String stationId = "STAT002";
        String vehicleId = "VEH001";
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(stationWithVehicles));

        // When
        boolean result = stationService.isVehicleAtStation(stationId, vehicleId);

        // Then
        assertTrue(result);
        verify(stationRepository).findById(stationId);
    }

    @Test
    void testIsVehicleAtStation_VehicleNotPresent() {
        // Given
        String stationId = "STAT001";
        String vehicleId = "VEH999";
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(testStation));

        // When
        boolean result = stationService.isVehicleAtStation(stationId, vehicleId);

        // Then
        assertFalse(result);
        verify(stationRepository).findById(stationId);
    }

    @Test
    void testIsVehicleAtStation_StationNotFound() {
        // Given
        String stationId = "STAT999";
        String vehicleId = "VEH001";
        when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

        // When
        boolean result = stationService.isVehicleAtStation(stationId, vehicleId);

        // Then
        assertFalse(result);
        verify(stationRepository).findById(stationId);
    }

    @Test
    void testFindStationWithVehicle_Found() {
        // Given
        String vehicleId = "VEH001";
        List<Station> allStations = Arrays.asList(testStation, stationWithVehicles, fullStation);
        when(stationRepository.findAll()).thenReturn(allStations);

        // When
        Optional<Station> result = stationService.findStationWithVehicle(vehicleId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(stationWithVehicles.getId(), result.get().getId());
        verify(stationRepository).findAll();
    }

    @Test
    void testFindStationWithVehicle_NotFound() {
        // Given
        String vehicleId = "VEH999";
        List<Station> allStations = Arrays.asList(testStation, stationWithVehicles, fullStation);
        when(stationRepository.findAll()).thenReturn(allStations);

        // When
        Optional<Station> result = stationService.findStationWithVehicle(vehicleId);

        // Then
        assertFalse(result.isPresent());
        verify(stationRepository).findAll();
    }

    @Test
    void testRemoveVehicleFromStation_Success() {
        // Given
        String stationId = "STAT002";
        String vehicleId = "VEH001";
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(stationWithVehicles));
        when(stationRepository.save(any(Station.class))).thenReturn(stationWithVehicles);

        // When
        boolean result = stationService.removeVehicleFromStation(stationId, vehicleId);

        // Then
        assertTrue(result);
        assertFalse(stationWithVehicles.getVehiculeIds().contains(vehicleId));
        verify(stationRepository).findById(stationId);
        verify(stationRepository).save(stationWithVehicles);
    }

    @Test
    void testRemoveVehicleFromStation_VehicleNotPresent() {
        // Given
        String stationId = "STAT001";
        String vehicleId = "VEH999";
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(testStation));

        // When
        boolean result = stationService.removeVehicleFromStation(stationId, vehicleId);

        // Then
        assertFalse(result);
        verify(stationRepository).findById(stationId);
        verify(stationRepository, never()).save(any());
    }

    @Test
    void testRemoveVehicleFromStation_StationNotFound() {
        // Given
        String stationId = "STAT999";
        String vehicleId = "VEH001";
        when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

        // When
        boolean result = stationService.removeVehicleFromStation(stationId, vehicleId);

        // Then
        assertFalse(result);
        verify(stationRepository).findById(stationId);
        verify(stationRepository, never()).save(any());
    }

    @Test
    void testAddVehicleToStation_Success() {
        // Given
        String stationId = "STAT001";
        String vehicleId = "VEH005";
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(testStation));
        when(stationRepository.save(any(Station.class))).thenReturn(testStation);

        // When
        boolean result = stationService.addVehicleToStation(stationId, vehicleId);

        // Then
        assertTrue(result);
        assertTrue(testStation.getVehiculeIds().contains(vehicleId));
        verify(stationRepository).findById(stationId);
        verify(stationRepository).save(testStation);
    }

    @Test
    void testAddVehicleToStation_StationFull() {
        // Given
        String stationId = "STAT003";
        String vehicleId = "VEH005";
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(fullStation));

        // When
        boolean result = stationService.addVehicleToStation(stationId, vehicleId);

        // Then
        assertFalse(result);
        verify(stationRepository).findById(stationId);
        verify(stationRepository, never()).save(any());
    }

    @Test
    void testAddVehicleToStation_VehicleAlreadyPresent() {
        // Given
        String stationId = "STAT002";
        String vehicleId = "VEH001"; // Already present
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(stationWithVehicles));

        // When
        boolean result = stationService.addVehicleToStation(stationId, vehicleId);

        // Then
        assertFalse(result);
        verify(stationRepository).findById(stationId);
        verify(stationRepository, never()).save(any());
    }

    @Test
    void testAddVehicleToStation_StationNotFound() {
        // Given
        String stationId = "STAT999";
        String vehicleId = "VEH005";
        when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

        // When
        boolean result = stationService.addVehicleToStation(stationId, vehicleId);

        // Then
        assertFalse(result);
        verify(stationRepository).findById(stationId);
        verify(stationRepository, never()).save(any());
    }

    @Test
    void testGetStationsWithAvailableSpace() {
        // Given
        List<Station> allStations = Arrays.asList(testStation, stationWithVehicles, fullStation);
        when(stationRepository.findAll()).thenReturn(allStations);

        // When
        List<Station> result = stationService.getStationsWithAvailableSpace();

        // Then
        assertEquals(2, result.size()); // testStation and stationWithVehicles have space
        assertTrue(result.contains(testStation));
        assertTrue(result.contains(stationWithVehicles));
        assertFalse(result.contains(fullStation));
        verify(stationRepository).findAll();
    }

    @Test
    void testGetStationsWithVehicles() {
        // Given
        List<Station> allStations = Arrays.asList(testStation, stationWithVehicles, fullStation);
        when(stationRepository.findAll()).thenReturn(allStations);

        // When
        List<Station> result = stationService.getStationsWithVehicles();

        // Then
        assertEquals(2, result.size()); // stationWithVehicles and fullStation have vehicles
        assertFalse(result.contains(testStation));
        assertTrue(result.contains(stationWithVehicles));
        assertTrue(result.contains(fullStation));
        verify(stationRepository).findAll();
    }
} 