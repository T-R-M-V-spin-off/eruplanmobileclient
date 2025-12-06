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

import java.util.ArrayList;
import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.adapter.MembroAdapter;
import eruplan.unisa.eruplan.entity.MembroEntity;

public class VisualizzaNucleoBoundary extends AppCompatActivity {

    private RecyclerView rvMembri;
    private MembroAdapter membroAdapter;
    private List<MembroEntity> membriList;
    private Button btnBack;
    private ImageButton btnMenu;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_nucleo);

        // Inizializzazione View
        rvMembri = findViewById(R.id.rv_membri);
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);

        membriList = new ArrayList<>();
        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

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
        gestioneNucleoFamiliareControl.getMembri(new GestioneNucleoFamiliareControl.MembriControlCallback() {
            @Override
            public void onMembriLoaded(List<MembroEntity> membri) {
                membriList.clear();
                membriList.addAll(membri);
                membroAdapter.notifyDataSetChanged();

                if (membriList.isEmpty()) {
                    Toast.makeText(VisualizzaNucleoBoundary.this, "Nessun membro trovato nel nucleo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onControlError(String message) {
                Toast.makeText(VisualizzaNucleoBoundary.this, message, Toast.LENGTH_LONG).show();
            }
        });
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
                if (item.getItemId() == 1) {
                    gestioneNucleoFamiliareControl.mostraFormAggiungiMembro();
                    return true;
                } else if (item.getItemId() == 3) {
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
