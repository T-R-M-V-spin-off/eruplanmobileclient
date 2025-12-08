package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.callback.GenericCallback;
import eruplan.unisa.eruplan.callback.NucleoCallback;
import eruplan.unisa.eruplan.entity.NucleoEntity;

public class VisualizzaNucleoBoundary extends AppCompatActivity {

    private TextInputEditText viaPiazzaEditText, comuneEditText, regioneEditText, paeseEditText, civicoEditText, capEditText;
    private Button btnSalva, btnChiudi;
    private ProgressBar loadingProgressBar;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_nucleo);

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        initViews();

        btnSalva.setOnClickListener(v -> salvaModifiche());

        btnChiudi.setOnClickListener(v -> {
            // Semplificato: finish() torna all'activity precedente nello stack
            finish();
        });

        caricaDatiNucleo();
    }

    private void initViews() {
        viaPiazzaEditText = findViewById(R.id.viaPiazzaEditText);
        comuneEditText = findViewById(R.id.comuneEditText);
        regioneEditText = findViewById(R.id.regioneEditText);
        paeseEditText = findViewById(R.id.paeseEditText);
        civicoEditText = findViewById(R.id.civicoEditText);
        capEditText = findViewById(R.id.capEditText);
        btnSalva = findViewById(R.id.btnSalva);
        btnChiudi = findViewById(R.id.btnChiudi);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
    }

    private void caricaDatiNucleo() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        gestioneNucleoFamiliareControl.getNucleo(new NucleoCallback() {
            @Override
            public void onNucleoLoaded(NucleoEntity nucleo) {
                loadingProgressBar.setVisibility(View.GONE);
                if (nucleo != null) {
                    viaPiazzaEditText.setText(nucleo.getViaPiazza());
                    comuneEditText.setText(nucleo.getComune());
                    regioneEditText.setText(nucleo.getRegione());
                    paeseEditText.setText(nucleo.getPaese());
                    civicoEditText.setText(nucleo.getCivico());
                    capEditText.setText(nucleo.getCap());
                } else {
                    Toast.makeText(VisualizzaNucleoBoundary.this, R.string.no_nucleus_found, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String message) {
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(VisualizzaNucleoBoundary.this, getString(R.string.generic_error, message), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(viaPiazzaEditText.getText())) {
            viaPiazzaEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(comuneEditText.getText())) {
            comuneEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(regioneEditText.getText())) {
            regioneEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(paeseEditText.getText())) {
            paeseEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(civicoEditText.getText())) {
            civicoEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(capEditText.getText())) {
            capEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        return true;
    }

    private void salvaModifiche() {
        if (!validateInput()) {
            return;
        }

        String viaPiazza = viaPiazzaEditText.getText().toString().trim();
        String comune = comuneEditText.getText().toString().trim();
        String regione = regioneEditText.getText().toString().trim();
        String paese = paeseEditText.getText().toString().trim();
        String civico = civicoEditText.getText().toString().trim();
        String cap = capEditText.getText().toString().trim();

        loadingProgressBar.setVisibility(View.VISIBLE);
        btnSalva.setEnabled(false);

        gestioneNucleoFamiliareControl.modificaResidenza(viaPiazza, comune, regione, paese, civico, cap, new GenericCallback() {
            @Override
            public void onSuccess(String message) {
                loadingProgressBar.setVisibility(View.GONE);
                btnSalva.setEnabled(true);
                Toast.makeText(VisualizzaNucleoBoundary.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                loadingProgressBar.setVisibility(View.GONE);
                btnSalva.setEnabled(true);
                Toast.makeText(VisualizzaNucleoBoundary.this, getString(R.string.generic_error, message), Toast.LENGTH_LONG).show();
            }
        });
    }
}
