package com.example.gestionabsences.model;

public class AbsenceWithMatiere {
    public int id;
    public int etudiantId;
    public int matiereId;
    public String date;
    public String seance;
    public String justificatif;
    public String penalite;
    public String matiereNom;

    public AbsenceWithMatiere(int id, int etudiantId, int matiereId, String date, String seance, String justificatif, String penalite, String matiereNom) {
        this.id = id;
        this.etudiantId = etudiantId;
        this.matiereId = matiereId;
        this.date = date;
        this.seance = seance;
        this.justificatif = justificatif;
        this.penalite = penalite;
        this.matiereNom = matiereNom;
    }
}