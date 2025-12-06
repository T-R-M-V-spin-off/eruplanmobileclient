package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.adapter.RichiestaAdapter;
import eruplan.unisa.eruplan.entity.RichiestaEntity;

/**
 * Activity che si occupa di visualizzare la lista degli inviti ricevuti (Requisito RF-GNF.06).
 * Scarica i dati reali dal server e li mostra in una RecyclerView.
 */
public class ListaRichiesteBoundary extends AppCompatActivity {

    private ArrayList<RichiestaEntity> listaDati;
    private RichiestaAdapter adapter;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Collegare il layout principale (titolo, RecyclerView vuota, bottone "Torna al Menu").
        setContentView(R.layout.activity_lista_richieste);

        // 2. Configurazione RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRichieste);

        // Obbligatorio per RecyclerView: imposta come disporre gli elementi (verticale).
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inizializza la lista Java come vuota all'inizio
        listaDati = new ArrayList<>();

        // Crea e collega l'adapter personalizzato passandogli la lista vuota.
        adapter = new RichiestaAdapter(listaDati);
        recyclerView.setAdapter(adapter);

        // Trova il pulsante "Torna al Menu" e al click chiude l'activity e torna indietro
        Button btnTornaMenu = findViewById(R.id.btn_torna_menu);
        btnTornaMenu.setOnClickListener(v -> finish());

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        // Scarica i dati reali dal server.
        caricaRichieste();
    }

    /**
     *  Metodo che gestisce la richiesta GET a Volley, analizza il JSON reale e aggiorna la UI.
     */
    private void caricaRichieste() {
        gestioneNucleoFamiliareControl.getRichieste(new GestioneNucleoFamiliareControl.RichiesteControlCallback() {
            @Override
            public void onRichiesteLoaded(List<RichiestaEntity> richieste) {
                listaDati.clear();
                listaDati.addAll(richieste);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onControlError(String message) {
                Toast.makeText(ListaRichiesteBoundary.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
