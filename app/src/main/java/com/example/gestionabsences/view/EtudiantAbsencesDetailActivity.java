package com.example.gestionabsences.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestionabsences.R;
import com.example.gestionabsences.model.Absence;
import com.example.gestionabsences.model.Etudiant;
import com.example.gestionabsences.viewmodel.AbsenceViewModel;
import com.example.gestionabsences.viewmodel.EtudiantViewModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EtudiantAbsencesDetailActivity extends AppCompatActivity {
    private EtudiantViewModel etudiantViewModel;
    private AbsenceViewModel absenceViewModel;
    private TextView etudiantNomTextView;
    private Button addAbsenceButton;
    private Button contactEmailButton;
    private RecyclerView absencesRecyclerView;
    private AbsenceDetailAdapter absenceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etudiant_absences_detail);

        // Récupérer les IDs depuis l'intent
        int etudiantId = getIntent().getIntExtra("etudiantId", -1);
        int matiereId = getIntent().getIntExtra("matiereId", -1);
        if (etudiantId == -1 || matiereId == -1) {
            Toast.makeText(this, "Erreur: IDs non trouvés", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser les ViewModels
        etudiantViewModel = new ViewModelProvider(this).get(EtudiantViewModel.class);
        absenceViewModel = new ViewModelProvider(this).get(AbsenceViewModel.class);

        // Initialiser les vues
        etudiantNomTextView = findViewById(R.id.etudiantNomTextView);
        addAbsenceButton = findViewById(R.id.addAbsenceButton);
        contactEmailButton = findViewById(R.id.contactEmailButton);
        absencesRecyclerView = findViewById(R.id.absencesRecyclerView);
        absencesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        absenceAdapter = new AbsenceDetailAdapter(new ArrayList<>());
        absencesRecyclerView.setAdapter(absenceAdapter);

        // Charger les coordonnées de l'étudiant
        etudiantViewModel.getEtudiantById(etudiantId).observe(this, etudiant -> {
            if (etudiant != null) {
                etudiantNomTextView.setText("Étudiant: " + etudiant.nom);
                // Gérer le bouton Contacter par email
                contactEmailButton.setOnClickListener(v -> {
                    if (etudiant.email != null && !etudiant.email.isEmpty()) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:" + etudiant.email));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Notification d'absence");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Bonjour " + etudiant.nom + ",\n\nCeci est un message concernant vos absences.\nCordialement,\n[Votre Nom]");
                        try {
                            startActivity(Intent.createChooser(emailIntent, "Envoyer l'email"));
                        } catch (Exception e) {
                            Toast.makeText(this, "Aucune application de messagerie trouvée", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Adresse email non disponible", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Étudiant non trouvé", Toast.LENGTH_SHORT).show();
            }
        });

        // Charger les absences
        absenceViewModel.getAbsencesByEtudiantAndMatiere(etudiantId, matiereId).observe(this, absences -> {
            if (absences != null) {
                absenceAdapter.updateAbsences(absences);
            } else {
                Toast.makeText(this, "Aucune absence trouvée", Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton Ajouter absence
        addAbsenceButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAbsenceActivity.class);
            intent.putExtra("etudiantId", etudiantId);
            intent.putExtra("matiereId", matiereId);
            startActivity(intent);
        });
    }

    // Adaptateur pour le RecyclerView
    private class AbsenceDetailAdapter extends RecyclerView.Adapter<AbsenceDetailAdapter.AbsenceViewHolder> {
        private List<Absence> absences;

        public AbsenceDetailAdapter(List<Absence> absences) {
            this.absences = absences;
        }

        public void updateAbsences(List<Absence> newAbsences) {
            this.absences = newAbsences;
            notifyDataSetChanged();
        }

        @Override
        public AbsenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_absence_detail, parent, false);
            return new AbsenceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AbsenceViewHolder holder, int position) {
            Absence absence = absences.get(position);
            holder.dateTextView.setText(absence.date);
            holder.seanceTextView.setText("Séance: " + absence.seance);
            holder.justificatifTextView.setText("Justificatif: " + (absence.justificatif != null ? "Oui" : "Non"));
            holder.penaliteTextView.setText("Pénalité: " + (absence.penalite != null ? absence.penalite : "Aucune"));

            // Gérer la visibilité du bouton Voir Justificatif
            holder.viewJustificatifButton.setVisibility(absence.justificatif != null ? View.VISIBLE : View.GONE);
            holder.viewJustificatifButton.setOnClickListener(v -> {
                if (absence.justificatif != null) {
                    File file = new File(getFilesDir(), absence.justificatif);
                    if (file.exists()) {
                        Uri fileUri = FileProvider.getUriForFile(
                                EtudiantAbsencesDetailActivity.this,
                                "com.example.gestionabsences.fileprovider",
                                file
                        );
                        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                        viewIntent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
                        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            startActivity(Intent.createChooser(viewIntent, "Ouvrir le justificatif"));
                        } catch (Exception e) {
                            Toast.makeText(EtudiantAbsencesDetailActivity.this, "Aucune application pour ouvrir le fichier", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EtudiantAbsencesDetailActivity.this, "Fichier justificatif non trouvé", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Bouton Modifier
            holder.editButton.setOnClickListener(v -> {
                Intent intent = new Intent(EtudiantAbsencesDetailActivity.this, UpdateAbsenceActivity.class);
                intent.putExtra("absenceId", absence.id);
                startActivity(intent);
            });

            // Bouton Supprimer
            holder.deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(EtudiantAbsencesDetailActivity.this)
                        .setTitle("Confirmer la suppression")
                        .setMessage("Voulez-vous supprimer cette absence ?")
                        .setPositiveButton("Oui", (dialog, which) -> {
                            absenceViewModel.deleteAbsence(absence);
                            Toast.makeText(EtudiantAbsencesDetailActivity.this, "Absence supprimée", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Non", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return absences.size();
        }

        class AbsenceViewHolder extends RecyclerView.ViewHolder {
            TextView dateTextView, seanceTextView, justificatifTextView, penaliteTextView;
            Button editButton, deleteButton, viewJustificatifButton;

            public AbsenceViewHolder(View itemView) {
                super(itemView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                seanceTextView = itemView.findViewById(R.id.seanceTextView);
                justificatifTextView = itemView.findViewById(R.id.justificatifTextView);
                penaliteTextView = itemView.findViewById(R.id.penaliteTextView);
                editButton = itemView.findViewById(R.id.editButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);
                viewJustificatifButton = itemView.findViewById(R.id.viewJustificatifButton);
            }
        }
    }
}