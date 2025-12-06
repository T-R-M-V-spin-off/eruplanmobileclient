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

import java.net.CookieHandler;
import java.net.CookieManager;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.gestioneUtenteMobile.StartupBoundary;

public class GestioneNucleoBoundary extends AppCompatActivity {

    // Riferimenti ai pulsanti nel layout
    private MaterialButton btnLogout;
    private MaterialButton btnMembri;
    private MaterialButton btnLuoghi;
    private MaterialButton btnResidenza;
    private MaterialButton btnAbbandona;
    private MaterialButton btnSi;
    private MaterialButton btnNo;
    private TextView tvConferma;
    private LinearLayout layoutConfermaAzioni;
    private Flow buttonFlow;

    // Controller per la gestione del nucleo
    private GestioneNucleoFamiliareControl gestioneNucleoControl;

    // Classe di destinazione dopo il logout: StartupActivity (Homepage)
    private final Class<?> LOGOUT_TARGET_CLASS = StartupBoundary.class;

    // TAG per i log delle notifiche
    private static final String TAG_FCM = "FCM_DEBUG";
    // Topic per le notifiche di emergenza
    private static final String TOPIC_EMERGENZA = "emergenza";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carica il layout del menu GNF
        setContentView(R.layout.activity_gnf);

        // 1. Inizializzazione del Control
        gestioneNucleoControl = new GestioneNucleoFamiliareControl(getApplicationContext());


        // Inizializzazione Views
        btnLogout = findViewById(R.id.btnLogout);
        btnMembri = findViewById(R.id.btnMembri);
        btnLuoghi = findViewById(R.id.btnLuoghi);
        btnResidenza = findViewById(R.id.btnResidenza);
        btnAbbandona = findViewById(R.id.btnAbbandona);
        btnSi = findViewById(R.id.btnSi);
        btnNo = findViewById(R.id.btnNo);
        tvConferma = findViewById(R.id.tvConferma);
        layoutConfermaAzioni = findViewById(R.id.layoutConfermaAzioni);
        buttonFlow = findViewById(R.id.button_flow);

        // Iscrizione al topic per le notifiche di emergenza.
        subscribeToNotificationTopic();

        // Listener per il pulsante "I membri del tuo Nucleo"
        btnMembri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GestioneNucleoBoundary.this, VisualizzaNucleoBoundary.class);
                startActivity(intent);
            }
        });

        // Listener per il pulsante "I tuoi Luoghi Sicuri"
        btnLuoghi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestioneNucleoControl.apriListaAppoggio();
            }
        });

        btnResidenza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestioneNucleoControl.mostraVisualizzaResidenza();
            }
        });

        // Listener per il pulsante "Logout"
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });

        // Listener per il pulsante "Abbandona Nucleo"
        btnAbbandona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostra la sezione di conferma
                mostraSezioneConferma(true);

            }
        });

        // Listener per il pulsante "Sì" (nella sezione di conferma)
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logica per eseguire l\'abbandono del nucleo
                eseguiAbbandonoNucleo();
            }
        });


        // Listener per il pulsante "No" (nella sezione di conferma)
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nasconde la sezione di conferma
                mostraSezioneConferma(false);
            }
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

    // Metodo per eseguire il logout
    private void performLogout() {
        // 1. Cancella i cookie di sessione (JSESSIONID) memorizzati localmente
        CookieHandler cookieHandler = CookieHandler.getDefault();
        if (cookieHandler instanceof CookieManager) {
            ((CookieManager) cookieHandler).getCookieStore().removeAll();
        }

        // 2. Navigazione alla schermata di avvio (StartupActivity)
        navigateToStartupScreen();
    }

    // Metodo per reindirizzare l\'utente alla schermata di avvio
    private void navigateToStartupScreen() {
        Intent intent = new Intent(GestioneNucleoBoundary.this, LOGOUT_TARGET_CLASS);
        // I flag servono a pulire lo stack delle activity (non si può tornare indietro al menu loggato)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Mostra o nasconde la sezione di conferma per l\'abbandono del nucleo.
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
     * Chiama il control per eseguire l\'operazione di abbandono del nucleo
     * e gestisce la risposta.
     */
    private void eseguiAbbandonoNucleo() {
        // Disabilita i bottoni per prevenire click multipli durante la richiesta
        btnSi.setEnabled(false);
        btnNo.setEnabled(false);

        gestioneNucleoControl.abbandonaNucleo(new GestioneNucleoFamiliareControl.ControlCallback() {
            @Override
            public void onInserimentoSuccesso(String message) {
                // Successo: mostra un messaggio e naviga alla schermata di avvio
                Toast.makeText(GestioneNucleoBoundary.this, message, Toast.LENGTH_LONG).show();
                navigateToStartupScreen(); // Riutilizziamo il metodo di navigazione
            }

            @Override
            public void onInserimentoErrore(String message) {
                // Errore: nascondi la sezione di conferma, mostra un errore e riabilita i bottoni
                mostraSezioneConferma(false);
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                btnSi.setEnabled(true);
                btnNo.setEnabled(true);
            }
        });
    }


}
