package com.example.userservice.dto;

public class VehicleDTO {
    private String id;
    private String marque;
    private String modele;
    private int nombrePlaces;
    private float kilometrage;
    private String etat;
    private int niveauCharge;

    public VehicleDTO() {}

    // Getters et Setters
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

    public int getNiveauCharge() { return niveauCharge; }
    public void setNiveauCharge(int niveauCharge) { this.niveauCharge = niveauCharge; }

    public boolean isDisponible() {
        return "OPERATIONNEL_EN_STATION".equals(etat);
    }
} 