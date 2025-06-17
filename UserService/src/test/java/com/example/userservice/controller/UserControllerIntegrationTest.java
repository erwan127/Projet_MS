package com.example.userservice.controller;

import com.example.userservice.client.StationClientService;
import com.example.userservice.client.VehicleClientService;
import com.example.userservice.dto.StationDTO;
import com.example.userservice.dto.VehicleDTO;
import com.example.userservice.dto.PositionDTO;
import com.example.userservice.entities.User;
import com.example.userservice.entities.UserStatus;
import com.example.userservice.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class UserControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private VehicleClientService vehicleClientService;

    @MockBean
    private StationClientService stationClientService;

    private User testUser;
    private VehicleDTO testVehicle;
    private StationDTO testStation;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
        
        // Setup test user
        testUser = new User();
        testUser.setId("user123");
        testUser.setCardNumber("CARD123");
        testUser.setPinCode("1234");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@test.com");
        testUser.setPhoneNumber("123456789");
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setCurrentRentedVehicleId(null);
        userRepository.save(testUser);

        // Setup test vehicle
        testVehicle = new VehicleDTO();
        testVehicle.setId("vehicle123");
        testVehicle.setEtat("OPERATIONNEL_EN_STATION"); // Use setEtat instead of setDisponible

        // Setup test station
        testStation = new StationDTO();
        testStation.setId("station456");
        testStation.setPosition(new PositionDTO(10, 20));
        testStation.setCapaciteGlobale(5);
        testStation.setVehiculeIds(List.of("vehicle123", "vehicle789"));
    }

    @Test
    void testCompleteRentalWorkflow_Success() throws Exception {
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";
        String returnStationId = "station789";

        // Mock dependencies for rental start
        when(vehicleClientService.getVehicle(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(true);
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.of(testStation));
        when(stationClientService.removeVehicleFromStation(testStation.getId(), vehicleId)).thenReturn(true);
        when(vehicleClientService.startRental(vehicleId)).thenReturn(true);

        // Step 1: Start rental
        mockMvc.perform(post("/users/card/{cardNumber}/start-rental", cardNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("vehicleId", vehicleId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Location démarrée avec succès depuis la station de recharge"))
                .andExpect(jsonPath("$.user.currentRentedVehicleId").value(vehicleId));

        // Mock dependencies for rental end
        StationDTO returnStation = new StationDTO();
        returnStation.setId(returnStationId);
        returnStation.setCapaciteGlobale(5);
        returnStation.setVehiculeIds(List.of("vehicle456")); // Has space

        when(stationClientService.getStation(returnStationId)).thenReturn(Optional.of(returnStation));
        when(stationClientService.addVehicleToStation(returnStationId, vehicleId)).thenReturn(true);
        when(vehicleClientService.endRental(vehicleId)).thenReturn(true);

        // Step 2: End rental
        mockMvc.perform(post("/users/card/{cardNumber}/end-rental", cardNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("stationId", returnStationId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Location terminée avec succès à la station de recharge"))
                .andExpect(jsonPath("$.user.currentRentedVehicleId").isEmpty());

        // Verify all service interactions
        verify(stationClientService).removeVehicleFromStation(testStation.getId(), vehicleId);
        verify(vehicleClientService).startRental(vehicleId);
        verify(stationClientService).addVehicleToStation(returnStationId, vehicleId);
        verify(vehicleClientService).endRental(vehicleId);
    }

    @Test
    void testStartRental_UserNotFound() throws Exception {
        String cardNumber = "INVALID_CARD";
        String vehicleId = "vehicle123";

        mockMvc.perform(post("/users/card/{cardNumber}/start-rental", cardNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("vehicleId", vehicleId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Utilisateur non trouvé avec la carte: INVALID_CARD"));
    }

    @Test
    void testStartRental_VehicleNotAtStation() throws Exception {
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(vehicleClientService.getVehicle(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(true);
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/card/{cardNumber}/start-rental", cardNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("vehicleId", vehicleId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("STATE_ERROR"))
                .andExpect(jsonPath("$.message").value("Le véhicule doit être dans une station de recharge pour commencer la location"));
    }

    @Test
    void testStartRental_UserAlreadyHasActiveRental() throws Exception {
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        // Set user to have active rental
        testUser.setCurrentRentedVehicleId("otherVehicle");
        userRepository.save(testUser);

        when(vehicleClientService.getVehicle(vehicleId)).thenReturn(Optional.of(testVehicle));

        mockMvc.perform(post("/users/card/{cardNumber}/start-rental", cardNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("vehicleId", vehicleId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("STATE_ERROR"))
                .andExpect(jsonPath("$.message").value("Utilisateur a déjà un véhicule en location"));
    }

    @Test
    void testEndRental_MissingStationId() throws Exception {
        String cardNumber = "CARD123";

        // Set user to have active rental
        testUser.setCurrentRentedVehicleId("vehicle123");
        userRepository.save(testUser);

        mockMvc.perform(post("/users/card/{cardNumber}/end-rental", cardNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("MISSING_STATION_ID"))
                .andExpect(jsonPath("$.message").value("L'ID de la station de recharge est requis pour terminer la location"));
    }

    @Test
    void testEndRental_StationFull() throws Exception {
        String cardNumber = "CARD123";
        String stationId = "station789";
        String vehicleId = "vehicle123";

        // Set user to have active rental
        testUser.setCurrentRentedVehicleId(vehicleId);
        userRepository.save(testUser);

        // Mock full station
        StationDTO fullStation = new StationDTO();
        fullStation.setId(stationId);
        fullStation.setCapaciteGlobale(2);
        fullStation.setVehiculeIds(List.of("vehicle456", "vehicle789")); // Full capacity

        when(stationClientService.getStation(stationId)).thenReturn(Optional.of(fullStation));

        mockMvc.perform(post("/users/card/{cardNumber}/end-rental", cardNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("stationId", stationId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("STATE_ERROR"))
                .andExpect(jsonPath("$.message").value("La station de recharge n'a pas de place disponible"));
    }

    @Test
    void testCanUserRentVehicle_Success() throws Exception {
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(true);
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.of(testStation));

        mockMvc.perform(get("/users/card/{cardNumber}/can-rent/{vehicleId}", cardNumber, vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canRent").value(true))
                .andExpect(jsonPath("$.message").value("Utilisateur peut louer ce véhicule"));
    }

    @Test
    void testCanUserRentVehicle_VehicleNotAtStation() throws Exception {
        String cardNumber = "CARD123";
        String vehicleId = "vehicle123";

        when(vehicleClientService.isVehicleAvailable(vehicleId)).thenReturn(true);
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/card/{cardNumber}/can-rent/{vehicleId}", cardNumber, vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canRent").value(false))
                .andExpect(jsonPath("$.message").value("Utilisateur ne peut pas louer ce véhicule"));
    }

    @Test
    void testGetStationsWithVehicles() throws Exception {
        List<StationDTO> stationsWithVehicles = List.of(testStation);
        when(stationClientService.getStationsWithVehicles()).thenReturn(stationsWithVehicles);

        mockMvc.perform(get("/users/stations/with-vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("station456"))
                .andExpect(jsonPath("$[0].capaciteGlobale").value(5));
    }

    @Test
    void testGetStationsWithAvailableSpace() throws Exception {
        List<StationDTO> stationsWithSpace = List.of(testStation);
        when(stationClientService.getStationsWithAvailableSpace()).thenReturn(stationsWithSpace);

        mockMvc.perform(get("/users/stations/with-space"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("station456"));
    }

    @Test
    void testGetVehicleStation() throws Exception {
        String vehicleId = "vehicle123";
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.of(testStation));

        mockMvc.perform(get("/users/vehicles/{vehicleId}/station", vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("station456"))
                .andExpect(jsonPath("$.capaciteGlobale").value(5));
    }

    @Test
    void testGetVehicleStation_NotFound() throws Exception {
        String vehicleId = "vehicle123";
        when(stationClientService.findStationWithVehicle(vehicleId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/vehicles/{vehicleId}/station", vehicleId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCanReturnVehicleToStation_Success() throws Exception {
        String stationId = "station456";
        StationDTO stationWithSpace = new StationDTO();
        stationWithSpace.setId(stationId);
        stationWithSpace.setCapaciteGlobale(5);
        stationWithSpace.setVehiculeIds(List.of("vehicle789"));

        when(stationClientService.getStation(stationId)).thenReturn(Optional.of(stationWithSpace));

        mockMvc.perform(get("/users/stations/{stationId}/can-return", stationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canReturn").value(true))
                .andExpect(jsonPath("$.stationId").value(stationId))
                .andExpect(jsonPath("$.message").value("Véhicule peut être rendu à cette station"));
    }

    @Test
    void testCanReturnVehicleToStation_StationFull() throws Exception {
        String stationId = "station456";
        StationDTO fullStation = new StationDTO();
        fullStation.setId(stationId);
        fullStation.setCapaciteGlobale(2);
        fullStation.setVehiculeIds(List.of("vehicle789", "vehicle999"));

        when(stationClientService.getStation(stationId)).thenReturn(Optional.of(fullStation));

        mockMvc.perform(get("/users/stations/{stationId}/can-return", stationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canReturn").value(false))
                .andExpect(jsonPath("$.message").value("Station pleine ou inexistante"));
    }

    @Test
    void testAuthenticateUser_Success() throws Exception {
        Map<String, String> credentials = Map.of(
            "cardNumber", "CARD123",
            "pinCode", "1234"
        );

        mockMvc.perform(post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.message").value("Authentification réussie"));
    }

    @Test
    void testAuthenticateUser_Failed() throws Exception {
        Map<String, String> credentials = Map.of(
            "cardNumber", "CARD123",
            "pinCode", "wrong"
        );

        mockMvc.perform(post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.message").value("Authentification échouée"));
    }
} 