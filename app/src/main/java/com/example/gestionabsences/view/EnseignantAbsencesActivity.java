package com.example.gestionabsences.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestionabsences.R;
import com.example.gestionabsences.dao.EtudiantAbsenceCount;
import com.example.gestionabsences.model.Matiere;
import com.example.gestionabsences.viewmodel.AbsenceViewModel;
import com.example.gestionabsences.viewmodel.MatiereViewModel;
import java.util.ArrayList;
import java.util.List;

public class EnseignantAbsencesActivity extends AppCompatActivity {
    private MatiereViewModel matiereViewModel;
    private AbsenceViewModel absenceViewModel;
    private Spinner matiereSpinner;
    private RecyclerView etudiantsRecyclerView;
    private EtudiantAbsenceAdapter etudiantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enseignant_absences);

        // Récupérer l'ID de l'enseignant depuis l'intent
        int enseignantId = getIntent().getIntExtra("enseignantId", -1);
        if ( enseignantId == -1) {
            Toast.makeText(this, "Erreur: ID enseignant non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser les ViewModels
        matiereViewModel = new ViewModelProvider(this).get(MatiereViewModel.class);
        absenceViewModel = new ViewModelProvider(this).get(AbsenceViewModel.class);

        // Initialiser le Spinner
        matiereSpinner = findViewById(R.id.matiereSpinner);
        // Créer un ArrayAdapter personnalisé pour afficher uniquement le nom de la matière
        ArrayAdapter<Matiere> spinnerAdapter = new ArrayAdapter<Matiere>(this, android.R.layout.simple_spinner_item, new ArrayList<>()) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                android.widget.TextView textView = (android.widget.TextView) view;
                textView.setText(getItem(position).nom);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                android.widget.TextView textView = (android.widget.TextView) view;
                textView.setText(getItem(position).nom);
                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matiereSpinner.setAdapter(spinnerAdapter);

        // Observer les matières
        matiereViewModel.getMatieresByEnseignant(enseignantId).observe(this, matieres -> {
            if (matieres != null && !matieres.isEmpty()) {
                spinnerAdapter.clear();
                spinnerAdapter.addAll(matieres);
                spinnerAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Aucune matière trouvée", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialiser le RecyclerView
        etudiantsRecyclerView = findViewById(R.id.etudiantsRecyclerView);
        etudiantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        etudiantAdapter = new EtudiantAbsenceAdapter(new ArrayList<>());
        etudiantsRecyclerView.setAdapter(etudiantAdapter);

        // Gérer la sélection d'une matière
        matiereSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Matiere selectedMatiere = (Matiere) parent.getItemAtPosition(position);
                // Observer les étudiants avec leur nombre d'absences
                absenceViewModel.getEtudiantsWithAbsenceCount(selectedMatiere.id).observe(EnseignantAbsencesActivity.this, etudiants -> {
                    if (etudiants != null) {
                        etudiantAdapter.updateEtudiants(etudiants);
                    } else {
                        Toast.makeText(EnseignantAbsencesActivity.this, "Aucun étudiant trouvé", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Rien à faire
            }
        });
    }

    // Adaptateur pour le RecyclerView
    private class EtudiantAbsenceAdapter extends RecyclerView.Adapter<EtudiantAbsenceAdapter.EtudiantViewHolder> {
        private List<EtudiantAbsenceCount> etudiants;

        public EtudiantAbsenceAdapter(List<EtudiantAbsenceCount> etudiants) {
            this.etudiants = etudiants;
        }

        public void updateEtudiants(List<EtudiantAbsenceCount> newEtudiants) {
            this.etudiants = newEtudiants;
            notifyDataSetChanged();
        }

        @Override
        public EtudiantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_etudiant_absence, parent, false);
            return new EtudiantViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EtudiantViewHolder holder, int position) {
            EtudiantAbsenceCount etudiant = etudiants.get(position);
            holder.nomTextView.setText(etudiant.nom);
            holder.absencesTextView.setText("Absences: " + etudiant.nombreAbsences);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(EnseignantAbsencesActivity.this, EtudiantAbsencesDetailActivity.class);
                intent.putExtra("etudiantId", etudiant.id);
                intent.putExtra("matiereId", ((Matiere) matiereSpinner.getSelectedItem()).id);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return etudiants.size();
        }

        class EtudiantViewHolder extends RecyclerView.ViewHolder {
            TextView nomTextView, absencesTextView;

            public EtudiantViewHolder(View itemView) {
                super(itemView);
                nomTextView = itemView.findViewById(R.id.nomTextView);
                absencesTextView = itemView.findViewById(R.id.absencesTextView);
            }
        }
    }
}