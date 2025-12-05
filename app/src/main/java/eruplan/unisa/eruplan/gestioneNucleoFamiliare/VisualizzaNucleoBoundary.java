package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
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

        // MODIFICA: Ora apre il menu opzioni
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraMenuOpzioni(v);
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

    private void mostraMenuOpzioni(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        // Aggiungiamo le opzioni: Aggiungi, Rimuovi, Invita
        popup.getMenu().add(0, 1, 0, "Aggiungi membro");
        popup.getMenu().add(0, 2, 0, "Rimuovi membro");
        popup.getMenu().add(0, 3, 0, "Invita membro");

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 3) {
                    apriDialogInvito(); // Apre il dialog
                    return true;
                }
                // Gestione future delle altre voci (aggiungi e rimuovi membro)
                return false;
            }
        });
        popup.show();
    }


    private void apriDialogInvito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        // ROSSA finché Alfonso non crea il layout XML
        View dialogView = inflater.inflate(R.layout.dialog_invita_membro, null);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Binding View (Rossi finché Alfonso non inserisce gli ID nell'XML)
        final EditText etCF = dialogView.findViewById(R.id.et_codice_fiscale_ricerca);
        Button btnCerca = dialogView.findViewById(R.id.btn_cerca_membro);
        final View layoutRisultato = dialogView.findViewById(R.id.layout_risultato);
        final TextView tvNome = dialogView.findViewById(R.id.tv_nome_trovato);
        final TextView tvCognome = dialogView.findViewById(R.id.tv_cognome_trovato);
        final TextView tvCF = dialogView.findViewById(R.id.tv_cf_trovato);
        Button btnInvita = dialogView.findViewById(R.id.btn_conferma_invito);


        // Istanza del Controller
        final GestioneNucleoFamiliareControl controller = new GestioneNucleoFamiliareControl(this);

        // Click su CERCA
        btnCerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cfDigitato = etCF.getText().toString();

                controller.cercaMembroPerInvito(cfDigitato, new GestioneNucleoFamiliareControl.RicercaCallback() {
                    @Override
                    public void onUtenteTrovato(MembroEntity m) {
                        // Mostra i dati trovati
                        layoutRisultato.setVisibility(View.VISIBLE);
                        tvNome.setText("Nome: " + m.getNome());
                        tvCognome.setText("Cognome: " + m.getCognome());
                        tvCF.setText("CF: " + m.getCodiceFiscale());

                        // Blocchiamo l'input per evitare modifiche durante l'invito
                        etCF.setEnabled(false);
                    }

                    @Override
                    public void onErrore(String msg) {
                        // Nascondi risultato se c'è errore
                        layoutRisultato.setVisibility(View.GONE);
                        Toast.makeText(VisualizzaNucleoBoundary.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Click su INVITA
        btnInvita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cfDaInvitare = etCF.getText().toString();

                controller.finalizzaInvito(cfDaInvitare, new GestioneNucleoFamiliareControl.ControlCallback() {
                    @Override
                    public void onInserimentoSuccesso(String message) {
                        dialog.dismiss();
                        Toast.makeText(VisualizzaNucleoBoundary.this, "Invito inviato con successo!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onInserimentoErrore(String message) {
                        Toast.makeText(VisualizzaNucleoBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        dialog.show();
    }
}