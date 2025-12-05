package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.VolleySingleton;
import eruplan.unisa.eruplan.adapter.RichiestaAdapter;
import eruplan.unisa.eruplan.entity.RichiestaEntity;

/**
 * Activity che si occupa di visualizzare la lista degli inviti ricevuti (Requisito RF-GNF.06).
 * Scarica i dati reali dal server e li mostra in una RecyclerView.
 */
public class ListaRichiesteBoundary extends AppCompatActivity {

    // A. URL del server Azure (da sostituire con quello reale fornito dal backend).
    private static final String GET_INVITI_URL = "https://eruplanserver.azurewebsites.net/nucleo/inviti";

    // B. Variabili per gestire i dati e il ponte tra dati e UI (Adapter).
    private ArrayList<RichiestaEntity> listaDati;
    private RichiestaAdapter adapter;

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

        //TODO Implementare flusso applicazione

        // Scarica i dati reali dal server tramite Volley al posto dei dati finti.
        caricaRichiesteDalServer();
    }

    /**
     *  Metodo che gestisce la richiesta GET a Volley, analizza il JSON reale e aggiorna la UI.
     */
    private void caricaRichiesteDalServer() {

        // Crea una richiesta GET. Ci aspettiamo un Array JSON [] dal server Azure.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_INVITI_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Se il server risponde con successo:
                        try {
                            // 1. Puliamo la lista attuale per evitare duplicati se il metodo viene richiamato
                            listaDati.clear();

                            // 2. Ciclo per leggere ogni elemento dell'Array JSON ricevuto []
                            for (int i = 0; i < response.length(); i++) {
                                // Prendiamo l'oggetto JSON alla posizione i
                                JSONObject jsonObject = response.getJSONObject(i);

                                // 3. Estraiamo i campi reali basati sulle chiavi JSON del backend (accordarsi con il backend).
                                String mittente = jsonObject.getString("nomeMittente").trim();
                                String data = jsonObject.getString("dataOra").trim();

                                // 4. Creiamo l'oggetto Richiesta vero e lo aggiungiamo alla lista Java
                                listaDati.add(new RichiestaEntity(mittente, data));
                            }

                            // 5. Agggiorna la UI usando l'adapter.
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            // Errore nel formato del JSON ricevuto (es. chiavi mancanti o errate)
                            Toast.makeText(ListaRichiesteBoundary.this, "Errore parsing dati", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    // Se c'è un errrore di rete
                    Toast.makeText(ListaRichiesteBoundary.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                }
        );

        // Usa la classe Singleton per inviare la richiesta gestendo i cookie (JSESSIONID).
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }
}