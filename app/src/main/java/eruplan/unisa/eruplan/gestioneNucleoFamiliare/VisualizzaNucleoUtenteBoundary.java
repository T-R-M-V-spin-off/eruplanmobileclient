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
import eruplan.unisa.eruplan.callback.NucleoCallback;
import eruplan.unisa.eruplan.entity.NucleoEntity;

public class VisualizzaNucleoUtenteBoundary extends AppCompatActivity {

    private TextView viaPiazzaTextView, comuneTextView, regioneTextView, paeseTextView, civicoTextView, capTextView;
    private Button btnChiudi;
    private ProgressBar loadingProgressBar;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_nucleo_utente);

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        initViews();

        btnChiudi.setOnClickListener(v -> {
            Intent intent = new Intent(VisualizzaNucleoUtenteBoundary.this, GestioneNucleoBoundary.class);
            startActivity(intent);
            finish();
        });

        caricaDatiNucleo();
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

    private void caricaDatiNucleo() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        gestioneNucleoFamiliareControl.getNucleo(new NucleoCallback() {
            @Override
            public void onNucleoLoaded(NucleoEntity nucleo) {
                loadingProgressBar.setVisibility(View.GONE);
                if (nucleo != null) {
                    viaPiazzaTextView.setText(nucleo.getViaPiazza());
                    comuneTextView.setText(nucleo.getComune());
                    regioneTextView.setText(nucleo.getRegione());
                    paeseTextView.setText(nucleo.getPaese());
                    civicoTextView.setText(nucleo.getCivico());
                    capTextView.setText(nucleo.getCap());
                } else {
                    Toast.makeText(VisualizzaNucleoUtenteBoundary.this, "Nessun nucleo trovato.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String message) {
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(VisualizzaNucleoUtenteBoundary.this, getString(R.string.generic_error, message), Toast.LENGTH_LONG).show();
            }
        });
    }
}
