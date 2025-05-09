package com.example.gestionabsences.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.gestionabsences.database.AppDatabase;
import com.example.gestionabsences.dao.MatiereDao;
import com.example.gestionabsences.model.Matiere;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatiereViewModel extends AndroidViewModel {
    private final MatiereDao matiereDao;
    private final ExecutorService executorService;

    public MatiereViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        matiereDao = db.matiereDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    // Insérer une matière
    public void insertMatiere(Matiere matiere) {
        executorService.execute(() -> matiereDao.insert(matiere));
    }

    // Mettre à jour une matière
    public void updateMatiere(Matiere matiere) {
        executorService.execute(() -> matiereDao.update(matiere));
    }

    // Supprimer une matière
    public void deleteMatiere(Matiere matiere) {
        executorService.execute(() -> matiereDao.delete(matiere));
    }

    // Récupérer les matières d’un enseignant
    public LiveData<List<Matiere>> getMatieresByEnseignant(int enseignantId) {
        return matiereDao.getMatieresByEnseignant(enseignantId);
    }

    // Récupérer une matière par ID
    public LiveData<Matiere> getMatiereById(int id) {
        return matiereDao.getMatiereById(id);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}