package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.adapter.RichiestaAdapter;
import eruplan.unisa.eruplan.entity.RichiestaEntity;

/**
 * Activity che si occupa di visualizzare la lista degli inviti ricevuti (Requisito RF-GNF.06).
 * Scarica i dati reali dal server e li mostra in una RecyclerView.
 * IMPLEMENTA RichiestaAdapter.OnItemSelectListener per ascoltare i click sulle righe.
 */
public class ListaRichiesteBoundary extends AppCompatActivity implements RichiestaAdapter.OnItemSelectListener {

    private ArrayList<RichiestaEntity> listaDati;
    private RichiestaAdapter adapter;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    // Riferimento alla richiesta attualmente selezionata dall'utente
    private RichiestaEntity richiestaSelezionata = null;

    // Riferimenti ai 3 bottoni
    private Button btnConferma;
    private Button btnIndietro;
    private Button btnTornaMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Collegare il layout principale (titolo, RecyclerView vuota, bottone "Torna al Menu").
        setContentView(R.layout.activity_lista_richieste);

        // Recupero i bottoni (conferma e indietro vanno aggiunti all'xml)
        // btnConferma = findViewById(R.id.btn_conferma);
        // btnIndietro = findViewById(R.id.btn_indietro);
        btnTornaMenu = findViewById(R.id.btn_torna_menu);

        // Controllo se questa pagina è stata aperta in modalità "Accettazione" o "Sola Lettura"
        // Chi chiama questa activity dovrà passare true/false. Default false (Sola lettura).
        boolean isActionable = getIntent().getBooleanExtra("IS_ACTIONABLE", false);

        if (isActionable) {
            // MODALITÀ ACCETTAZIONE (Vengo da "Cosa desideri fare")
            btnConferma.setVisibility(View.VISIBLE);
            // All'inizio è disabilitato perché nessuna riga è selezionata
            btnConferma.setEnabled(false);

            // Mostro il tasto indietro e nascondo quello per tornare al menu
            btnIndietro.setVisibility(View.VISIBLE);
            btnTornaMenu.setVisibility(View.GONE);

            // Click su Indietro -> chiude activity; Click su Conferma -> avvia accettazione
            btnIndietro.setOnClickListener(v -> finish());
            btnConferma.setOnClickListener(v -> {
                if (richiestaSelezionata != null) {
                    accettaRichiesta(richiestaSelezionata);
                }
            });

        } else {
            // MODALITÀ SOLA LETTURA (Vengo dal Menu)
            // Nascondo "Conferma" e "Indietro" e rendo visibile "Torna al Menu"
            btnConferma.setVisibility(View.GONE);
            btnIndietro.setVisibility(View.GONE);
            btnTornaMenu.setVisibility(View.VISIBLE);
        }

        // 2. Configurazione RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRichieste);

        // Obbligatorio per RecyclerView: imposta come disporre gli elementi (verticale).
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inizializza la lista Java come vuota all'inizio
        listaDati = new ArrayList<>();

        // Crea e collega l'adapter personalizzato.
        // NOTA: Passo 'this' perché questa Activity ora implementa l'interfaccia OnItemSelectListener
        adapter = new RichiestaAdapter(listaDati, this);
        recyclerView.setAdapter(adapter);

        // Al click su "Torna al Menu" chiude l'activity e torna indietro
        btnTornaMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ListaRichiesteBoundary.this, GestioneNucleoBoundary.class);
            // Flag per evitare di creare activity multiple nello stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

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

    // Implementazione del metodo dell'interfaccia OnItemSelectListener
    @Override
    public void onItemSelected(RichiestaEntity richiesta) {
        this.richiestaSelezionata = richiesta;

        // Se il bottone conferma è visibile (siamo in modalità accettazione), lo abilitiamo
        if (btnConferma.getVisibility() == View.VISIBLE) {
            btnConferma.setEnabled(true);
        }
    }

    // Metodo per gestire la logica di accettazione tramite il Control
    private void accettaRichiesta(RichiestaEntity richiesta) {
        gestioneNucleoFamiliareControl.accettaRichiesta(richiesta.getId(), new GestioneNucleoFamiliareControl.AccettaRichiestaCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(ListaRichiesteBoundary.this, "Benvenuto nel nucleo!", Toast.LENGTH_LONG).show();

                // Dopo l'accettazione, reindirizziamo al menu del nucleo (GestioneNucleoBoundary)
                Intent intent = new Intent(ListaRichiesteBoundary.this, GestioneNucleoBoundary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ListaRichiesteBoundary.this, "Errore durante l'accettazione: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
