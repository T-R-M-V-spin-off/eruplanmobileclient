package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.app.AlertDialog;
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

        control = new GestioneNucleoFamiliareControl(this);

        initViews();
        setupRecyclerView();
        setupListeners();

        caricaMembri();
    }

    private void initViews() {
        rvMembri = findViewById(R.id.rv_membri);
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);
    }

    private void setupRecyclerView() {
        rvMembri.setLayoutManager(new LinearLayoutManager(this));
        membriList = new ArrayList<>();
        membroAdapter = new MembroAdapter(membriList);
        membroAdapter.setOnItemDeleteListener(position -> {
            MembroEntity membroDaRimuovere = membriList.get(position);
            rimuoviMembro(membroDaRimuovere, position);
        });
        membroAdapter.setShowDeleteIcon(false);
        rvMembri.setAdapter(membroAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> control.tornaAllaGestioneNucleo());

        if (btnMenu != null) {
            btnMenu.setOnClickListener(this::showPopupMenu);
        }
    }

    private void showPopupMenu(View anchor) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_opzioni_nucleo, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.showAsDropDown(anchor);

        popupView.findViewById(R.id.btn_aggiungi_membro_menu).setOnClickListener(v -> {
            control.mostraAggiungiMembro();
            popupWindow.dismiss();
        });

        popupView.findViewById(R.id.btn_rimuovi_membro_menu).setOnClickListener(v -> {
            membroAdapter.setShowDeleteIcon(true);
            membroAdapter.notifyDataSetChanged();
            popupWindow.dismiss();
        });

        popupView.findViewById(R.id.btn_invita_membro_menu).setOnClickListener(v -> {
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
            public void onError(String message) {
                Toast.makeText(VisualizzaMembriBoundary.this, "Errore caricamento membri: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void rimuoviMembro(MembroEntity membro, int position) {
        control.rimuoviMembro(membro.getCodiceFiscale(), new GestioneNucleoFamiliareControl.ControlCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(VisualizzaMembriBoundary.this, message, Toast.LENGTH_SHORT).show();
                membriList.remove(position);
                membroAdapter.notifyItemRemoved(position);
                membroAdapter.notifyItemRangeChanged(position, membriList.size());
            }

            @Override
            public void onError(String message) {
                Toast.makeText(VisualizzaMembriBoundary.this, "Errore eliminazione: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void apriDialogInvito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_invita_membro, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        final EditText etCF = dialogView.findViewById(R.id.et_codice_fiscale_ricerca);
        final View layoutRisultato = dialogView.findViewById(R.id.layout_risultato);
        final TextView tvNome = dialogView.findViewById(R.id.tv_nome_trovato);
        final TextView tvCognome = dialogView.findViewById(R.id.tv_cognome_trovato);
        final TextView tvCF = dialogView.findViewById(R.id.tv_cf_trovato);
        Button btnInvita = dialogView.findViewById(R.id.btn_conferma_invito);

        etCF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 16) {
                    control.cercaUtentePerInvito(s.toString(), new GestioneNucleoFamiliareControl.RicercaCallback() {
                        @Override
                        public void onUtenteTrovato(MembroEntity m) {
                            layoutRisultato.setVisibility(View.VISIBLE);
                            tvNome.setText("Nome: " + m.getNome());
                            tvCognome.setText("Cognome: " + m.getCognome());
                            tvCF.setText("CF: " + m.getCodiceFiscale());
                            etCF.setEnabled(false);
                        }

                        @Override
                        public void onError(String msg) {
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
            control.finalizzaInvito(cfDaInvitare, new GestioneNucleoFamiliareControl.ControlCallback() {
                @Override
                public void onSuccess(String message) {
                    dialog.dismiss();
                    Toast.makeText(VisualizzaMembriBoundary.this, "Invito inviato con successo!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(VisualizzaMembriBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }
}
