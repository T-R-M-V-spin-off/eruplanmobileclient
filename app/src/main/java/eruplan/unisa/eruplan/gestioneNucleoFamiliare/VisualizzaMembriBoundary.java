package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
        setContentView(R.layout.activity_visualizza_nucleo);

        // Inizializza il control
        control = new GestioneNucleoFamiliareControl(this);

        // Inizializzazione View
        rvMembri = findViewById(R.id.rv_membri);
        btnBack = findViewById(R.id.btn_back);
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
        
        // Di default, mostriamo l'icona delete. Puoi cambiare logica se serve (es. solo capofamiglia)
        membroAdapter.setShowDeleteIcon(true);
        
        rvMembri.setAdapter(membroAdapter);

        // Caricamento dati reali dal backend
        caricaMembri();

        // Listener Pulsanti
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Torna al menu GNF
                finish(); // Chiude semplicemente questa activity tornando a quella precedente nello stack
            }
        });

        // Il tasto menu (se presente nel layout)
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                // Logica menu se necessaria
            });
        }
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
}
