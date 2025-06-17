package com.example.userservice.service;

import com.example.userservice.client.StationClientService;
import com.example.userservice.client.VehicleClientService;
import com.example.userservice.dto.StationDTO;
import com.example.userservice.dto.VehicleDTO;
import com.example.userservice.dto.PositionDTO;
import com.example.userservice.entities.User;
import com.example.userservice.entities.UserStatus;
import com.example.userservice.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleClientService vehicleClientService;

    @Mock
    private StationClientService stationClientService;

    @Mock
    private UserBusinessService userBusinessService;

    @InjectMocks
    private RentalService rentalService;

    private User testUser;
    private VehicleDTO testVehicle;
    private StationDTO testStation;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId("user123");
        testUser.setCardNumber("CARD123");
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setCurrentRentedVehicleId(null);

        // Setup test vehicle
        testVehicle = new VehicleDTO();
        testVehicle.setId("vehicle123");
        testVehicle.setEtat("OPERATIONNEL_EN_STATION");

        // Setup test station
        testStation = new StationDTO();
        testStation.setId("station456");
        testStation.setPosition(new PositionDTO(10, 20));
        testStation.setCapaciteGlobale(5);
        testStation.setVehiculeIds(List.of("vehicle123", "vehicle789"));
    }

    @Test
    void testStartRental_Success() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(vehicleClientService.getVehicle(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(true);
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.of(testStation));
        when(stationClientService.removeVehicleFromStation(testStation.getId(), vehicleId)).thenReturn(true);
        when(vehicleClientService.startRental(vehicleId)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = rentalService.startRental(cardNumber, vehicleId);

        // Then
        assertNotNull(result);
        assertEquals(vehicleId, testUser.getCurrentRentedVehicleId());
        
        // Verify service calls
        verify(userRepository).findByCardNumber(cardNumber);
        verify(vehicleClientService).getVehicle(vehicleId);
        verify(vehicleClientService).isVehicleAvailable(vehicleId);
        verify(stationClientService).findStationWithVehicle(vehicleId);
        verify(stationClientService).removeVehicleFromStation(testStation.getId(), vehicleId);
        verify(vehicleClientService).startRental(vehicleId);
        verify(userRepository).save(testUser);
    }

    @Test
    void testStartRental_UserNotFound() {
        // Given
        String cardNumber = "INVALID_CARD";
        String vehicleId = "vehicle123";

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> rentalService.startRental(cardNumber, vehicleId));
        
        assertEquals("Utilisateur non trouvé avec la carte: INVALID_CARD", exception.getMessage());
        
        // Verify no further service calls
        verify(userRepository).findByCardNumber(cardNumber);
        verifyNoInteractions(vehicleClientService, stationClientService);
    }

    @Test
    void testStartRental_UserNotActive() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";
        testUser.setStatus(UserStatus.SUSPENDED);

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> rentalService.startRental(cardNumber, vehicleId));
        
        assertEquals("Utilisateur non actif", exception.getMessage());
    }

    @Test
    void testStartRental_UserAlreadyHasRental() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";
        testUser.setCurrentRentedVehicleId("otherVehicle");

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> rentalService.startRental(cardNumber, vehicleId));
        
        assertEquals("Utilisateur a déjà un véhicule en location", exception.getMessage());
    }

    @Test
    void testStartRental_VehicleNotFound() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "invalid_vehicle";

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(vehicleClientService.getVehicle(vehicleId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> rentalService.startRental(cardNumber, vehicleId));
        
        assertEquals("Véhicule non trouvé: invalid_vehicle", exception.getMessage());
    }

    @Test
    void testStartRental_VehicleNotAvailable() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";
        testVehicle.setEtat("LOUE"); // Vehicle is not available

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(vehicleClientService.getVehicle(vehicleId)).thenReturn(Optional.of(testVehicle));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> rentalService.startRental(cardNumber, vehicleId));
        
        assertEquals("Véhicule non disponible pour la location", exception.getMessage());
    }

    @Test
    void testStartRental_VehicleNotAtStation() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(vehicleClientService.getVehicle(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> rentalService.startRental(cardNumber, vehicleId));
        
        assertEquals("Le véhicule doit être dans une station de recharge pour commencer la location", exception.getMessage());
    }

    @Test
    void testStartRental_StationRemovalFails() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(vehicleClientService.getVehicle(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(true);
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.of(testStation));
        when(stationClientService.removeVehicleFromStation(testStation.getId(), vehicleId)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> rentalService.startRental(cardNumber, vehicleId));
        
        assertEquals("Échec de la suppression du véhicule de la station de recharge", exception.getMessage());
    }

    @Test
    void testStartRental_VehicleRentalFailsWithRollback() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(vehicleClientService.getVehicle(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(true);
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.of(testStation));
        when(stationClientService.removeVehicleFromStation(testStation.getId(), vehicleId)).thenReturn(true);
        when(vehicleClientService.startRental(vehicleId)).thenReturn(false); // Vehicle rental fails
        when(stationClientService.addVehicleToStation(testStation.getId(), vehicleId)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> rentalService.startRental(cardNumber, vehicleId));
        
        assertEquals("Erreur lors de l'enregistrement de la location utilisateur", exception.getMessage());
        
        // Verify rollback was called (may be called multiple times due to try-catch rollback)
        verify(stationClientService, atLeast(1)).addVehicleToStation(testStation.getId(), vehicleId);
    }

    @Test
    void testEndRental_Success() {
        // Given
        String cardNumber = "CARD123";
        String stationId = "station789";
        String vehicleId = "vehicle123";
        testUser.setCurrentRentedVehicleId(vehicleId);

        StationDTO returnStation = new StationDTO();
        returnStation.setId(stationId);
        returnStation.setCapaciteGlobale(5);
        returnStation.setVehiculeIds(List.of("vehicle456")); // Has space

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(stationClientService.getStation(stationId)).thenReturn(Optional.of(returnStation));
        when(stationClientService.addVehicleToStation(stationId, vehicleId)).thenReturn(true);
        when(vehicleClientService.endRental(vehicleId)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = rentalService.endRental(cardNumber, stationId);

        // Then
        assertNotNull(result);
        assertNull(testUser.getCurrentRentedVehicleId());
        
        // Verify service calls
        verify(stationClientService).addVehicleToStation(stationId, vehicleId);
        verify(vehicleClientService).endRental(vehicleId);
        verify(userRepository).save(testUser);
    }

    @Test
    void testEndRental_UserNotFound() {
        // Given
        String cardNumber = "INVALID_CARD";
        String stationId = "station789";

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> rentalService.endRental(cardNumber, stationId));
        
        assertEquals("Utilisateur non trouvé avec la carte: INVALID_CARD", exception.getMessage());
    }

    @Test
    void testEndRental_NoActiveRental() {
        // Given
        String cardNumber = "CARD123";
        String stationId = "station789";
        testUser.setCurrentRentedVehicleId(null); // No active rental

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> rentalService.endRental(cardNumber, stationId));
        
        assertEquals("Aucun véhicule en location pour cet utilisateur", exception.getMessage());
    }

    @Test
    void testEndRental_StationNotFound() {
        // Given
        String cardNumber = "CARD123";
        String stationId = "invalid_station";
        testUser.setCurrentRentedVehicleId("vehicle123");

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(stationClientService.getStation(stationId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> rentalService.endRental(cardNumber, stationId));
        
        assertEquals("Station de recharge non trouvée: invalid_station", exception.getMessage());
    }

    @Test
    void testEndRental_StationFull() {
        // Given
        String cardNumber = "CARD123";
        String stationId = "station789";
        String vehicleId = "vehicle123";
        testUser.setCurrentRentedVehicleId(vehicleId);

        StationDTO fullStation = new StationDTO();
        fullStation.setId(stationId);
        fullStation.setCapaciteGlobale(2);
        fullStation.setVehiculeIds(List.of("vehicle456", "vehicle789")); // Full capacity

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(stationClientService.getStation(stationId)).thenReturn(Optional.of(fullStation));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> rentalService.endRental(cardNumber, stationId));
        
        assertEquals("La station de recharge n'a pas de place disponible", exception.getMessage());
    }

    @Test
    void testEndRental_VehicleReturnFailsWithRollback() {
        // Given
        String cardNumber = "CARD123";
        String stationId = "station789";
        String vehicleId = "vehicle123";
        testUser.setCurrentRentedVehicleId(vehicleId);

        StationDTO returnStation = new StationDTO();
        returnStation.setId(stationId);
        returnStation.setCapaciteGlobale(5);
        returnStation.setVehiculeIds(List.of("vehicle456"));

        when(userRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testUser));
        when(stationClientService.getStation(stationId)).thenReturn(Optional.of(returnStation));
        when(stationClientService.addVehicleToStation(stationId, vehicleId)).thenReturn(true);
        when(vehicleClientService.endRental(vehicleId)).thenReturn(false); // Vehicle return fails
        when(stationClientService.removeVehicleFromStation(stationId, vehicleId)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> rentalService.endRental(cardNumber, stationId));
        
        assertEquals("Erreur lors de la fin de location utilisateur", exception.getMessage());
        
        // Verify rollback was called (may be called multiple times due to try-catch rollback)
        verify(stationClientService, atLeast(1)).removeVehicleFromStation(stationId, vehicleId);
    }

    @Test
    void testCanUserRentVehicle_Success() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(userBusinessService.peutLouerVehicule(cardNumber)).thenReturn(true);
        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(true);
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.of(testStation));

        // When
        boolean result = rentalService.canUserRentVehicle(cardNumber, vehicleId);

        // Then
        assertTrue(result);
    }

    @Test
    void testCanUserRentVehicle_UserCannotRent() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(userBusinessService.peutLouerVehicule(cardNumber)).thenReturn(false);

        // When
        boolean result = rentalService.canUserRentVehicle(cardNumber, vehicleId);

        // Then
        assertFalse(result);
        verifyNoInteractions(vehicleClientService, stationClientService);
    }

    @Test
    void testCanUserRentVehicle_VehicleNotAvailable() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(userBusinessService.peutLouerVehicule(cardNumber)).thenReturn(true);
        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(false);

        // When
        boolean result = rentalService.canUserRentVehicle(cardNumber, vehicleId);

        // Then
        assertFalse(result);
    }

    @Test
    void testCanUserRentVehicle_VehicleNotAtStation() {
        // Given
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(userBusinessService.peutLouerVehicule(cardNumber)).thenReturn(true);
        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(true);
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.empty());

        // When
        boolean result = rentalService.canUserRentVehicle(cardNumber, vehicleId);

        // Then
        assertFalse(result);
    }

    @Test
    void testEndRental_WithoutStationId_ThrowsException() {
        // Given
        String cardNumber = "CARD123";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> rentalService.endRental(cardNumber));
        
        assertEquals("La restitution d'un véhicule nécessite maintenant de spécifier une station de recharge. Utilisez endRental(cardNumber, stationId)", exception.getMessage());
    }
} 