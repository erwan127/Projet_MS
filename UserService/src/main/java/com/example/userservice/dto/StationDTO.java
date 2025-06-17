package com.example.userservice.dto;

import java.util.List;

public class StationDTO {
private String id;
private PositionDTO position;
private int capaciteGlobale;
private List<String> vehiculeIds;

public StationDTO() {}

public StationDTO(String id, PositionDTO position, int capaciteGlobale, List<String> vehiculeIds) {
    this.id = id;
    this.position = position;
    this.capaciteGlobale = capaciteGlobale;
    this.vehiculeIds = vehiculeIds;
}

// Getters et Setters
public String getId() { return id; }
public void setId(String id) { this.id = id; }

public PositionDTO getPosition() { return position; }
public void setPosition(PositionDTO position) { this.position = position; }

public int getCapaciteGlobale() { return capaciteGlobale; }
public void setCapaciteGlobale(int capaciteGlobale) { this.capaciteGlobale = capaciteGlobale; }

public List<String> getVehiculeIds() { return vehiculeIds; }
public void setVehiculeIds(List<String> vehiculeIds) { this.vehiculeIds = vehiculeIds; }

// Méthodes utilitaires
public int getNombreVehiculesGares() {
    return vehiculeIds != null ? vehiculeIds.size() : 0;
}

public int getNombrePlacesLibres() {
    return capaciteGlobale - getNombreVehiculesGares();
}

public boolean hasVehicle(String vehicleId) {
    return vehiculeIds != null && vehiculeIds.contains(vehicleId);
}

public boolean hasAvailableSpace() {
    return getNombrePlacesLibres() > 0;
}
} 