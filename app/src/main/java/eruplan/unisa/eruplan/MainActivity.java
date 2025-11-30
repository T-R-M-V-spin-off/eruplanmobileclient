package eruplan.unisa.eruplan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FCM_DEBUG";
    // Definiamo il nome del "canale" a cui ci iscriviamo
    private static final String TOPIC_TO_SUBSCRIBE = "emergenza";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("Prototipo Push Notification emergenza in corso!");
        setContentView(textView);
        subscribeToTopic();
    }

    /**
     * Iscrive silenziosamente questo dispositivo al topic "news" (topic generico creato per il test).
     * L'utente non vede nulla.
     * idee per il futuro:
     *  - prendere l'indirizzo del nucleo familiare tramite il form
     *  - creare più topic es: emergenza_campania, emergenza_lombardia ecc
     *  per poter mandare la notifica solo alle regioni corrette in base alla posizione
     */
    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_TO_SUBSCRIBE)
                .addOnCompleteListener(task -> {
                    String msg = "Iscrizione al Topic '" + TOPIC_TO_SUBSCRIBE + "' riuscita.";
                    if (!task.isSuccessful()) {
                        msg = "ERRORE: Iscrizione al Topic fallita.";
                        Log.e(TAG, msg, task.getException());
                    } else {
                        Log.d(TAG, msg);
                    }
                });
    }
}