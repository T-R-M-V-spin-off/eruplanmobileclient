package eruplan.unisa.eruplan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessaging;

import java.net.CookieHandler;
import java.net.CookieManager;

public class GnfActivity extends AppCompatActivity {

    // Riferimenti ai pulsanti nel layout
    private AppCompatButton btnLogout;
    private AppCompatButton btnMembri;

    // Classe di destinazione dopo il logout: StartupActivity (Homepage)
    private final Class<?> LOGOUT_TARGET_CLASS = StartupActivity.class;
    
    // TAG per i log delle notifiche
    private static final String TAG_FCM = "FCM_DEBUG";
    // Topic per le notifiche di emergenza
    private static final String TOPIC_EMERGENZA = "emergenza";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carica il layout del menu GNF
        setContentView(R.layout.activity_gnf);

        // Inizializzazione Views
        btnLogout = findViewById(R.id.btnLogout);
        btnMembri = findViewById(R.id.btnMembri);
        
        // Iscrizione al topic per le notifiche di emergenza.
        subscribeToNotificationTopic();

        // Listener per il pulsante "I membri del tuo Nucleo"
        btnMembri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GnfActivity.this, VisualizzaNucleoActivity.class);
                startActivity(intent);
            }
        });

        // Listener per il pulsante "Logout"
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
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

    // Metodo per reindirizzare l'utente alla schermata di avvio
    private void navigateToStartupScreen() {
        Intent intent = new Intent(GnfActivity.this, LOGOUT_TARGET_CLASS);
        // I flag servono a pulire lo stack delle activity (non si può tornare indietro al menu loggato)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}