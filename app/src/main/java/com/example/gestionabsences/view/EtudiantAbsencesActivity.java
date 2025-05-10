package com.example.gestionabsences.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestionabsences.R;
import com.example.gestionabsences.model.Absence;
import com.example.gestionabsences.viewmodel.AbsenceViewModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EtudiantAbsencesActivity extends AppCompatActivity {
    private AbsenceViewModel absenceViewModel;
    private RecyclerView absencesRecyclerView;
    private AbsenceAdapter absenceAdapter;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etudiant_absences);

        // Récupérer l'ID de l'étudiant depuis l'intent
        int etudiantId = getIntent().getIntExtra("etudiantId", -1);
        if (etudiantId == -1) {
            Toast.makeText(this, "Erreur: ID étudiant non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser le ViewModel
        absenceViewModel = new ViewModelProvider(this).get(AbsenceViewModel.class);

        // Initialiser le RecyclerView
        absencesRecyclerView = findViewById(R.id.absencesRecyclerView);
        absencesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        absenceAdapter = new AbsenceAdapter(new ArrayList<>());
        absencesRecyclerView.setAdapter(absenceAdapter);

        // Observer les absences de l'étudiant
        absenceViewModel.getAbsencesByEtudiant(etudiantId).observe(this, absences -> {
            if (absences != null) {
                absenceAdapter.updateAbsences(absences);
            } else {
                Toast.makeText(this, "Aucune absence trouvée", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialiser le lanceur pour l'intent implicite
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri fileUri = result.getData().getData();
                if (fileUri != null) {
                    // Récupérer l'absence associée
                    Absence selectedAbsence = (Absence) absencesRecyclerView
                            .findViewHolderForAdapterPosition(absenceAdapter.getLastClickedPosition())
                            .itemView.getTag();
                    if (selectedAbsence != null) {
                        try {
                            // Créer le dossier justificatifs s'il n'existe pas
                            File justificatifsDir = new File(getFilesDir(), "justificatifs");
                            if (!justificatifsDir.exists()) {
                                justificatifsDir.mkdirs();
                            }

                            // Générer un nom de fichier unique
                            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
                            String fileExtension = getFileExtension(fileUri);
                            String fileName = "justificatif_absence_" + selectedAbsence.id + "_" + timestamp + fileExtension;
                            File destinationFile = new File(justificatifsDir, fileName);

                            // Copier le fichier dans le stockage local
                            copyFileToLocalStorage(fileUri, destinationFile);

                            // Mettre à jour le champ justificatif
                            selectedAbsence.justificatif = "justificatifs/" + fileName;
                            absenceViewModel.updateAbsence(selectedAbsence);
                            Toast.makeText(this, "Justificatif ajouté pour l'absence du " + selectedAbsence.date, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "Erreur lors de l'ajout du justificatif: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Aucun fichier sélectionné", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Copier le fichier depuis l'URI vers le stockage local
    private void copyFileToLocalStorage(Uri sourceUri, File destinationFile) throws Exception {
        try (InputStream inputStream = getContentResolver().openInputStream(sourceUri);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            if (inputStream == null) {
                throw new Exception("Impossible d'ouvrir le fichier source");
            }
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    // Obtenir l'extension du fichier à partir de l'URI
    private String getFileExtension(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("application/pdf")) {
                return ".pdf";
            } else if (mimeType.startsWith("image/")) {
                return ".jpg"; // Simplification, peut être .png, .jpeg, etc.
            }
        }
        return ".file"; // Fallback
    }

    // Adaptateur pour le RecyclerView
    private class AbsenceAdapter extends RecyclerView.Adapter<AbsenceAdapter.AbsenceViewHolder> {
        private List<Absence> absences;
        private int lastClickedPosition = -1;

        public AbsenceAdapter(List<Absence> absences) {
            this.absences = absences;
        }

        public void updateAbsences(List<Absence> newAbsences) {
            this.absences = newAbsences;
            notifyDataSetChanged();
        }

        public int getLastClickedPosition() {
            return lastClickedPosition;
        }

        @Override
        public AbsenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_absence, parent, false);
            return new AbsenceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AbsenceViewHolder holder, int position) {
            Absence absence = absences.get(position);
            holder.dateTextView.setText(absence.date);
            holder.seanceTextView.setText("Séance: " + absence.seance);
            holder.justificatifTextView.setText("Justificatif: " + (absence.justificatif != null ? "Oui" : "Non"));
            holder.penaliteTextView.setText("Pénalité: " + (absence.penalite != null ? absence.penalite : "Aucune"));
            holder.itemView.setTag(absence); // Stocker l'absence dans le tag

            // Gérer la visibilité du bouton Voir Justificatif
            holder.viewJustificatifButton.setVisibility(absence.justificatif != null ? View.VISIBLE : View.GONE);
            holder.viewJustificatifButton.setOnClickListener(v -> {
                if (absence.justificatif != null) {
                    File file = new File(getFilesDir(), absence.justificatif);
                    if (file.exists()) {
                        Uri fileUri = FileProvider.getUriForFile(
                                EtudiantAbsencesActivity.this,
                                "com.example.gestionabsences.fileprovider",
                                file
                        );
                        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                        viewIntent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
                        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            startActivity(Intent.createChooser(viewIntent, "Ouvrir le justificatif"));
                        } catch (Exception e) {
                            Toast.makeText(EtudiantAbsencesActivity.this, "Aucune application pour ouvrir le fichier", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EtudiantAbsencesActivity.this, "Fichier justificatif non trouvé", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.addJustificatifButton.setOnClickListener(v -> {
                lastClickedPosition = holder.getAdapterPosition();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf,image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    filePickerLauncher.launch(Intent.createChooser(intent, "Sélectionner un fichier"));
                } catch (Exception e) {
                    Toast.makeText(EtudiantAbsencesActivity.this, "Aucune application pour sélectionner un fichier", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return absences.size();
        }

        class AbsenceViewHolder extends RecyclerView.ViewHolder {
            TextView dateTextView, seanceTextView, justificatifTextView, penaliteTextView;
            Button addJustificatifButton, viewJustificatifButton;

            public AbsenceViewHolder(View itemView) {
                super(itemView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                seanceTextView = itemView.findViewById(R.id.seanceTextView);
                justificatifTextView = itemView.findViewById(R.id.justificatifTextView);
                penaliteTextView = itemView.findViewById(R.id.penaliteTextView);
                addJustificatifButton = itemView.findViewById(R.id.addJustificatifButton);
                viewJustificatifButton = itemView.findViewById(R.id.viewJustificatifButton);
            }
        }
    }
}