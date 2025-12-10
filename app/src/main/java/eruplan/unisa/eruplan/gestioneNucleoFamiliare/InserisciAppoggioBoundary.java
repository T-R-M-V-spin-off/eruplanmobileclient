package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.callback.GenericCallback;

public class InserisciAppoggioBoundary extends AppCompatActivity {

    private EditText viaPiazzaEditText, civicoEditText, comuneEditText, capEditText, provinciaEditText, regioneEditText, paeseEditText;
    private Button submitButton;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserisci_appoggio);

        initViews();

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        submitButton.setOnClickListener(v -> inserisciAppoggio());
    }

    private void initViews() {
        viaPiazzaEditText = findViewById(R.id.via_piazza);
        civicoEditText = findViewById(R.id.civico);
        comuneEditText = findViewById(R.id.comune);
        capEditText = findViewById(R.id.cap);
        provinciaEditText = findViewById(R.id.provincia);
        regioneEditText = findViewById(R.id.regione);
        paeseEditText = findViewById(R.id.paese);
        submitButton = findViewById(R.id.submit_button);
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(viaPiazzaEditText.getText())) {
            viaPiazzaEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(civicoEditText.getText())) {
            civicoEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(comuneEditText.getText())) {
            comuneEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(capEditText.getText())) {
            capEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(provinciaEditText.getText())) {
            provinciaEditText.setError(getString(R.string.empty_field_error));
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
        return true;
    }

    private void inserisciAppoggio() {
        if (!validateInput()) {
            return;
        }

        String viaPiazza = viaPiazzaEditText.getText().toString().trim();
        String civico = civicoEditText.getText().toString().trim();
        String comune = comuneEditText.getText().toString().trim();
        String cap = capEditText.getText().toString().trim();
        String provincia = provinciaEditText.getText().toString().trim();
        String regione = regioneEditText.getText().toString().trim();
        String paese = paeseEditText.getText().toString().trim();

        gestioneNucleoFamiliareControl.creaAppoggio(viaPiazza, civico, comune, cap, provincia, regione, paese, new GenericCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(InserisciAppoggioBoundary.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(InserisciAppoggioBoundary.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
