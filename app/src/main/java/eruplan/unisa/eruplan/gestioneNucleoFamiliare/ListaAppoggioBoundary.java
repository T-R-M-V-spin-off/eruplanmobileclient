package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.adapter.AppoggioAdapter;
import eruplan.unisa.eruplan.entity.AppoggioEntity;

public class ListaAppoggioBoundary extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppoggioAdapter adapter;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_appoggi);

        recyclerView = findViewById(R.id.appoggi_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button addAppoggioButton = findViewById(R.id.add_appoggio_button);

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        addAppoggioButton.setOnClickListener(v -> { gestioneNucleoFamiliareControl.mostraFormCreaAppoggio();
        });

        loadAppoggi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppoggi();
    }

    private void loadAppoggi() {
        gestioneNucleoFamiliareControl.getAppoggi(new GestioneNucleoFamiliareControl.AppoggiControlCallback() {
            @Override
            public void onAppoggiLoaded(List<AppoggioEntity> appoggi) {
                adapter = new AppoggioAdapter(appoggi, appoggioToDelete -> eliminaAppoggio(appoggioToDelete.getId()));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onControlError(String message) {
                Toast.makeText(ListaAppoggioBoundary.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminaAppoggio(long appoggioId) {
        gestioneNucleoFamiliareControl.eliminaAppoggio(appoggioId, new GestioneNucleoFamiliareControl.ControlCallback() {
            @Override
            public void onInserimentoSuccesso(String message) {
                Toast.makeText(ListaAppoggioBoundary.this, message, Toast.LENGTH_SHORT).show();
                loadAppoggi(); // Ricarica la lista dopo l'eliminazione
            }

            @Override
            public void onInserimentoErrore(String message) {
                Toast.makeText(ListaAppoggioBoundary.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
