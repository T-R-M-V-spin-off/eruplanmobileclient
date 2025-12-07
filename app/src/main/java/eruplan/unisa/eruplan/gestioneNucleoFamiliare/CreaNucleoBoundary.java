package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import eruplan.unisa.eruplan.R;

/**
 * Rappresenta l'interfaccia utente (Boundary) per la compilazione dei dati
 * relativi a un nuovo nucleo familiare.
 */
public class CreaNucleoBoundary extends AppCompatActivity {

    private EditText viaPiazzaEditText, comuneEditText, regioneEditText, paeseEditText, civicoEditText, capEditText, postiVeicoloEditText;
    private Button btnSubmitNucleo;
    private ProgressBar loadingProgressBarNucleo;
    private CheckBox hasVeicoloCheckBox;



    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_nucleo);

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        initViews();

        //CheckBox per far comparire e scomparire il campo per i posti del veicolo
        hasVeicoloCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    postiVeicoloEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

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
        hasVeicoloCheckBox = findViewById(R.id.hasVeicoloCheckBox);
        postiVeicoloEditText = findViewById(R.id.postiVeicoloEditText);
    }

    private void submitNucleo() {
        String viaPiazza = viaPiazzaEditText.getText().toString().trim();
        String comune = comuneEditText.getText().toString().trim();
        String regione = regioneEditText.getText().toString().trim();
        String paese = paeseEditText.getText().toString().trim();
        String civico = civicoEditText.getText().toString().trim();
        String cap = capEditText.getText().toString().trim();
        boolean hasVeicolo = hasVeicoloCheckBox.isChecked();
        int postiVeicolo = 0;

        if (hasVeicolo) {
            String postiStr = postiVeicoloEditText.getText().toString().trim();
            if (postiStr.isEmpty()) {
                Toast.makeText(this, "Per favore, inserisci il numero di posti.", Toast.LENGTH_SHORT).show();
                return; // Interrompe l'operazione
            }
            try {
                postiVeicolo = Integer.parseInt(postiStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Il numero di posti non è valido.", Toast.LENGTH_SHORT).show();
                return; // Interrompe l'operazione
            }
        }

        loadingProgressBarNucleo.setVisibility(View.VISIBLE);
        btnSubmitNucleo.setEnabled(false);

        gestioneNucleoFamiliareControl.creaNucleo(viaPiazza, comune, regione, paese, civico, cap, hasVeicolo, postiVeicolo, new GestioneNucleoFamiliareControl.ControlCallback() {
            @Override
            public void onSuccess(String message) {
                loadingProgressBarNucleo.setVisibility(View.GONE);
                btnSubmitNucleo.setEnabled(true);
                Toast.makeText(CreaNucleoBoundary.this, message, Toast.LENGTH_LONG).show();

                // Chiude l'activity corrente
                finish();
            }

            @Override
            public void onError(String message) {
                loadingProgressBarNucleo.setVisibility(View.GONE);
                btnSubmitNucleo.setEnabled(true);
                Toast.makeText(CreaNucleoBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
