package com.example.gestionabsences.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.gestionabsences.database.AppDatabase;
import com.example.gestionabsences.dao.AbsenceDao;
import com.example.gestionabsences.dao.EtudiantAbsenceCount;
import com.example.gestionabsences.model.Absence;
import com.example.gestionabsences.model.AbsenceWithMatiere;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AbsenceViewModel extends AndroidViewModel {
    private final AbsenceDao absenceDao;
    private final ExecutorService executorService;

    public AbsenceViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        absenceDao = db.absenceDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    // Insérer une absence
    public void insertAbsence(Absence absence) {
        executorService.execute(() -> absenceDao.insert(absence));
    }

    // Mettre à jour une absence
    public void updateAbsence(Absence absence) {
        executorService.execute(() -> absenceDao.update(absence));
    }

    // Supprimer une absence
    public void deleteAbsence(Absence absence) {
        executorService.execute(() -> absenceDao.delete(absence));
    }

    // Récupérer les absences d’un étudiant
    public LiveData<List<Absence>> getAbsencesByEtudiant(int etudiantId) {
        return absenceDao.getAbsencesByEtudiant(etudiantId);
    }

    // Récupérer les absences pour une matière
    public LiveData<List<Absence>> getAbsencesByMatiere(int matiereId) {
        return absenceDao.getAbsencesByMatiere(matiereId);
    }

    // Récupérer une absence par ID
    public LiveData<Absence> getAbsenceById(int id) {
        return absenceDao.getAbsenceById(id);
    }

    // Compter les absences par étudiant pour une matière
    public LiveData<List<EtudiantAbsenceCount>> getEtudiantsWithAbsenceCount(int matiereId) {
        return absenceDao.getEtudiantsWithAbsenceCount(matiereId);
    }

    // Récupérer les absences d’un étudiant pour une matière
    public LiveData<List<Absence>> getAbsencesByEtudiantAndMatiere(int etudiantId, int matiereId) {
        return absenceDao.getAbsencesByEtudiantAndMatiere(etudiantId, matiereId);
    }

    // Récupérer les absences d’un étudiant avec le nom de la matière (ancienne méthode)
    public LiveData<List<Absence>> getAbsencesWithMatiereNameByEtudiant(int etudiantId) {
        return absenceDao.getAbsencesWithMatiereNameByEtudiant(etudiantId);
    }

    // Nouvelle méthode pour AbsenceWithMatiere
    public LiveData<List<AbsenceWithMatiere>> getAbsencesWithMatiereByEtudiant(int etudiantId) {
        return absenceDao.getAbsencesWithMatiereByEtudiant(etudiantId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}