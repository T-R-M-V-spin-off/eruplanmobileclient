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

    // Controller per la gestione del nucleo
    private GestioneNucleoFamiliareControl gestioneNucleoControl;
    private GestioneUtenteControl gestioneUtenteControl;

    // TAG per i log delle notifiche
    private static final String TAG_FCM = "FCM_DEBUG";
    // Topic per le notifiche di emergenza
    private static final String TOPIC_EMERGENZA = "emergenza";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carica il layout del menu GNF
        setContentView(R.layout.activity_gnf);

        // 1. Inizializzazione dei Control
        gestioneNucleoControl = new GestioneNucleoFamiliareControl(getApplicationContext());
        gestioneUtenteControl = new GestioneUtenteControl(this, new GestioneUtenteControl.ControlCallbackAdapter() {
            @Override
            public void onOperazioneSuccess(String message) {
                Toast.makeText(GestioneNucleoBoundary.this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GestioneNucleoBoundary.this, LoginBoundary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onOperazioneError(String message) {
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
            }
        }); // Correzione: Inizializzazione del control


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

        // Iscrizione al topic per le notifiche di emergenza.
        subscribeToNotificationTopic();

        // Listener per il pulsante "I membri del tuo Nucleo"
        btnMembri.setOnClickListener(v -> gestioneNucleoControl.mostraVisualizzaMembri());

        // Listener per il pulsante "I tuoi Luoghi Sicuri"
        btnLuoghi.setOnClickListener(v -> gestioneNucleoControl.apriListaAppoggio());

        //Listneer per il pulsante "Il tuo Piano di Emergenza"
        btnGestioneEmergenza.setOnClickListener(v -> gestioneNucleoControl.mostraGestioneEmergenza());

        btnResidenza.setOnClickListener(v -> gestioneNucleoControl.mostraVisualizzaNucleo());

        // Listener per il pulsante "I tuoi Inviti"
        btnInviti.setOnClickListener(v -> gestioneNucleoControl.mostraListaRichieste());

        // Listener per il pulsante "Logout"
        btnLogout.setOnClickListener(v -> gestioneUtenteControl.logout());

        // Listener per il pulsante "Abbandona Nucleo"
        btnAbbandona.setOnClickListener(v -> {
            // Mostra la sezione di conferma
            mostraSezioneConferma(true);

        });

        // Listener per il pulsante "Sì" (nella sezione di conferma)
        btnSi.setOnClickListener(v -> {
            // Logica per eseguire l'abbandono del nucleo
            eseguiAbbandonoNucleo();
        });


        // Listener per il pulsante "No" (nella sezione di conferma)
        btnNo.setOnClickListener(v -> {
            // Nasconde la sezione di conferma
            mostraSezioneConferma(false);
        });

    }

    /**
     * Iscrive il dispositivo al topic "emergenza" di Firebase Cloud Messaging.
     */
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

    /**
     * Mostra o nasconde la sezione di conferma per l'abbandono del nucleo.
     * @param mostra true per mostrare, false per nascondere.
     */
    private void mostraSezioneConferma(boolean mostra) {
        int visibility = mostra ? View.VISIBLE : View.GONE;
        int gridVisibility = mostra ? View.GONE : View.VISIBLE;

        tvConferma.setVisibility(visibility);
        layoutConfermaAzioni.setVisibility(visibility);
        buttonFlow.setVisibility(gridVisibility);
    }

    /**
     * Chiama il control per eseguire l'operazione di abbandono del nucleo
     * e gestisce la risposta.
     */
    private void eseguiAbbandonoNucleo() {
        // Disabilita i bottoni per prevenire click multipli durante la richiesta
        btnSi.setEnabled(false);
        btnNo.setEnabled(false);

        gestioneNucleoControl.abbandonaNucleo(new GestioneNucleoFamiliareControl.ControlCallback() {
            @Override
            public void onSuccess(String message) { // Correzione: nome metodo
                // Successo: mostra un messaggio e naviga alla schermata di avvio
                Toast.makeText(GestioneNucleoBoundary.this, message, Toast.LENGTH_LONG).show();
                gestioneUtenteControl.logout();
            }

            @Override
            public void onError(String message) { // Correzione: nome metodo
                // Errore: nascondi la sezione di conferma, mostra un errore e riabilita i bottoni
                mostraSezioneConferma(false);
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                btnSi.setEnabled(true);
                btnNo.setEnabled(true);
            }
        });
    }
}
