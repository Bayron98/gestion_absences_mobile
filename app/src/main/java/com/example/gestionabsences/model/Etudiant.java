package com.example.gestionabsences.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "etudiants")
public class Etudiant {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String cne;
    public String nom;
    public String email;
    public String motDePasse;

    public Etudiant(@NonNull String cne, String nom, String email, String motDePasse) {
        this.cne = cne;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }
    // Constructeur par défaut pour Room
    public Etudiant() {
    }
}