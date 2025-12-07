package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
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

public class VisualizzaMembriBoundary extends AppCompatActivity {

    private RecyclerView rvMembri;
    private MembroAdapter membroAdapter;
    private List<MembroEntity> membriList;
    private Button btnBack;
    private ImageButton btnMenu;
    private GestioneNucleoFamiliareControl control;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_membri);

        // Inizializza il control
        control = new GestioneNucleoFamiliareControl(this);

        // Inizializzazione View
        rvMembri = findViewById(R.id.rv_membri);
        btnBack = findViewById(R.id.btn_back);
        //
        //Solo se sei admin visualizzi questa view
        //
        btnMenu = findViewById(R.id.btn_menu);

        // Configurazione RecyclerView
        rvMembri.setLayoutManager(new LinearLayoutManager(this));

        // Inizializza lista vuota e adapter
        membriList = new ArrayList<>();
        membroAdapter = new MembroAdapter(membriList);
        
        // Impostiamo il listener per l'eliminazione
        membroAdapter.setOnItemDeleteListener(position -> {
            MembroEntity membroDaRimuovere = membriList.get(position);
            rimuoviMembro(membroDaRimuovere, position);
        });
        
        // Di default, l'icona di cancellazione è nascosta
        membroAdapter.setShowDeleteIcon(false);
        
        rvMembri.setAdapter(membroAdapter);

        // Caricamento dati reali dal backend
        caricaMembri();

        // Listener Pulsanti
        btnBack.setOnClickListener(v -> {
            // Torna al menu Gestione_Nucleo_Boundary
            Intent intent = new Intent(VisualizzaMembriBoundary.this, GestioneNucleoBoundary.class);
            startActivity(intent);
            finish();// Chiude semplicemente questa activity tornando a quella precedente nello stack
        });

        // Il tasto menu (se presente nel layout)
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                showPopupMenu(v);
            });
        }
    }

    private void showPopupMenu(View anchor) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_opzioni_nucleo, null);

        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAsDropDown(anchor);

        Button btnAggiungi = popupView.findViewById(R.id.btn_aggiungi_membro_menu);
        btnAggiungi.setOnClickListener(v -> {
            control.mostraAggiungiMembro();
            popupWindow.dismiss();
        });

        Button btnRimuovi = popupView.findViewById(R.id.btn_rimuovi_membro_menu);
        btnRimuovi.setOnClickListener(v -> {
            // Mostra le icone di cancellazione per permettere la rimozione
            membroAdapter.setShowDeleteIcon(true);
            membroAdapter.notifyDataSetChanged();
            popupWindow.dismiss();
        });

        Button btnInvita = popupView.findViewById(R.id.btn_invita_membro_menu);
        btnInvita.setOnClickListener(v -> {

            // Aggiungi qui la logica per invitare un membro, se necessaria

            apriDialogInvito();
            popupWindow.dismiss();

        });
    }

    private void caricaMembri() {
        control.getMembri(new GestioneNucleoFamiliareControl.MembriControlCallback() {
            @Override
            public void onMembriLoaded(List<MembroEntity> membri) {
                membriList.clear();
                if (membri != null) {
                    membriList.addAll(membri);
                }
                membroAdapter.notifyDataSetChanged();
                
                if (membriList.isEmpty()) {
                    Toast.makeText(VisualizzaMembriBoundary.this, "Nessun membro trovato nel nucleo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onControlError(String message) {
                Toast.makeText(VisualizzaMembriBoundary.this, "Errore caricamento membri: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void rimuoviMembro(MembroEntity membro, int position) {
        control.rimuoviMembro(membro.getCodiceFiscale(), new GestioneNucleoFamiliareControl.ControlCallback() {
            @Override
            public void onInserimentoSuccesso(String message) {
                Toast.makeText(VisualizzaMembriBoundary.this, message, Toast.LENGTH_SHORT).show();
                // Rimuoviamo dalla lista locale e aggiorniamo la UI
                membriList.remove(position);
                membroAdapter.notifyItemRemoved(position);
                membroAdapter.notifyItemRangeChanged(position, membriList.size());
            }

            @Override
            public void onInserimentoErrore(String message) {
                Toast.makeText(VisualizzaMembriBoundary.this, "Errore eliminazione: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void apriDialogInvito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_invita_membro, null);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        final EditText etCF = dialogView.findViewById(R.id.et_codice_fiscale_ricerca);
        final View layoutRisultato = dialogView.findViewById(R.id.layout_risultato);
        final TextView tvNome = dialogView.findViewById(R.id.tv_nome_trovato);
        final TextView tvCognome = dialogView.findViewById(R.id.tv_cognome_trovato);
        final TextView tvCF = dialogView.findViewById(R.id.tv_cf_trovato);
        Button btnInvita = dialogView.findViewById(R.id.btn_conferma_invito);

        final GestioneNucleoFamiliareControl controller = new GestioneNucleoFamiliareControl(this);

        etCF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 16) {
                    controller.cercaMembroPerInvito(s.toString(), new GestioneNucleoFamiliareControl.RicercaCallback() {
                        @Override
                        public void onUtenteTrovato(MembroEntity m) {
                            layoutRisultato.setVisibility(View.VISIBLE);
                            tvNome.setText("Nome: " + m.getNome());
                            tvCognome.setText("Cognome: " + m.getCognome());
                            tvCF.setText("CF: " + m.getCodiceFiscale());
                            etCF.setEnabled(false);
                        }

                        @Override
                        public void onErrore(String msg) {
                            layoutRisultato.setVisibility(View.GONE);
                            Toast.makeText(VisualizzaMembriBoundary.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    layoutRisultato.setVisibility(View.GONE);
                }
            }
        });

        btnInvita.setOnClickListener(v -> {
            String cfDaInvitare = etCF.getText().toString();
            controller.finalizzaInvito(cfDaInvitare, new GestioneNucleoFamiliareControl.ControlCallback() {
                @Override
                public void onInserimentoSuccesso(String message) {
                    dialog.dismiss();
                    Toast.makeText(VisualizzaMembriBoundary.this, "Invito inviato con successo!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onInserimentoErrore(String message) {
                    Toast.makeText(VisualizzaMembriBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

}
