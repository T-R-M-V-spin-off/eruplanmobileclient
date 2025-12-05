package eruplan.unisa.eruplan;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Gestisce la persistenza e la comunicazione con il server per le operazioni relative all'utente.
 * È l'unico strato che conosce gli endpoint del server e come costruire le richieste JSON.
 */
public class GestioneUtenteRepository {

    private static final String LOGIN_URL = "https://eruplanserver.azurewebsites.net/gestoreUtentiMobile/login";
    private static final String SIGNUP_URL = "https://eruplanserver.azurewebsites.net/gestoreUtentiMobile/registra";
    private Context context;

    public interface RepositoryCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public GestioneUtenteRepository(Context context) {
        this.context = context;
    }

    /**
     * Crea il JSON ed esegue la chiamata di rete per il login.
     */
    public void login(String codiceFiscale, String password, final RepositoryCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("codiceFiscale", codiceFiscale);
            requestBody.put("password", password);
        } catch (JSONException e) {
            callback.onError("Errore interno nella creazione della richiesta.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, requestBody,
                response -> {
                    try {
                        String message = response.getString("message");
                        callback.onSuccess(message);
                    } catch (Exception e) {
                        callback.onError("Risposta del server non valida.");
                    }
                },
                error -> {
                    String errorMsg = "Errore di connessione";
                    if (error.networkResponse != null) {
                        errorMsg += " (Server: " + error.networkResponse.statusCode + ")";
                    }
                    callback.onError(errorMsg);
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Crea il JSON ed esegue la chiamata di rete per la registrazione.
     */
    public void registra(String nome, String cognome, String cf, String data, String sesso, String password, final RepositoryCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("nome", nome);
            requestBody.put("cognome", cognome);
            requestBody.put("codiceFiscale", cf.toUpperCase());
            requestBody.put("dataNascita", data);
            requestBody.put("sesso", sesso);
            requestBody.put("password", password);
        } catch (JSONException e) {
            callback.onError("Errore interno nella creazione della richiesta di registrazione.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SIGNUP_URL, requestBody,
                response -> {
                    try {
                        boolean success = response.optBoolean("success", true);
                        String message = response.optString("message", "Registrazione avvenuta con successo.");
                        if (success) {
                            callback.onSuccess(message);
                        } else {
                            callback.onError(message);
                        }
                    } catch (Exception e) {
                        callback.onError("Risposta del server non valida.");
                    }
                },
                error -> {
                     String errorMsg = "Errore di connessione";
                    if (error.networkResponse != null) {
                        errorMsg += " (Server: " + error.networkResponse.statusCode + ")";
                    }
                    callback.onError(errorMsg);
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
