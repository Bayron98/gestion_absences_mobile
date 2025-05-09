package com.example.gestionabsences.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "enseignants")
public class Enseignant {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nom;
    public String motDePasse;

    public Enseignant(String nom, String motDePasse) {
        this.nom = nom;
        this.motDePasse = motDePasse;
    }
    // Constructeur par défaut pour Room
    public Enseignant() {
    }
}