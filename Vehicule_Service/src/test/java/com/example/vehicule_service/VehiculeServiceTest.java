package com.example.vehicule_service;

import com.example.vehicule_service.entities.EtatVehicule;
import com.example.vehicule_service.entities.Position;
import com.example.vehicule_service.entities.Vehicule;
import com.example.vehicule_service.repo.VehiculeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculeServiceTest {

    @Mock
    private VehiculeRepo vehiculeRepository;

    @InjectMocks
    private Vehicule_Service vehiculeService;

    private Vehicule testVehicule;

    @BeforeEach
    void setUp() {
        testVehicule = new Vehicule();
        testVehicule.setId("VEH001");
        testVehicule.setMarque("Tesla");
        testVehicule.setModele("Model 3");
        testVehicule.setNombrePlaces(5);
        testVehicule.setEtat(EtatVehicule.OPERATIONNEL_EN_STATION);
        testVehicule.setPosition(new Position(48, 2));
        testVehicule.setNiveauCharge(100);
        testVehicule.setKilometrage(0);
    }

    @Test
    void testListerVehicules_Success() {
        // Given
        List<Vehicule> vehicules = Arrays.asList(testVehicule);
        when(vehiculeRepository.findAll()).thenReturn(vehicules);

        // When
        List<Vehicule> result = vehiculeService.listerVehicules();

        // Then
        assertEquals(1, result.size());
        assertEquals(testVehicule.getId(), result.get(0).getId());
        verify(vehiculeRepository).findAll();
    }

    @Test
    void testObtenirVehicule_Found() {
        // Given
        String vehiculeId = "VEH001";
        when(vehiculeRepository.findById(vehiculeId)).thenReturn(Optional.of(testVehicule));

        // When
        Optional<Vehicule> result = vehiculeService.obtenirVehicule(vehiculeId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testVehicule.getId(), result.get().getId());
        verify(vehiculeRepository).findById(vehiculeId);
    }

    @Test
    void testObtenirVehicule_NotFound() {
        // Given
        String vehiculeId = "VEH999";
        when(vehiculeRepository.findById(vehiculeId)).thenReturn(Optional.empty());

        // When
        Optional<Vehicule> result = vehiculeService.obtenirVehicule(vehiculeId);

        // Then
        assertFalse(result.isPresent());
        verify(vehiculeRepository).findById(vehiculeId);
    }

    @Test
    void testCreerVehicule_Success() {
        // Given
        when(vehiculeRepository.existsById(testVehicule.getId())).thenReturn(false);
        when(vehiculeRepository.save(testVehicule)).thenReturn(testVehicule);

        // When
        Vehicule result = vehiculeService.creerVehicule(testVehicule);

        // Then
        assertEquals(testVehicule.getId(), result.getId());
        verify(vehiculeRepository).existsById(testVehicule.getId());
        verify(vehiculeRepository).save(testVehicule);
    }

    @Test
    void testCreerVehicule_AlreadyExists() {
        // Given
        when(vehiculeRepository.existsById(testVehicule.getId())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> vehiculeService.creerVehicule(testVehicule));
        
        assertEquals("Véhicule avec cet ID existe déjà", exception.getMessage());
        verify(vehiculeRepository).existsById(testVehicule.getId());
        verify(vehiculeRepository, never()).save(any());
    }

    @Test
    void testSupprimerVehicule_Success() {
        // Given
        String vehiculeId = "VEH001";
        when(vehiculeRepository.existsById(vehiculeId)).thenReturn(true);

        // When
        boolean result = vehiculeService.supprimerVehicule(vehiculeId);

        // Then
        assertTrue(result);
        verify(vehiculeRepository).existsById(vehiculeId);
        verify(vehiculeRepository).deleteById(vehiculeId);
    }

    @Test
    void testSupprimerVehicule_NotFound() {
        // Given
        String vehiculeId = "VEH999";
        when(vehiculeRepository.existsById(vehiculeId)).thenReturn(false);

        // When
        boolean result = vehiculeService.supprimerVehicule(vehiculeId);

        // Then
        assertFalse(result);
        verify(vehiculeRepository).existsById(vehiculeId);
        verify(vehiculeRepository, never()).deleteById(any());
    }

    @Test
    void testListerVehiculesDisponibles_Success() {
        // Given
        List<Vehicule> vehiculesDisponibles = Arrays.asList(testVehicule);
        when(vehiculeRepository.findByEtat(EtatVehicule.OPERATIONNEL_EN_STATION)).thenReturn(vehiculesDisponibles);

        // When
        List<Vehicule> result = vehiculeService.listerVehiculesDisponibles();

        // Then
        assertEquals(1, result.size());
        assertEquals(EtatVehicule.OPERATIONNEL_EN_STATION, result.get(0).getEtat());
        verify(vehiculeRepository).findByEtat(EtatVehicule.OPERATIONNEL_EN_STATION);
    }

    @Test
    void testIsVehiculeDisponible_Available() {
        // Given
        String vehiculeId = "VEH001";
        when(vehiculeRepository.findById(vehiculeId)).thenReturn(Optional.of(testVehicule));

        // When
        boolean result = vehiculeService.isVehiculeDisponible(vehiculeId);

        // Then
        assertTrue(result);
        verify(vehiculeRepository).findById(vehiculeId);
    }

    @Test
    void testIsVehiculeDisponible_NotAvailable() {
        // Given
        String vehiculeId = "VEH001";
        testVehicule.setEtat(EtatVehicule.OPERATIONNEL_EN_LOCATION);
        when(vehiculeRepository.findById(vehiculeId)).thenReturn(Optional.of(testVehicule));

        // When
        boolean result = vehiculeService.isVehiculeDisponible(vehiculeId);

        // Then
        assertFalse(result);
        verify(vehiculeRepository).findById(vehiculeId);
    }

    @Test
    void testIsVehiculeDisponible_NotFound() {
        // Given
        String vehiculeId = "VEH999";
        when(vehiculeRepository.findById(vehiculeId)).thenReturn(Optional.empty());

        // When
        boolean result = vehiculeService.isVehiculeDisponible(vehiculeId);

        // Then
        assertFalse(result);
        verify(vehiculeRepository).findById(vehiculeId);
    }

    @Test
    void testChangerStatutVehicule_Success() {
        // Given
        String vehiculeId = "VEH001";
        EtatVehicule nouvelEtat = EtatVehicule.OPERATIONNEL_EN_LOCATION;
        when(vehiculeRepository.findById(vehiculeId)).thenReturn(Optional.of(testVehicule));
        when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(testVehicule);

        // When
        Vehicule result = vehiculeService.changerStatutVehicule(vehiculeId, nouvelEtat);

        // Then
        assertEquals(nouvelEtat, result.getEtat());
        verify(vehiculeRepository).findById(vehiculeId);
        verify(vehiculeRepository).save(testVehicule);
    }

    @Test
    void testChangerStatutVehicule_VehiculeNotFound() {
        // Given
        String vehiculeId = "VEH999";
        EtatVehicule nouvelEtat = EtatVehicule.OPERATIONNEL_EN_LOCATION;
        when(vehiculeRepository.findById(vehiculeId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> vehiculeService.changerStatutVehicule(vehiculeId, nouvelEtat));
        
        assertEquals("Véhicule non trouvé avec l'ID: " + vehiculeId, exception.getMessage());
        verify(vehiculeRepository).findById(vehiculeId);
        verify(vehiculeRepository, never()).save(any());
    }

    @Test
    void testCommencerLocation_Success() {
        // Given
        String vehiculeId = "VEH001";
        when(vehiculeRepository.findById(vehiculeId)).thenReturn(Optional.of(testVehicule));
        when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(testVehicule);

        // When
        Vehicule result = vehiculeService.commencerLocation(vehiculeId);

        // Then
        assertEquals(EtatVehicule.OPERATIONNEL_EN_LOCATION, result.getEtat());
        verify(vehiculeRepository).findById(vehiculeId);
        verify(vehiculeRepository).save(testVehicule);
    }

    @Test
    void testTerminerLocation_Success() {
        // Given
        String vehiculeId = "VEH001";
        testVehicule.setEtat(EtatVehicule.OPERATIONNEL_EN_LOCATION);
        when(vehiculeRepository.findById(vehiculeId)).thenReturn(Optional.of(testVehicule));
        when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(testVehicule);

        // When
        Vehicule result = vehiculeService.terminerLocation(vehiculeId);

        // Then
        assertEquals(EtatVehicule.OPERATIONNEL_EN_STATION, result.getEtat());
        verify(vehiculeRepository).findById(vehiculeId);
        verify(vehiculeRepository).save(testVehicule);
    }
} 