package com.example.gestionabsences.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.gestionabsences.model.Etudiant;
import com.example.gestionabsences.model.Enseignant;
import com.example.gestionabsences.model.Matiere;
import com.example.gestionabsences.model.Absence;
import com.example.gestionabsences.dao.EtudiantDao;
import com.example.gestionabsences.dao.EnseignantDao;
import com.example.gestionabsences.dao.MatiereDao;
import com.example.gestionabsences.dao.AbsenceDao;

@Database(entities = {Etudiant.class, Enseignant.class, Matiere.class, Absence.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EtudiantDao etudiantDao();
    public abstract EnseignantDao enseignantDao();
    public abstract MatiereDao matiereDao();
    public abstract AbsenceDao absenceDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "absence_db")
                            .fallbackToDestructiveMigration()
                            .build();
                    // Insérer les données de test
                    insertTestData(context);
                }
            }
        }
        return INSTANCE;
    }

    private static void insertTestData(Context context) {
        new Thread(() -> {
            AppDatabase db = getDatabase(context);
            EnseignantDao enseignantDao = db.enseignantDao();
            MatiereDao matiereDao = db.matiereDao();
            EtudiantDao etudiantDao = db.etudiantDao();
            AbsenceDao absenceDao = db.absenceDao();

            // Vérifier si la table matieres est vide pour éviter les doublons
            if (matiereDao.getMatieresByEnseignant(1).getValue() == null || matiereDao.getMatieresByEnseignant(1).getValue().isEmpty()) {
                // Insérer des enseignants (id=1, 2)
                Enseignant enseignant1 = new Enseignant();
                enseignant1.nom = "Dr. Dupont";
                enseignant1.motDePasse = "pass123";
                enseignantDao.insert(enseignant1); // id=1

                Enseignant enseignant2 = new Enseignant();
                enseignant2.nom = "Prof. Martin";
                enseignant2.motDePasse = "pass456";
                enseignantDao.insert(enseignant2); // id=2

                // Insérer des matières (id=1, 2, 3)
                Matiere matiere1 = new Matiere();
                matiere1.nom = "Mathématiques";
                matiere1.enseignantId = 1;
                matiereDao.insert(matiere1); // id=1

                Matiere matiere2 = new Matiere();
                matiere2.nom = "Physique";
                matiere2.enseignantId = 1;
                matiereDao.insert(matiere2); // id=2

                Matiere matiere3 = new Matiere();
                matiere3.nom = "Informatique";
                matiere3.enseignantId = 2;
                matiereDao.insert(matiere3); // id=3

                // Insérer des étudiants (id=1, 2, 3, 4)
                Etudiant etudiant1 = new Etudiant();
                etudiant1.nom = "Alice Dubois";
                etudiant1.cne = "CNE001";
                etudiant1.motDePasse = "alice123";
                etudiant1.email = "alice.dubois@example.com";
                etudiantDao.insert(etudiant1); // id=1

                Etudiant etudiant2 = new Etudiant();
                etudiant2.nom = "Bob Martin";
                etudiant2.cne = "CNE002";
                etudiant2.motDePasse = "bob456";
                etudiant2.email = "bob.martin@example.com";
                etudiantDao.insert(etudiant2); // id=2

                Etudiant etudiant3 = new Etudiant();
                etudiant3.nom = "Claire Dupont";
                etudiant3.cne = "CNE003";
                etudiant3.motDePasse = "claire789";
                etudiant3.email = "claire.dupont@example.com";
                etudiantDao.insert(etudiant3); // id=3

                Etudiant etudiant4 = new Etudiant();
                etudiant4.nom = "David Leclerc";
                etudiant4.cne = "CNE004";
                etudiant4.motDePasse = "david012";
                etudiant4.email = "david.leclerc@example.com";
                etudiantDao.insert(etudiant4); // id=4

                // Insérer des absences
                Absence absence1 = new Absence(1, 1, "2025-05-01", "Cours 1", null, "Avertissement");
                absenceDao.insert(absence1);

                Absence absence2 = new Absence(1, 1, "2025-05-02", "Cours 2", "justificatifs/absence1_20250502.pdf", null);
                absenceDao.insert(absence2);

                Absence absence3 = new Absence(2, 1, "2025-05-01", "Cours 1", null, "Sanction");
                absenceDao.insert(absence3);

                Absence absence4 = new Absence(3, 2, "2025-05-03", "TP 1", null, null);
                absenceDao.insert(absence4);

                Absence absence5 = new Absence(4, 3, "2025-05-04", "Cours 1", "justificatifs/absence5_20250504.pdf", "Avertissement");
                absenceDao.insert(absence5);
            }
        }).start();
    }
}