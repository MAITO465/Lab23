package com.maito.lab23antidebug;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private NativeSecurityManager securityManager;

    private TextView tvShieldIcon, tvSecurityStatus, tvSecurityDetails;
    private TextView tvHello, tvFact;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        securityManager = new NativeSecurityManager();

        tvShieldIcon = findViewById(R.id.tvShieldIcon);
        tvSecurityStatus = findViewById(R.id.tvSecurityStatus);
        tvSecurityDetails = findViewById(R.id.tvSecurityDetails);
        tvHello = findViewById(R.id.tvHello);
        tvFact = findViewById(R.id.tvFact);
        btnRefresh = findViewById(R.id.btnRefresh);

        btnRefresh.setOnClickListener(v -> checkSecurityAndLoadData());

        // Lancement immédiat au démarrage
        checkSecurityAndLoadData();
    }

    private void checkSecurityAndLoadData() {
        // L'appel part dans la librairie native C++
        int securityCode = securityManager.getSecurityStatusNative();

        if (securityCode == 0) {
            // SYSTÈME SAIN
            tvShieldIcon.setText("✅");
            tvSecurityStatus.setText("SÉCURITÉ OPTIMALE");
            tvSecurityStatus.setTextColor(ContextCompat.getColor(this, R.color.shield_green));
            tvSecurityDetails.setText("Aucun débogueur détecté.");

            // Exécution des fonctions sensibles autorisée
            tvHello.setText("Hello : " + securityManager.helloFromJNI());
            tvFact.setText("Factoriel(10) : " + securityManager.factorial(10));
            tvHello.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvFact.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

        } else {
            // SYSTÈME COMPROMIS
            tvShieldIcon.setText("☠️");
            tvSecurityStatus.setText("SYSTÈME COMPROMIS");
            tvSecurityStatus.setTextColor(ContextCompat.getColor(this, R.color.shield_red));

            String details = "";
            if (securityCode == 1) details = "Débogueur (ptrace) détecté.";
            else if (securityCode == 2) details = "Bibliothèques suspectes (Frida/Xposed) détectées.";
            else if (securityCode == 3) details = "Débogueur ET instrumentation dynamique détectés.";
            
            tvSecurityDetails.setText(details);

            // Blocage logique de l'UI
            tvHello.setText("Hello : [ACCÈS VERROUILLÉ]");
            tvFact.setText("Factoriel(10) : [ACCÈS VERROUILLÉ]");
            tvHello.setTextColor(ContextCompat.getColor(this, R.color.shield_red));
            tvFact.setTextColor(ContextCompat.getColor(this, R.color.shield_red));
        }
    }
}
