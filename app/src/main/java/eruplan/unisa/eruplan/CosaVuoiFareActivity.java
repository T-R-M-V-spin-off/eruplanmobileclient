package eruplan.unisa.eruplan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Rappresenta la schermata iniziale (Boundary) da cui l'utente può scegliere
 * di avviare l'operazione di creazione di un nuovo nucleo familiare.
 */
public class CosaVuoiFareActivity extends AppCompatActivity {

    private Button btnCreaNucleo;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosa_vuoi_fare);

        // Inizializza il Control, passando il contesto dell'activity
        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        // Collega il pulsante del layout
        btnCreaNucleo = findViewById(R.id.btnCreaNucleo);

        // Imposta il listener per il click
        btnCreaNucleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chiama il metodo del Control per mostrare il form di creazione nucleo
                gestioneNucleoFamiliareControl.mostraFormCreaNucleo();
            }
        });
    }
}
