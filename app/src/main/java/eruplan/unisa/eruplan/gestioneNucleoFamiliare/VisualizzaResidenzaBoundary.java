package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.entity.ResidenzaEntity;

public class VisualizzaResidenzaBoundary extends AppCompatActivity {

    private TextView viaPiazzaTextView, comuneTextView, regioneTextView, paeseTextView, civicoTextView, capTextView;
    private Button btnChiudi;
    private ProgressBar loadingProgressBar;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_residenza);

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        initViews();

        btnChiudi.setOnClickListener(v -> {
            Intent intent = new Intent(VisualizzaResidenzaBoundary.this, GestioneNucleoBoundary.class);
            startActivity(intent);
            finish();
        });

        caricaDatiResidenza();
    }

    private void initViews() {
        viaPiazzaTextView = findViewById(R.id.viaPiazzaTextView);
        comuneTextView = findViewById(R.id.comuneTextView);
        regioneTextView = findViewById(R.id.regioneTextView);
        paeseTextView = findViewById(R.id.paeseTextView);
        civicoTextView = findViewById(R.id.civicoTextView);
        capTextView = findViewById(R.id.capTextView);
        btnChiudi = findViewById(R.id.btnChiudi);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
    }

    private void caricaDatiResidenza() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        gestioneNucleoFamiliareControl.getResidenza(new GestioneNucleoFamiliareControl.ResidenzaControlCallback() {
            @Override
            public void onResidenzaLoaded(ResidenzaEntity residenza) {
                loadingProgressBar.setVisibility(View.GONE);
                if (residenza != null) {
                    viaPiazzaTextView.setText(residenza.getViaPiazza());
                    comuneTextView.setText(residenza.getComune());
                    regioneTextView.setText(residenza.getRegione());
                    paeseTextView.setText(residenza.getPaese());
                    civicoTextView.setText(residenza.getCivico());
                    capTextView.setText(residenza.getCap());
                } else {
                    Toast.makeText(VisualizzaResidenzaBoundary.this, "Nessuna residenza trovata.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onControlError(String message) {
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(VisualizzaResidenzaBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
