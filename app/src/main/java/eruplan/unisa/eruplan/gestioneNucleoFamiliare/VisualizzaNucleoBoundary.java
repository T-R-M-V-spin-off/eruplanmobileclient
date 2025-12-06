package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.app.AlertDialog;
import android.content.Context;
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

public class VisualizzaNucleoBoundary extends AppCompatActivity implements MembroAdapter.OnItemDeleteListener {

    private RecyclerView rvMembri;
    private MembroAdapter membroAdapter;
    private List<MembroEntity> membriList;
    private Button btnBack;
    private ImageButton btnMenu;
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_nucleo);

        rvMembri = findViewById(R.id.rv_membri);
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);

        membriList = new ArrayList<>();
        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        rvMembri.setLayoutManager(new LinearLayoutManager(this));
        membroAdapter = new MembroAdapter(membriList, this);
        rvMembri.setAdapter(membroAdapter);

        loadMembri();

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(VisualizzaNucleoBoundary.this, GestioneNucleoBoundary.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnMenu.setOnClickListener(this::mostraMenuOpzioni);
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

    private void mostraMenuOpzioni(View anchor) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_opzioni_nucleo, null);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        Button btnAggiungi = popupView.findViewById(R.id.btn_aggiungi_membro_menu);
        Button btnRimuovi = popupView.findViewById(R.id.btn_rimuovi_membro_menu);
        Button btnInvita = popupView.findViewById(R.id.btn_invita_membro_menu);

        btnAggiungi.setOnClickListener(v -> {
            gestioneNucleoFamiliareControl.mostraFormAggiungiMembro();
            popupWindow.dismiss();
        });

        btnRimuovi.setOnClickListener(v -> {
            membroAdapter.setShowDeleteIcon(true);
            popupWindow.dismiss();
        });

        btnInvita.setOnClickListener(v -> {
            apriDialogInvito();
            popupWindow.dismiss();
        });

        popupWindow.showAsDropDown(anchor);
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
                            Toast.makeText(VisualizzaNucleoBoundary.this, msg, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(VisualizzaNucleoBoundary.this, "Invito inviato con successo!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onInserimentoErrore(String message) {
                    Toast.makeText(VisualizzaNucleoBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onItemDelete(int position) {
        MembroEntity membro = membriList.get(position);
        gestioneNucleoFamiliareControl.rimuoviMembro(membro.getCodiceFiscale(), new GestioneNucleoFamiliareControl.ControlCallback() {
            @Override
            public void onInserimentoSuccesso(String message) {
                Toast.makeText(VisualizzaNucleoBoundary.this, message, Toast.LENGTH_SHORT).show();
                membriList.remove(position);
                membroAdapter.notifyItemRemoved(position);
                membroAdapter.setShowDeleteIcon(false);
            }

            @Override
            public void onInserimentoErrore(String message) {
                Toast.makeText(VisualizzaNucleoBoundary.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
