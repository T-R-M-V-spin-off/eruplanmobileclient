package eruplan.unisa.eruplan;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Servizio per la gestione delle notifiche Push di Firebase (FCM).
 * Rinominato da MyFirebaseMessagingService per coerenza col progetto.
 */
public class EruplanMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    private static final String CHANNEL_ID = "DefaultChannel"; // ID del canale

    /**
     * Chiamato se un nuovo token viene generato.
     * Questo succede in background, separatamente dalle Activity.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Token FCM Aggiornato/Nuovo: " + token);
        // In un'app reale, questo token andrebbe inviato al server per notifiche mirate al singolo utente
    }

    /**
     * Chiamato quando un messaggio è ricevuto MENTRE L'APP È IN FOREGROUND (aperta).
     * Se l'app è in background o chiusa, Firebase gestisce la notifica
     * automaticamente (la mostra nella barra di stato).
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Messaggio ricevuto da: " + remoteMessage.getFrom());

        // Controlla se il messaggio contiene un payload di notifica.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notifica: Titolo=" + title + ", Corpo=" + body);

            // Dato che l'app è aperta, Firebase non mostra la notifica.
            // Dobbiamo farlo noi manualmente chiamando il nostro helper.
            sendNotification(title, body);
        }
    }

    // Costruisce e mostra la notifica (serve per il foreground).
    private void sendNotification(String title, String messageBody) {
        // 1. Bisogna creare un Canale
        createNotificationChannel();

        // 2. Costruzione della notifica
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                // Icona di default (esiste sempre nei progetti nuovi)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // La notifica sparisce quando ci clicchi

        // 3. Mostra la notifica
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Controlla il permesso prima di notificare (necessario su Android 13+)
        try {
             notificationManager.notify(0, notificationBuilder.build());
        } catch (SecurityException e) {
             Log.w(TAG, "Notifica bloccata. Permesso POST_NOTIFICATIONS non concesso.");
        }
    }

    // Crea il Canale di Notifica. Obbligatorio per Android 8.0+
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Notifiche Principali";
            String description = "Canale di default per le notifiche push";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Registra il canale con il sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}