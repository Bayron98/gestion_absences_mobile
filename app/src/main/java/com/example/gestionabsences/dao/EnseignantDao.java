package com.example.gestionabsences.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gestionabsences.model.Enseignant;

@Dao
public interface EnseignantDao {
    @Insert
    void insert(Enseignant enseignant);

    @Update
    void update(Enseignant enseignant);

    @Delete
    void delete(Enseignant enseignant);

    // Authentification enseignant par nom et mot de passe
    @Query("SELECT * FROM enseignants WHERE nom = :nom AND motDePasse = :motDePasse")
    Enseignant getEnseignantByCredentials(String nom, String motDePasse);

    // Récupérer un enseignant par ID
    @Query("SELECT * FROM enseignants WHERE id = :id")
    LiveData<Enseignant> getEnseignantById(String id);
}