package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import eruplan.unisa.eruplan.R;

public class InserisciAppoggioBoundary extends AppCompatActivity {

    private EditText viaPiazzaEditText, civicoEditText, comuneEditText, capEditText, provinciaEditText, regioneEditText, paeseEditText;
    private Button submitButton;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserisci_appoggio);

        viaPiazzaEditText = findViewById(R.id.via_piazza);
        civicoEditText = findViewById(R.id.civico);
        comuneEditText = findViewById(R.id.comune);
        capEditText = findViewById(R.id.cap);
        provinciaEditText = findViewById(R.id.provincia);
        regioneEditText = findViewById(R.id.regione);
        paeseEditText = findViewById(R.id.paese);
        submitButton = findViewById(R.id.submit_button);

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String viaPiazza = viaPiazzaEditText.getText().toString();
                String civico = civicoEditText.getText().toString();
                String comune = comuneEditText.getText().toString();
                String cap = capEditText.getText().toString();
                String provincia = provinciaEditText.getText().toString();
                String regione = regioneEditText.getText().toString();
                String paese = paeseEditText.getText().toString();

                gestioneNucleoFamiliareControl.creaAppoggio(viaPiazza, civico, comune, cap, provincia, regione, paese, new GestioneNucleoFamiliareControl.ControlCallback() {
                    @Override
                    public void onInserimentoSuccesso(String message) {
                        Toast.makeText(InserisciAppoggioBoundary.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onInserimentoErrore(String message) {
                        Toast.makeText(InserisciAppoggioBoundary.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
