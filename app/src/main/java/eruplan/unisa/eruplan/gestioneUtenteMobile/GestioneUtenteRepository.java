package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import eruplan.unisa.eruplan.utility.VolleySingleton;

/**
 * Gestisce la persistenza e la comunicazione con il server per le operazioni relative all'utente.
 * È l'unico strato che conosce gli endpoint del server e come costruire le richieste JSON.
 */
public class GestioneUtenteRepository {

    private static final String LOGIN_URL = "https://eruplanserver.azurewebsites.net/gestoreUtentiMobile/login";
    private static final String SIGNUP_URL = "https://eruplanserver.azurewebsites.net/gestoreUtentiMobile/registra";
    private static final String LOGOUT_URL = "https://eruplanserver.azurewebsites.net/gestoreUtentiMobile/logout";
    private final Context context;

    public interface RepositoryCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public GestioneUtenteRepository(Context context) {
        this.context = context;
    }

    /**
     * Crea il JSON ed esegue la chiamata di rete per il login.
     * Usa StringRequest per gestire la risposta di successo con corpo vuoto dal backend.
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    // Per il login, una risposta 2xx è un successo, anche con corpo vuoto.
                    callback.onSuccess("Login effettuato con successo.");
                },
                error -> {
                    String errorMessage = parseError(error);
                    callback.onError(errorMessage);
                }
        ) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * Esegue la chiamata di rete per il logout.
     * Usa StringRequest per gestire la risposta di successo con corpo vuoto dal backend.
     */
    public void logout(final RepositoryCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, LOGOUT_URL,
                response -> {
                    // Una risposta 2xx è un successo.
                    callback.onSuccess("Logout effettuato con successo.");
                },
                error -> {
                    String errorMessage = parseError(error);
                    callback.onError(errorMessage);
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
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
                    String errorMessage = parseError(error);
                    callback.onError(errorMessage);
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Estrae un messaggio di errore significativo dalla risposta del server.
     */
    private String parseError(com.android.volley.VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            try {
                String responseBody = new String(response.data, StandardCharsets.UTF_8);
                JSONObject data = new JSONObject(responseBody);
                // Il backend Spring di default usa 'message' per l'errore inviato con sendError
                if (data.has("message")) {
                    return data.getString("message");
                } else if (data.has("error")) {
                    return data.getString("error");
                }
            } catch (Exception e) {
                // Ignora l'errore di parsing e restituisce un errore generico
            }
        }
        // Fallback su un messaggio di errore generico
        String genericError = "Errore di comunicazione con il server.";
        if (response != null) {
            // Aggiunge il codice di stato se disponibile per aiutare nel debug
            genericError += " (Codice: " + response.statusCode + ")";
        }
        return genericError;
    }
}
