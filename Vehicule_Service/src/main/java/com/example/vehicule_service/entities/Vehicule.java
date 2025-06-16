package com.example.vehicule_service.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicules")
public class Vehicule {
    @Id
    private String id;

    private String marque;
    private String modele;
    private int nombrePlaces;
    private float kilometrage;

    @Enumerated(EnumType.STRING)
    private EtatVehicule etat;

    @Embedded
    private Position position;

    private int niveauCharge;

    // Constructeurs
    public Vehicule() {}

    public Vehicule(String id, String marque, String modele, int nombrePlaces) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.nombrePlaces = nombrePlaces;
        this.etat = EtatVehicule.OPERATIONNEL_EN_STATION;
        this.niveauCharge = 100;
        this.kilometrage = 0;
    }

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

    public EtatVehicule getEtat() { return etat; }
    public void setEtat(EtatVehicule etat) { this.etat = etat; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public int getNiveauCharge() { return niveauCharge; }
    public void setNiveauCharge(int niveauCharge) { this.niveauCharge = niveauCharge; }
}