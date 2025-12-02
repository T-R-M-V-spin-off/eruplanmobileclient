package eruplan.unisa.eruplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VisualizzaNucleoActivity extends AppCompatActivity {

    private RecyclerView rvMembri;
    private MembroAdapter membroAdapter;
    private List<Membro> membriList;
    private Button btnBack;
    private ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_nucleo);

        // Inizializzazione View
        rvMembri = findViewById(R.id.rv_membri);
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);

        // Configurazione RecyclerView
        rvMembri.setLayoutManager(new LinearLayoutManager(this));
        
        // Caricamento dati (simulato)
        loadMembri();

        // Impostazione Adapter
        membroAdapter = new MembroAdapter(membriList);
        rvMembri.setAdapter(membroAdapter);

        // Listener Pulsanti
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Torna al menu GNF (GnfActivity)
                Intent intent = new Intent(VisualizzaNucleoActivity.this, GnfActivity.class);
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
        membriList = new ArrayList<>();
        // Dati di prova - Sostituire con chiamata al backend/database
        membriList.add(new Membro("Mario", "Rossi", "RSSMRA80A01H501U", "01/01/1980", "M", false, false));
        membriList.add(new Membro("Anna", "Verdi", "VRDNNA85B45H501Z", "15/02/1985", "F", false, false));
        membriList.add(new Membro("Luca", "Rossi", "RSSLCA10C10H501X", "10/03/2010", "M", false, true));
    }
}