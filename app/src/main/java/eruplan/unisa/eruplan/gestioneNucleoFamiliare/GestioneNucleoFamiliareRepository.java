package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
// MODIFICA: Rimosso import di RequestQueue e Volley standard perché usiamo il Singleton

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eruplan.unisa.eruplan.VolleySingleton;
import eruplan.unisa.eruplan.entity.AppoggioEntity;
import eruplan.unisa.eruplan.entity.MembroEntity;
import eruplan.unisa.eruplan.entity.NucleoEntity;
import eruplan.unisa.eruplan.entity.RichiestaEntity;

/**
 * Gestisce la persistenza dei dati comunicando con il server web.
 */
public class GestioneNucleoFamiliareRepository {

    private static final String ADD_MEMBER_URL = "https://eruplanserver.azurewebsites.net/sottosistema/membri";
    private static final String ADD_NUCLEO_URL = "https://eruplanserver.azurewebsites.net/sottosistema/nuclei";
    private static final String GET_NUCLEO_URL = "https://eruplanserver.azurewebsites.net/sottosistema/nuclei/residenza";
    private static final String ABBANDONA_NUCLEO_URL = "https://eruplanserver.azurewebsites.net/sottosistema/nuclei/abbandona";
    private static final String ADD_APPOGGIO_URL = "https://eruplanserver.azurewebsites.net/sottosistema/appoggi";
    private static final String GET_INVITI_URL = "https://eruplanserver.azurewebsites.net/nucleo/inviti";


    // MODIFICA: Rimosso la RequestQueue locale.
    // Ora utilizziamo il contesto per accedere al VolleySingleton.
    // private RequestQueue requestQueue;
    private Context context;

    public interface RepositoryCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public interface NucleoCallback {
        void onSuccess(NucleoEntity nucleo);
        void onError(String message);
    }

    public interface AppoggiCallback {
        void onSuccess(List<AppoggioEntity> appoggi);
        void onError(String message);
    }

    public interface MembriCallback {
        void onSuccess(List<MembroEntity> membri);
        void onError(String message);
    }

    public interface RichiesteCallback {
        void onSuccess(List<RichiestaEntity> richieste);
        void onError(String message);
    }

    public GestioneNucleoFamiliareRepository(Context context) {
        // MODIFICA: Invece di creare una nuova coda separata (che perderebbe la sessione),
        // salviamo il context per richiamare il Singleton condiviso quando serve.
        this.context = context;

        // Vecchio codice rimosso:
        // this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void getNucleo(final NucleoCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_NUCLEO_URL, null,
                response -> {
                    try {
                        NucleoEntity nucleo = new NucleoEntity();
                        nucleo.setViaPiazza(response.getString("viaPiazza"));
                        nucleo.setComune(response.getString("comune"));
                        nucleo.setRegione(response.getString("regione"));
                        nucleo.setPaese(response.getString("paese"));
                        nucleo.setCivico(response.getString("civico"));
                        nucleo.setCap(response.getString("cap"));
                        callback.onSuccess(nucleo);
                    } catch (JSONException e) {
                        callback.onError("Errore nel parsing della risposta del server.");
                    }
                },
                error -> callback.onError("Errore di connessione al server: " + error.getMessage()));

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void getRichieste(final RichiesteCallback callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_INVITI_URL, null,
                response -> {
                    try {
                        List<RichiestaEntity> richieste = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);

                            String mittente = jsonObject.getString("nomeMittente").trim();
                            String data = jsonObject.getString("dataOra").trim();

                            richieste.add(new RichiestaEntity(mittente, data));
                        }
                        callback.onSuccess(richieste);
                    } catch (JSONException e) {
                        callback.onError("Errore nel parsing della risposta del server.");
                    }
                },
                error -> callback.onError("Errore di connessione al server: " + error.getMessage()));

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void getMembri(final MembriCallback callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ADD_MEMBER_URL, null,
                response -> {
                    try {
                        List<MembroEntity> membri = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            MembroEntity m = new MembroEntity();
                            m.setNome(obj.optString("nome"));
                            m.setCognome(obj.optString("cognome"));
                            m.setCodiceFiscale(obj.optString("codiceFiscale"));
                            m.setDataDiNascita(obj.optString("dataDiNascita"));
                            m.setSesso(obj.optString("sesso"));
                            m.setAssistenza(obj.optBoolean("assistenza"));
                            m.setMinorenne(obj.optBoolean("minorenne"));
                            membri.add(m);
                        }
                        callback.onSuccess(membri);
                    } catch (JSONException e) {
                        callback.onError("Errore nel parsing della risposta del server.");
                    }
                },
                error -> callback.onError("Errore di connessione al server: " + error.getMessage()));

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void getAppoggi(final AppoggiCallback callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ADD_APPOGGIO_URL, null,
                response -> {
                    try {
                        List<AppoggioEntity> appoggi = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject appoggioJson = response.getJSONObject(i);
                            AppoggioEntity appoggioEntity = new AppoggioEntity(
                                    appoggioJson.getString("viaPiazza"),
                                    appoggioJson.getString("civico"),
                                    appoggioJson.getString("comune"),
                                    appoggioJson.getString("cap"),
                                    appoggioJson.getString("provincia"),
                                    appoggioJson.getString("regione"),
                                    appoggioJson.getString("paese")
                            );
                            appoggioEntity.setId(appoggioJson.getLong("id"));
                            appoggi.add(appoggioEntity);
                        }
                        callback.onSuccess(appoggi);
                    } catch (JSONException e) {
                        callback.onError("Errore nel parsing della risposta del server.");
                    }
                },
                error -> callback.onError("Errore di connessione al server: " + error.getMessage()));

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void eliminaAppoggio(long appoggioId, final RepositoryCallback callback) {
        String url = ADD_APPOGGIO_URL + "/" + appoggioId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.optString("message", "Appoggio eliminato con successo.");
                        if (success) {
                            callback.onSuccess(message);
                        } else {
                            callback.onError(message);
                        }
                    } catch (JSONException e) {
                        callback.onError("Errore nel formato della risposta del server.");
                    }
                },
                error -> callback.onError("Errore di connessione al server: " + error.getMessage()));

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void rimuoviMembro(String codiceFiscale, final RepositoryCallback callback) {
        String url = ADD_MEMBER_URL + "/" + codiceFiscale;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.optString("message", "Membro rimosso con successo.");
                        if (success) {
                            callback.onSuccess(message);
                        } else {
                            callback.onError(message);
                        }
                    } catch (JSONException e) {
                        callback.onError("Errore nel formato della risposta del server.");
                    }
                },
                error -> callback.onError("Errore di connessione al server: " + error.getMessage()));

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public void salvaMembro(MembroEntity membroEntity, final RepositoryCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("nome", membroEntity.getNome());
            requestBody.put("cognome", membroEntity.getCognome());
            requestBody.put("codiceFiscale", membroEntity.getCodiceFiscale());
            requestBody.put("dataDiNascita", membroEntity.getDataDiNascita());
            requestBody.put("sesso", membroEntity.getSesso());
            requestBody.put("assistenza", membroEntity.isAssistenza());
            requestBody.put("minorenne", membroEntity.isMinorenne());
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
    public void salvaNucleo(NucleoEntity nucleoEntity, final RepositoryCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("viaPiazza", nucleoEntity.getViaPiazza());
            requestBody.put("comune", nucleoEntity.getComune());
            requestBody.put("regione", nucleoEntity.getRegione());
            requestBody.put("paese", nucleoEntity.getPaese());
            requestBody.put("civico", nucleoEntity.getCivico());
            requestBody.put("cap", nucleoEntity.getCap());
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

    public void salvaAppoggio(AppoggioEntity appoggio, final RepositoryCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("viaPiazza", appoggio.getViaPiazza());
            requestBody.put("civico", appoggio.getCivico());
            requestBody.put("comune", appoggio.getComune());
            requestBody.put("cap", appoggio.getCap());
            requestBody.put("provincia", appoggio.getProvincia());
            requestBody.put("regione", appoggio.getRegione());
            requestBody.put("paese", appoggio.getPaese());
        } catch (JSONException e) {
            callback.onError("Errore interno nella creazione della richiesta JSON per l'appoggio.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ADD_APPOGGIO_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.optString("message", "Appoggio creato con successo.");
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

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Invia la richiesta al server per far sì che l'utente loggato abbandoni il suo nucleo familiare.
     * L'identificazione dell'utente avviene tramite il cookie di sessione gestito da VolleySingleton.
     * @param callback L'interfaccia per notificare il successo o l'errore dell'operazione.
     */
    public void abbandonaNucleo(final RepositoryCallback callback) {
        // La richiesta è di tipo POST e non necessita di un corpo (body) JSON,
        // perché il server identifica l'utente dalla sessione. Passiamo 'null'.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ABBANDONA_NUCLEO_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Il server dovrebbe rispondere con un JSON che indica il successo
                            boolean success = response.getBoolean("success");
                            String message = response.optString("message", "Operazione completata con successo.");
                            if (success) {
                                callback.onSuccess(message);
                            } else {
                                callback.onError(message);
                            }
                        } catch (JSONException e) {
                            callback.onError("Errore nella lettura della risposta del server.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Errore di connessione. Impossibile abbandonare il nucleo." + error.getMessage());
                    }
                });

        // Aggiunta della richiesta alla coda usando il Singleton per mantenere la sessione
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    // =================================================================================
    //  NUOVI METODI PER L'INVITO (REQUISITO UC-GNF.01)
    // =================================================================================

    // URL concordati (ipotetici)
    private static final String CERCA_UTENTE_URL = "https://eruplanserver.azurewebsites.net/sottosistema/utenti/cerca";
    private static final String INVITA_UTENTE_URL = "https://eruplanserver.azurewebsites.net/sottosistema/inviti/nuovo";

    /**
     * Interfaccia specifica per quando cerchiamo un utente.
     * Ci restituisce un oggetto MembroEntity se lo troviamo.
     */
    public interface UtenteCallback {
        void onSuccess(MembroEntity membroTrovato);
        void onError(String message);
    }

    /**
     * Cerca nel DB se esiste un utente con questo Codice Fiscale.
     * Serve per l'anteprima prima di invitare.
     */
    public void cercaUtenteByCF(String codiceFiscale, final UtenteCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            // "codiceFiscale" è il nome del campo concordato nel JSON
            requestBody.put("codiceFiscale", codiceFiscale);
        } catch (JSONException e) {
            callback.onError("Errore interno: impossibile creare il JSON.");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CERCA_UTENTE_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                // Se success=true, mi aspetto un oggetto "data" con i dettagli
                                JSONObject data = response.getJSONObject("data");

                                // Creo un oggetto temporaneo per passare i dati
                                // Uso il costruttore vuoto e i setter per sicurezza
                                MembroEntity m = new MembroEntity();
                                m.setNome(data.optString("nome"));
                                m.setCognome(data.optString("cognome"));
                                m.setCodiceFiscale(data.optString("codiceFiscale"));

                                callback.onSuccess(m);
                            } else {
                                // Il server ha risposto, ma non ha trovato l'utente
                                String msg = response.optString("message", "Nessun utente trovato.");
                                callback.onError(msg);
                            }
                        } catch (JSONException e) {
                            callback.onError("Errore nella lettura della risposta del server.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Errore di connessione o utente non trovato.");
                    }
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Invia l'invito effettivo all'utente specificato.
     */
    public void inviaRichiestaInvito(String codiceFiscaleDestinatario, final RepositoryCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            // "codiceFiscaleDestinatario" è il nome concordato
            requestBody.put("codiceFiscaleDestinatario", codiceFiscaleDestinatario);
        } catch (JSONException e) {
            callback.onError("Errore interno JSON.");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, INVITA_UTENTE_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Leggo la risposta standard { "success": true, "message": "..." }
                        boolean success = response.optBoolean("success");
                        String msg = response.optString("message", "Operazione completata.");

                        if (success) {
                            callback.onSuccess(msg);
                        } else {
                            callback.onError(msg);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Impossibile inviare l'invito: " + error.getMessage());
                    }
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

}
