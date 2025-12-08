package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.adapter.RichiestaAdapter;
import eruplan.unisa.eruplan.callback.GenericCallback;
import eruplan.unisa.eruplan.callback.RichiesteCallback;
import eruplan.unisa.eruplan.entity.RichiestaEntity;

public class ListaRichiesteBoundary extends AppCompatActivity implements RichiestaAdapter.OnItemSelectListener {

    private ArrayList<RichiestaEntity> listaDati;
    private RichiestaAdapter adapter;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    private RichiestaEntity richiestaSelezionata = null;

    private MaterialButton btnConferma;
    private MaterialButton btnIndietro;
    private MaterialButton btnTornaMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_richieste);

        btnConferma = findViewById(R.id.btn_conferma);
        btnIndietro = findViewById(R.id.btn_indietro);
        btnTornaMenu = findViewById(R.id.btn_torna_menu);

        boolean isActionable = getIntent().getBooleanExtra("IS_ACTIONABLE", false);

        if (isActionable) {
            btnConferma.setVisibility(View.VISIBLE);
            btnConferma.setEnabled(false);
            btnIndietro.setVisibility(View.VISIBLE);
            btnTornaMenu.setVisibility(View.GONE);

            btnIndietro.setOnClickListener(v -> finish());
            btnConferma.setOnClickListener(v -> {
                if (richiestaSelezionata != null) {
                    accettaRichiesta(richiestaSelezionata);
                }
            });

        } else {
            btnConferma.setVisibility(View.GONE);
            btnIndietro.setVisibility(View.GONE);
            btnTornaMenu.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRichieste);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaDati = new ArrayList<>();

        adapter = new RichiestaAdapter(listaDati, this);
        recyclerView.setAdapter(adapter);

        btnTornaMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ListaRichiesteBoundary.this, GestioneNucleoBoundary.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        caricaRichieste();
    }

    private void caricaRichieste() {
        gestioneNucleoFamiliareControl.getRichieste(new RichiesteCallback() {
            @Override
            public void onRichiesteLoaded(List<RichiestaEntity> richieste) {
                listaDati.clear();
                listaDati.addAll(richieste);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ListaRichiesteBoundary.this, getString(R.string.generic_error, message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(RichiestaEntity richiesta) {
        this.richiestaSelezionata = richiesta;
        if (btnConferma.getVisibility() == View.VISIBLE) {
            btnConferma.setEnabled(true);
        }
    }

    private void accettaRichiesta(RichiestaEntity richiesta) {
        gestioneNucleoFamiliareControl.accettaRichiesta(richiesta.getId(), new GenericCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(ListaRichiesteBoundary.this, message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ListaRichiesteBoundary.this, GestioneNucleoBoundary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ListaRichiesteBoundary.this, getString(R.string.repo_accept_request_error, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
