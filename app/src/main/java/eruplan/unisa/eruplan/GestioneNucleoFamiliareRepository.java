package eruplan.unisa.eruplan;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
// MODIFICA: Rimosso import di RequestQueue e Volley standard perché usiamo il Singleton

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Gestisce la persistenza dei dati comunicando con il server web.
 */
public class GestioneNucleoFamiliareRepository {

    private static final String ADD_MEMBER_URL = "https://eruplanserver.azurewebsites.net/sottosistema/membri";
    private static final String ADD_NUCLEO_URL = "https://eruplanserver.azurewebsites.net/sottosistema/nuclei";

    // MODIFICA: Rimosso la RequestQueue locale. 
    // Ora utilizziamo il contesto per accedere al VolleySingleton.
    // private RequestQueue requestQueue;
    private Context context;

    public interface RepositoryCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public GestioneNucleoFamiliareRepository(Context context) {
        // MODIFICA: Invece di creare una nuova coda separata (che perderebbe la sessione),
        // salviamo il context per richiamare il Singleton condiviso quando serve.
        this.context = context;
        
        // Vecchio codice rimosso:
        // this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void salvaMembro(Membro membro, final RepositoryCallback callback) {
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
            callback.onError("Errore interno nella creazione della richiesta JSON.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ADD_MEMBER_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
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
                        callback.onError("Errore di connessione al server: " + error.getMessage());
                    }
                });

        // MODIFICA: Aggiungiamo la richiesta alla coda centralizzata tramite il Singleton.
        // Questo assicura che vengano inviati i cookie di sessione (JSESSIONID) del login.
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Invia i dati di un nuovo nucleo al server per il salvataggio.
     */
    public void salvaNucleo(Nucleo nucleo, final RepositoryCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("viaPiazza", nucleo.getViaPiazza());
            requestBody.put("comune", nucleo.getComune());
            requestBody.put("regione", nucleo.getRegione());
            requestBody.put("paese", nucleo.getPaese());
            requestBody.put("civico", nucleo.getCivico());
            requestBody.put("cap", nucleo.getCap());
        } catch (JSONException e) {
            callback.onError("Errore interno nella creazione della richiesta JSON per il nucleo.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ADD_NUCLEO_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.optString("message", "Nucleo creato con successo.");
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
                        callback.onError("Errore di connessione al server: " + error.getMessage());
                    }
                });

        // MODIFICA: Aggiungiamo la richiesta alla coda centralizzata tramite il Singleton.
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}