package com.example.gestionabsences.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.gestionabsences.R;
import com.example.gestionabsences.viewmodel.EnseignantViewModel;
import com.example.gestionabsences.viewmodel.EtudiantViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private EtudiantViewModel etudiantViewModel;
    private EnseignantViewModel enseignantViewModel;
    private TextInputEditText nomInput, passwordInput;
    private RadioGroup roleGroup;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser les ViewModels
        etudiantViewModel = new ViewModelProvider(this).get(EtudiantViewModel.class);
        enseignantViewModel = new ViewModelProvider(this).get(EnseignantViewModel.class);

        // Initialiser les vues
        nomInput = findViewById(R.id.nomInput);
        passwordInput = findViewById(R.id.passwordInput);
        roleGroup = findViewById(R.id.roleGroup);
        loginButton = findViewById(R.id.loginButton);

        // Gestion du clic sur le bouton de connexion
        loginButton.setOnClickListener(v -> {
            String nom = nomInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (nom.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedRoleId = roleGroup.getCheckedRadioButtonId();
            if (selectedRoleId == R.id.roleEtudiant) {
                // Authentification étudiant
                etudiantViewModel.authenticateEtudiant(nom, password, (success, etudiant) -> {
                    if (success) {
                        Intent intent = new Intent(LoginActivity.this, EtudiantAbsencesActivity.class);
                        intent.putExtra("etudiantId", etudiant.id);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Nom ou mot de passe étudiant incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (selectedRoleId == R.id.roleEnseignant) {
                // Authentification enseignant
                enseignantViewModel.authenticateEnseignant(nom, password, (success, enseignant) -> {
                    if (success) {
                        Intent intent = new Intent(LoginActivity.this, EnseignantAbsencesActivity.class);
                        intent.putExtra("enseignantId", enseignant.id);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Nom ou mot de passe enseignant incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Veuillez sélectionner un rôle", Toast.LENGTH_SHORT).show();
            }
        });
    }
}