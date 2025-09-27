package com.example.gestionabsences.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "absences",
        foreignKeys = {
                @ForeignKey(entity = Etudiant.class,
                        parentColumns = "id",
                        childColumns = "etudiantId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Matiere.class,
                        parentColumns = "id",
                        childColumns = "matiereId",
                        onDelete = ForeignKey.CASCADE)
        })
public class Absence {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int etudiantId;
    public int matiereId;
    public String date;
    public String seance;
    public String justificatif;
    public String penalite;



    // Constructeur
    public Absence(int etudiantId, int matiereId, String date, String seance, String justificatif, String penalite) {
        this.etudiantId = etudiantId;
        this.matiereId = matiereId;
        this.date = date;
        this.seance = seance;
        this.justificatif = justificatif;
        this.penalite = penalite;
    }

    // Constructeur par défaut pour Room
    public Absence() {
    }
}