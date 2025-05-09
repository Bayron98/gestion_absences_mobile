package com.example.gestionabsences.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "matieres",
        foreignKeys = @ForeignKey(entity = Enseignant.class,
                parentColumns = "id",
                childColumns = "enseignantId",
                onDelete = ForeignKey.CASCADE))
public class Matiere {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String nom;
    @NonNull
    public int enseignantId;

    public Matiere(@NonNull String nom, @NonNull int enseignantId) {
        this.nom = nom;
        this.enseignantId = enseignantId;
    }
    // Constructeur par défaut pour Room
    public Matiere() {
    }
}