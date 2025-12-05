package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.VolleySingleton;
import eruplan.unisa.eruplan.adapter.MembroAdapter;
import eruplan.unisa.eruplan.entity.MembroEntity;

public class VisualizzaNucleoBoundary extends AppCompatActivity {

    private RecyclerView rvMembri;
    private MembroAdapter membroAdapter;
    private List<MembroEntity> membriList;
    private Button btnBack;
    private ImageButton btnMenu;
    
    // URL per ottenere i membri
    private static final String GET_MEMBERS_URL = "https://eruplanserver.azurewebsites.net/sottosistema/membri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_nucleo);

        // Inizializzazione View
        rvMembri = findViewById(R.id.rv_membri);
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);

        membriList = new ArrayList<>();

        // Configurazione RecyclerView
        rvMembri.setLayoutManager(new LinearLayoutManager(this));
        membroAdapter = new MembroAdapter(membriList);
        rvMembri.setAdapter(membroAdapter);
        
        // Caricamento dati dal server
        loadMembri();

        // Listener Pulsanti
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Torna al menu GNF (GnfActivity)
                Intent intent = new Intent(VisualizzaNucleoBoundary.this, GestioneNucleoBoundary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
        
        btnMenu.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 // TODO: Implementare apertura menu laterale (PER GLI ADMIN)
             }
        });
    }

    private void loadMembri() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_MEMBERS_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        membriList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                MembroEntity m = new MembroEntity();
                                m.setNome(obj.optString("nome"));
                                m.setCognome(obj.optString("cognome"));
                                m.setCodiceFiscale(obj.optString("codiceFiscale"));
                                m.setDataDiNascita(obj.optString("dataDiNascita"));
                                m.setSesso(obj.optString("sesso"));
                                m.setAssistenza(obj.optBoolean("assistenza"));
                                m.setMinorenne(obj.optBoolean("minorenne"));
                                membriList.add(m);
                            }
                            membroAdapter.notifyDataSetChanged();
                            
                            if (membriList.isEmpty()) {
                                Toast.makeText(VisualizzaNucleoBoundary.this, "Nessun membro trovato nel nucleo.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(VisualizzaNucleoBoundary.this, "Errore nel parsing dei dati: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         String errorMsg = "Errore nel caricamento: " + error.getMessage();
                         Toast.makeText(VisualizzaNucleoBoundary.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

        // Usa il Singleton
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }
}