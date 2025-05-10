package com.example.gestionabsences.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.gestionabsences.R;
import com.example.gestionabsences.model.Absence;
import com.example.gestionabsences.viewmodel.AbsenceViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateAbsenceActivity extends AppCompatActivity {
    private AbsenceViewModel absenceViewModel;
    private TextInputEditText dateInput, seanceInput, penaliteInput;
    private Button saveButton;
    private Absence absence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_absence);

        // Récupérer l'ID de l'absence depuis l'intent
        int absenceId = getIntent().getIntExtra("absenceId", -1);
        if (absenceId == -1) {
            Toast.makeText(this, "Erreur: ID absence non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser le ViewModel
        absenceViewModel = new ViewModelProvider(this).get(AbsenceViewModel.class);

        // Initialiser les vues
        dateInput = findViewById(R.id.dateInput);
        seanceInput = findViewById(R.id.seanceInput);
        penaliteInput = findViewById(R.id.penaliteInput);
        saveButton = findViewById(R.id.saveButton);

        // Configurer le DatePicker
        dateInput.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Sélectionner une date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dateInput.setText(sdf.format(calendar.getTime()));
            });
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        // Charger l'absence
        absenceViewModel.getAbsenceById(absenceId).observe(this, abs -> {
            if (abs != null) {
                absence = abs;
                dateInput.setText(absence.date);
                seanceInput.setText(absence.seance);
                penaliteInput.setText(absence.penalite);
            } else {
                Toast.makeText(this, "Absence non trouvée", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Bouton Enregistrer
        saveButton.setOnClickListener(v -> {
            String date = dateInput.getText().toString().trim();
            String seance = seanceInput.getText().toString().trim();
            String penalite = penaliteInput.getText().toString().trim();

            if (date.isEmpty() || seance.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir la date et la séance", Toast.LENGTH_SHORT).show();
                return;
            }

            if (absence != null) {
                absence.date = date;
                absence.seance = seance;
                absence.penalite = penalite.isEmpty() ? null : penalite;
                absenceViewModel.updateAbsence(absence);
                Toast.makeText(this, "Absence mise à jour", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}