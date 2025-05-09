package com.example.gestionabsences.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gestionabsences.model.Etudiant;
import java.util.List;

@Dao
public interface EtudiantDao {
    @Insert
    void insert(Etudiant etudiant);

    @Update
    void update(Etudiant etudiant);

    @Delete
    void delete(Etudiant etudiant);

    // Authentification étudiant par nom et mot de passe
    @Query("SELECT * FROM etudiants WHERE nom = :nom AND motDePasse = :motDePasse")
    Etudiant getEtudiantByCredentials(String nom, String motDePasse);

    // Récupérer un étudiant par ID
    @Query("SELECT * FROM etudiants WHERE id = :id")
    LiveData<Etudiant> getEtudiantById(int id);

    // Récupérer tous les étudiants
    @Query("SELECT * FROM etudiants")
    LiveData<List<Etudiant>> getAllEtudiants();
}