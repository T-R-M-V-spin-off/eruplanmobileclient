package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import eruplan.unisa.eruplan.R;

/**
 * Rappresenta la schermata iniziale (Boundary) da cui l'utente può scegliere
 * di avviare l'operazione di creazione di un nuovo nucleo familiare.
 */
public class CosaVuoiFareBoundary extends AppCompatActivity {

    private Button btnCreaNucleo;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;
    private Button btnInvitiRicevuti; // Riferimento al bottone per gli inviti

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosa_vuoi_fare);

        // Inizializza il Control, passando il contesto dell'activity
        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        // Collega il pulsante del layout
        btnCreaNucleo = findViewById(R.id.btnCreaNucleo);

        // Collega il bottone degli inviti (decommentare quando viene aggiunto all'XML)
        btnInvitiRicevuti = findViewById(R.id.btnInvitiRicevuti);

        // Imposta il listener per il click
        btnCreaNucleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chiama il metodo del Control per mostrare il form di creazione nucleo
                gestioneNucleoFamiliareControl.mostraFormCreaNucleo();
            }
        });

        // Listener per il bottone "Inviti Ricevuti"
        // Qui passiamo TRUE, in modo da rendere visibili "Conferma" e "Indietro"
        btnInvitiRicevuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CosaVuoiFareBoundary.this, ListaRichiesteBoundary.class);
                intent.putExtra("IS_ACTIONABLE", true); // Passiamo true: voglio i tasti "Conferma/Indietro"
                startActivity(intent);
            }
        });
    }
}
