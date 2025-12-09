package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.callback.GenericCallback;

public class CreaNucleoBoundary extends AppCompatActivity {

    private EditText viaPiazzaEditText, comuneEditText, regioneEditText, paeseEditText, civicoEditText, capEditText, postiVeicoloEditText;
    private Button btnSubmitNucleo;
    private ProgressBar loadingProgressBarNucleo;
    private CheckBox hasVeicoloCheckBox;
    private TextInputLayout postiVeicoloInputLayout;

    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_nucleo);

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        initViews();

        hasVeicoloCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            postiVeicoloInputLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
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
        postiVeicoloInputLayout = findViewById(R.id.postiVeicoloInputLayout);
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
        if (hasVeicoloCheckBox.isChecked() && TextUtils.isEmpty(postiVeicoloEditText.getText())) {
            postiVeicoloEditText.setError(getString(R.string.error_empty_vehicle_seats));
            return false;
        }
        return true;
    }

    private void submitNucleo() {
        if (!validateInput()) {
            return;
        }

        String viaPiazza = viaPiazzaEditText.getText().toString().trim();
        String comune = comuneEditText.getText().toString().trim();
        String regione = regioneEditText.getText().toString().trim();
        String paese = paeseEditText.getText().toString().trim();
        String civico = civicoEditText.getText().toString().trim();
        String cap = capEditText.getText().toString().trim();
        boolean hasVeicolo = hasVeicoloCheckBox.isChecked();
        int postiVeicolo = 0;

        if (hasVeicolo) {
            try {
                postiVeicolo = Integer.parseInt(postiVeicoloEditText.getText().toString().trim());
            } catch (NumberFormatException e) {
                postiVeicoloEditText.setError(getString(R.string.error_invalid_vehicle_seats));
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
                Intent intent = new Intent(CreaNucleoBoundary.this, GestioneNucleoBoundary.class);
                intent.putExtra("IS_ACTIONABLE", true);
                startActivity(intent);

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
