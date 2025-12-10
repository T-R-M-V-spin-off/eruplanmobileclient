package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import eruplan.unisa.eruplan.R;

/**
 * Rappresenta la schermata iniziale (Boundary) da cui l'utente può scegliere
 * di avviare l'operazione di creazione di un nuovo nucleo familiare.
 */
public class CosaVuoiFareBoundary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosa_vuoi_fare);

        // Collega i pulsanti del layout
        Button btnCreaNucleo = findViewById(R.id.btnCreaNucleo);
        Button btnInvitiRicevuti = findViewById(R.id.btnInvitiRicevuti);

        // La logica di navigazione è ora responsabilità della Boundary
        btnCreaNucleo.setOnClickListener(v -> startActivity(new Intent(this, CreaNucleoBoundary.class)));

        btnInvitiRicevuti.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaRichiesteBoundary.class);
            intent.putExtra("IS_ACTIONABLE", true);
            startActivity(intent);
        });
    }
}
