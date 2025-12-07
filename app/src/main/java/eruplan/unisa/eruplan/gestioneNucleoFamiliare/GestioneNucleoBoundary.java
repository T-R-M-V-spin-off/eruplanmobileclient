package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Flow;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import eruplan.unisa.eruplan.gestioneUtenteMobile.GestioneUtenteControl;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.gestioneUtenteMobile.LoginBoundary;


public class GestioneNucleoBoundary extends AppCompatActivity {

    // Riferimenti ai pulsanti nel layout
    private MaterialButton btnSi;
    private MaterialButton btnNo;
    private TextView tvConferma;
    private LinearLayout layoutConfermaAzioni;
    private Flow buttonFlow;

    // Controller
    private GestioneNucleoFamiliareControl gestioneNucleoControl;
    private GestioneUtenteControl gestioneUtenteControl;

    private static final String TAG_FCM = "FCM_DEBUG";
    private static final String TOPIC_EMERGENZA = "emergenza";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnf);

        // 1. Inizializzazione dei Control
        gestioneNucleoControl = new GestioneNucleoFamiliareControl(getApplicationContext());
        gestioneUtenteControl = new GestioneUtenteControl(this);

        // Inizializzazione Views
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        MaterialButton btnMembri = findViewById(R.id.btnMembri);
        MaterialButton btnLuoghi = findViewById(R.id.btnLuoghi);
        MaterialButton btnResidenza = findViewById(R.id.btnResidenza);
        MaterialButton btnAbbandona = findViewById(R.id.btnAbbandona);
        MaterialButton btnInviti = findViewById(R.id.btnInviti);
        MaterialButton btnGestioneEmergenza = findViewById(R.id.btnEmergenza);

        btnSi = findViewById(R.id.btnSi);
        btnNo = findViewById(R.id.btnNo);
        tvConferma = findViewById(R.id.tvConferma);
        layoutConfermaAzioni = findViewById(R.id.layoutConfermaAzioni);
        buttonFlow = findViewById(R.id.button_flow);

        subscribeToNotificationTopic();

        btnMembri.setOnClickListener(v -> gestioneNucleoControl.mostraVisualizzaMembri());
        btnLuoghi.setOnClickListener(v -> gestioneNucleoControl.apriListaAppoggio());

        //Listneer per il pulsante "Il tuo Piano di Emergenza"
        btnGestioneEmergenza.setOnClickListener(v -> gestioneNucleoControl.mostraGestioneEmergenza());

        btnResidenza.setOnClickListener(v -> gestioneNucleoControl.mostraVisualizzaNucleo());
        btnInviti.setOnClickListener(v -> gestioneNucleoControl.mostraListaRichieste());

        btnLogout.setOnClickListener(v -> performLogout());

        btnAbbandona.setOnClickListener(v -> mostraSezioneConferma(true));

        btnSi.setOnClickListener(v -> eseguiAbbandonoNucleo());

        btnNo.setOnClickListener(v -> mostraSezioneConferma(false));
    }

    private void performLogout() {
        gestioneUtenteControl.logout(new GestioneUtenteControl.OperationCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(GestioneNucleoBoundary.this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GestioneNucleoBoundary.this, LoginBoundary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void subscribeToNotificationTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_EMERGENZA)
                .addOnCompleteListener(task -> {
                    String msg = "Iscrizione al Topic '" + TOPIC_EMERGENZA + "' riuscita.";
                    if (!task.isSuccessful()) {
                        msg = "ERRORE: Iscrizione al Topic fallita.";
                        Log.e(TAG_FCM, msg, task.getException());
                    } else {
                        Log.d(TAG_FCM, msg);
                    }
                });
    }

    private void mostraSezioneConferma(boolean mostra) {
        int visibility = mostra ? View.VISIBLE : View.GONE;
        int gridVisibility = mostra ? View.GONE : View.VISIBLE;

        tvConferma.setVisibility(visibility);
        layoutConfermaAzioni.setVisibility(visibility);
        buttonFlow.setVisibility(gridVisibility);
    }

    private void eseguiAbbandonoNucleo() {
        btnSi.setEnabled(false);
        btnNo.setEnabled(false);

        gestioneNucleoControl.abbandonaNucleo(new GestioneNucleoFamiliareControl.ControlCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(GestioneNucleoBoundary.this, message, Toast.LENGTH_LONG).show();
                performLogout(); // Esegui il logout dopo aver abbandonato il nucleo
            }

            @Override
            public void onError(String message) {
                mostraSezioneConferma(false);
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                btnSi.setEnabled(true);
                btnNo.setEnabled(true);
            }
        });
    }
}
