package com.example.gestionabsences.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gestionabsences.model.Matiere;
import java.util.List;

@Dao
public interface MatiereDao {
    @Insert
    void insert(Matiere matiere);

    @Update
    void update(Matiere matiere);

    @Delete
    void delete(Matiere matiere);

    // Récupérer les matières d’un enseignant
    @Query("SELECT * FROM matieres WHERE enseignantId = :enseignantId")
    LiveData<List<Matiere>> getMatieresByEnseignant(int enseignantId);

    // Récupérer une matière par ID
    @Query("SELECT * FROM matieres WHERE id = :id")
    LiveData<Matiere> getMatiereById(int id);
}