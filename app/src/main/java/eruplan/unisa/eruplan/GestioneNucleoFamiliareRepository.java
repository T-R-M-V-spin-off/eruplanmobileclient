package eruplan.unisa.eruplan;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Gestisce la persistenza dei dati comunicando con il server web.
 * Questa classe è l'unico punto dell'applicazione che conosce gli endpoint specifici del server
 * e si occupa di effettuare le chiamate di rete (es. tramite Volley).
 */
public class GestioneNucleoFamiliareRepository {

    /**
     * URL dell'endpoint del server per l'aggiunta di un nuovo membro.
     * In un'applicazione reale, questo potrebbe essere centralizzato in una classe di configurazione.
     */
    private static final String ADD_MEMBER_URL = "https://eruplanserver.azurewebsites.net/sottosistema/membri";

    private RequestQueue requestQueue;

    /**
     * Interfaccia di callback per notificare al chiamante (il Service) l'esito di un'operazione di rete.
     * Questo permette di gestire in modo asincrono la risposta del server.
     */
    public interface RepositoryCallback {
        /** Chiamato quando il server conferma che l'operazione è andata a buon fine. */
        void onSuccess(String message);
        /** Chiamato in caso di errore di rete o se il server segnala un fallimento. */
        void onError(String message);
    }

    /**
     * Costruttore che inizializza la coda di richieste di Volley.
     * @param context Il contesto dell'applicazione, necessario per creare la RequestQueue.
     */
    public GestioneNucleoFamiliareRepository(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * Serializza un oggetto {@link Membro} in formato JSON e lo invia al server tramite una richiesta POST.
     *
     * @param membro   L'oggetto Membro contenente i dati da salvare.
     * @param callback Il callback che verrà notificato con il risultato dell'operazione.
     */
    public void salvaMembro(Membro membro, final RepositoryCallback callback) {

        // 1. Prepara il corpo della richiesta (payload) in formato JSON.
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("nome", membro.getNome());
            requestBody.put("cognome", membro.getCognome());
            requestBody.put("codiceFiscale", membro.getCodiceFiscale());
            requestBody.put("dataDiNascita", membro.getDataDiNascita());
            requestBody.put("sesso", membro.getSesso());
            requestBody.put("assistenza", membro.isAssistenza());
            requestBody.put("minorenne", membro.isMinorenne());
        } catch (JSONException e) {
            // Errore quasi impossibile in condizioni normali, ma gestito per sicurezza.
            callback.onError("Errore interno nella creazione della richiesta JSON.");
            return;
        }

        // 2. Crea la richiesta di rete POST con Volley.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ADD_MEMBER_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 3a. Il server ha risposto con successo (es. HTTP 200 OK).
                        try {
                            // Analizziamo il corpo della risposta JSON del server.
                            boolean success = response.getBoolean("success");
                            String message = response.optString("message", "Operazione completata.");
                            if (success) {
                                callback.onSuccess(message);
                            } else {
                                callback.onError(message);
                            }
                        } catch (JSONException e) {
                            callback.onError("Errore nel formato della risposta del server.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 3b. Si è verificato un errore di rete (es. server non raggiungibile, timeout, 404, etc.).
                        callback.onError("Errore di connessione al server: " + error.getMessage());
                    }
                });

        // 4. Aggiunge la richiesta alla coda. Volley la eseguirà in background.
        requestQueue.add(jsonObjectRequest);
    }
}
