package com.example.statisticsservice.dto;

public class VehicleDTO {
    private String id;
    private String marque;
    private String modele;
    private int nombrePlaces;
    private float kilometrage;
    private String etat;
    private PositionDTO position;
    private int niveauCharge;

    public VehicleDTO() {}

    public VehicleDTO(String id, String marque, String modele, int nombrePlaces, 
                     float kilometrage, String etat, PositionDTO position, int niveauCharge) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.nombrePlaces = nombrePlaces;
        this.kilometrage = kilometrage;
        this.etat = etat;
        this.position = position;
        this.niveauCharge = niveauCharge;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public int getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(int nombrePlaces) { this.nombrePlaces = nombrePlaces; }

    public float getKilometrage() { return kilometrage; }
    public void setKilometrage(float kilometrage) { this.kilometrage = kilometrage; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public PositionDTO getPosition() { return position; }
    public void setPosition(PositionDTO position) { this.position = position; }

    public int getNiveauCharge() { return niveauCharge; }
    public void setNiveauCharge(int niveauCharge) { this.niveauCharge = niveauCharge; }
} 