package com.example.gestionabsences.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.gestionabsences.database.AppDatabase;
import com.example.gestionabsences.dao.EnseignantDao;
import com.example.gestionabsences.model.Enseignant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EnseignantViewModel extends AndroidViewModel {
    private final EnseignantDao enseignantDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public EnseignantViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        enseignantDao = db.enseignantDao();
        executorService = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // Authentification enseignant par nom
    public void authenticateEnseignant(String nom, String motDePasse, AuthCallback callback) {
        executorService.execute(() -> {
            Enseignant enseignant = enseignantDao.getEnseignantByCredentials(nom, motDePasse);
            mainHandler.post(() -> callback.onResult(enseignant != null, enseignant));
        });
    }

    // Insérer un enseignant
    public void insertEnseignant(Enseignant enseignant) {
        executorService.execute(() -> enseignantDao.insert(enseignant));
    }

    // Mettre à jour un enseignant
    public void updateEnseignant(Enseignant enseignant) {
        executorService.execute(() -> enseignantDao.update(enseignant));
    }

    // Supprimer un enseignant
    public void deleteEnseignant(Enseignant enseignant) {
        executorService.execute(() -> enseignantDao.delete(enseignant));
    }

    // Récupérer un enseignant par ID
    public LiveData<Enseignant> getEnseignantById(String id) {
        return enseignantDao.getEnseignantById(id);
    }

    // Interface pour gérer le résultat de l'authentification
    public interface AuthCallback {
        void onResult(boolean success, Enseignant enseignant);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}