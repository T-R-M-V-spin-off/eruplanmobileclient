package eruplan.unisa.eruplan;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Rappresenta l'interfaccia utente (Boundary) per la compilazione dei dati
 * relativi a un nuovo nucleo familiare.
 */
public class CreaNucleoActivity extends AppCompatActivity {

    private EditText viaPiazzaEditText, comuneEditText, regioneEditText, paeseEditText, civicoEditText, capEditText;
    private Button btnSubmitNucleo;
    private ProgressBar loadingProgressBarNucleo;

    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_nucleo);

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        initViews();

        btnSubmitNucleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNucleo();
            }
        });
    }

    private void initViews() {
        viaPiazzaEditText = findViewById(R.id.viaPiazzaEditText);
        comuneEditText = findViewById(R.id.comuneEditText);
        regioneEditText = findViewById(R.id.regioneEditText);
        paeseEditText = findViewById(R.id.paeseEditText);
        civicoEditText = findViewById(R.id.civicoEditText);
        capEditText = findViewById(R.id.capEditText);
        btnSubmitNucleo = findViewById(R.id.btnSubmitNucleo);
        loadingProgressBarNucleo = findViewById(R.id.loadingProgressBarNucleo);
    }

    private void submitNucleo() {
        String viaPiazza = viaPiazzaEditText.getText().toString().trim();
        String comune = comuneEditText.getText().toString().trim();
        String regione = regioneEditText.getText().toString().trim();
        String paese = paeseEditText.getText().toString().trim();
        String civico = civicoEditText.getText().toString().trim();
        String cap = capEditText.getText().toString().trim();

        try {
            loadingProgressBarNucleo.setVisibility(View.VISIBLE);
            btnSubmitNucleo.setEnabled(false);

            gestioneNucleoFamiliareControl.creaNucleo(viaPiazza, comune, regione, paese, civico, cap, new GestioneNucleoFamiliareControl.ControlCallback() {
                @Override
                public void onInserimentoSuccesso(String message) {
                    loadingProgressBarNucleo.setVisibility(View.GONE);
                    btnSubmitNucleo.setEnabled(true);
                    Toast.makeText(CreaNucleoActivity.this, message, Toast.LENGTH_LONG).show();
                    
                    // Reindirizza l'utente alla schermata di gestione del nucleo
                    //Intent intent = new Intent(CreaNucleoActivity.this, GestisciNucleoBoundary.class);
                    //startActivity(intent);

                    // Chiude l'activity corrente
                    finish();
                }

                @Override
                public void onInserimentoErrore(String message) {
                    loadingProgressBarNucleo.setVisibility(View.GONE);
                    btnSubmitNucleo.setEnabled(true);
                    Toast.makeText(CreaNucleoActivity.this, "Errore: " + message, Toast.LENGTH_LONG).show();
                }
            });

        } catch (IllegalArgumentException e) {
            loadingProgressBarNucleo.setVisibility(View.GONE);
            btnSubmitNucleo.setEnabled(true);
            Toast.makeText(this, "Errore di validazione: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
