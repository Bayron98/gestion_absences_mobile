package com.example.gestionabsences.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.gestionabsences.database.AppDatabase;
import com.example.gestionabsences.dao.EtudiantDao;
import com.example.gestionabsences.model.Etudiant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EtudiantViewModel extends AndroidViewModel {
    private final EtudiantDao etudiantDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public EtudiantViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        etudiantDao = db.etudiantDao();
        executorService = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // Authentification étudiant par nom
    public void authenticateEtudiant(String nom, String motDePasse, AuthCallback callback) {
        executorService.execute(() -> {
            Etudiant etudiant = etudiantDao.getEtudiantByCredentials(nom, motDePasse);
            mainHandler.post(() -> callback.onResult(etudiant != null, etudiant));
        });
    }

    // Insérer un étudiant
    public void insertEtudiant(Etudiant etudiant) {
        executorService.execute(() -> etudiantDao.insert(etudiant));
    }

    // Mettre à jour un étudiant
    public void updateEtudiant(Etudiant etudiant) {
        executorService.execute(() -> etudiantDao.update(etudiant));
    }

    // Supprimer un étudiant
    public void deleteEtudiant(Etudiant etudiant) {
        executorService.execute(() -> etudiantDao.delete(etudiant));
    }

    // Récupérer un étudiant par ID
    public LiveData<Etudiant> getEtudiantById(int id) {
        return etudiantDao.getEtudiantById(id);
    }

    // Récupérer tous les étudiants
    public LiveData<List<Etudiant>> getAllEtudiants() {
        return etudiantDao.getAllEtudiants();
    }

    // Interface pour gérer le résultat de l'authentification
    public interface AuthCallback {
        void onResult(boolean success, Etudiant etudiant);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}