package eruplan.unisa.eruplan.gestioneEmergenza;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import eruplan.unisa.eruplan.R;

public class GestioneEmergenzaBoundary extends AppCompatActivity {

    private ActivityResultLauncher<Intent> mapsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_piano_evacuazione);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Registra il callback per quando l'utente torna da Maps
        mapsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Questo codice viene eseguito quando l'utente torna alla nostra app
                showArrivalConfirmationDialog();
            });

        //Qui passano i punti ed il lin per il percorso da seguire tramite MAPS
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/40.7743,14.6721/40.7736,14.6806/Salerno");

        MaterialButton button = findViewById(R.id.pulanteMappa);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            intent.setPackage("com.google.android.apps.maps"); // Forza l'apertura con Google Maps

            try {
                mapsLauncher.launch(intent); // Tenta di avviare Maps
            } catch (ActivityNotFoundException e) {
                // Se Maps non è installato, informa l'utente e apre il Play Store
                Toast.makeText(this, "Google Maps non è installato. Apro il Play Store.", Toast.LENGTH_LONG).show();
                try {
                    // Tenta di aprire l'app Play Store
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps")));
                } catch (ActivityNotFoundException playStoreEx) {
                    // Se l'app Play Store non è disponibile, apre il sito web
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")));
                }
            }
        });
    }

    /**
     * Mostra un popup per chiedere all'utente se è arrivato a destinazione.
     */
    private void showArrivalConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Arrivo a destinazione")
            .setMessage("Sei arrivato a destinazione?")
            .setPositiveButton("Sì", (dialog, which) -> {
                Toast.makeText(this, "Grazie per la conferma!", Toast.LENGTH_SHORT).show();
                // Qui potresti inviare lo stato al server o chiudere la schermata
                finish();
            })
            .setNegativeButton("No", (dialog, which) -> {
                Toast.makeText(this, "Navigazione non completata.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            })
            .setCancelable(false) // L'utente deve scegliere per chiudere
            .show();
    }
}
