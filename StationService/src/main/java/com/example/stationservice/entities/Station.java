package com.example.stationservice.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stations")
public class Station {
    @Id
    private String id;

    @Embedded
    private Position position;

    private int capaciteGlobale;

    @ElementCollection
    @CollectionTable(name = "station_vehicules", joinColumns = @JoinColumn(name = "station_id"))
    @Column(name = "vehicule_id")
    private List<String> vehiculeIds = new ArrayList<>();

    public Station(String id, Position position, int capaciteGlobale) {
        this.id = id;
        this.position = position;
        this.capaciteGlobale = capaciteGlobale;
    }

    public Station() {

    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public int getCapaciteGlobale() { return capaciteGlobale; }
    public void setCapaciteGlobale(int capaciteGlobale) { this.capaciteGlobale = capaciteGlobale; }

    public List<String> getVehiculeIds() { return vehiculeIds; }
    public void setVehiculeIds(List<String> vehiculeIds) { this.vehiculeIds = vehiculeIds; }

    public int getNombreVehiculesGares() {
        return vehiculeIds.size();
    }

    public int getNombrePlacesLibres() {
        return capaciteGlobale - vehiculeIds.size();
    }
}

