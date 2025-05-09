package com.example.gestionabsences.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gestionabsences.model.Absence;
import java.util.List;

@Dao
public interface AbsenceDao {
    @Insert
    void insert(Absence absence);

    @Update
    void update(Absence absence);

    @Delete
    void delete(Absence absence);

    // Récupérer les absences d’un étudiant (toutes matières)
    @Query("SELECT * FROM absences WHERE etudiantId = :etudiantId")
    LiveData<List<Absence>> getAbsencesByEtudiant(int etudiantId);

    // Récupérer les absences pour une matière
    @Query("SELECT * FROM absences WHERE matiereId = :matiereId")
    LiveData<List<Absence>> getAbsencesByMatiere(int matiereId);

    // Récupérer une absence par ID
    @Query("SELECT * FROM absences WHERE id = :id")
    LiveData<Absence> getAbsenceById(int id);

    // Compter les absences par étudiant pour une matière
    @Query("SELECT e.id, e.nom, e.cne, COUNT(a.id) as nombreAbsences " +
            "FROM etudiants e " +
            "LEFT JOIN absences a ON e.id = a.etudiantId AND a.matiereId = :matiereId " +
            "GROUP BY e.id, e.nom, e.cne")
    LiveData<List<EtudiantAbsenceCount>> getEtudiantsWithAbsenceCount(int matiereId);

    // Récupérer les absences d’un étudiant pour une matière spécifique
    @Query("SELECT * FROM absences WHERE etudiantId = :etudiantId AND matiereId = :matiereId")
    LiveData<List<Absence>> getAbsencesByEtudiantAndMatiere(int etudiantId, int matiereId);
}