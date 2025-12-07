package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.callback.GenericCallback;

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

        hasVeicoloCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            postiVeicoloEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        btnSubmitNucleo.setOnClickListener(v -> submitNucleo());
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
                return;
            }
            try {
                postiVeicolo = Integer.parseInt(postiStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Il numero di posti non è valido.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        loadingProgressBarNucleo.setVisibility(View.VISIBLE);
        btnSubmitNucleo.setEnabled(false);

        gestioneNucleoFamiliareControl.creaNucleo(viaPiazza, comune, regione, paese, civico, cap, hasVeicolo, postiVeicolo, new GenericCallback() {
            @Override
            public void onSuccess(String message) {
                loadingProgressBarNucleo.setVisibility(View.GONE);
                btnSubmitNucleo.setEnabled(true);
                Toast.makeText(CreaNucleoBoundary.this, message, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String message) {
                loadingProgressBarNucleo.setVisibility(View.GONE);
                btnSubmitNucleo.setEnabled(true);
                Toast.makeText(CreaNucleoBoundary.this, getString(R.string.generic_error, message), Toast.LENGTH_LONG).show();
            }
        });
    }
}
