package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import eruplan.unisa.eruplan.BuildConfig;
import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.callback.AppoggiCallback;
import eruplan.unisa.eruplan.callback.GenericCallback;
import eruplan.unisa.eruplan.callback.MembriCallback;
import eruplan.unisa.eruplan.callback.NucleoCallback;
import eruplan.unisa.eruplan.callback.RichiesteCallback;
import eruplan.unisa.eruplan.callback.UtenteCallback;
import eruplan.unisa.eruplan.entity.AppoggioEntity;
import eruplan.unisa.eruplan.entity.MembroEntity;
import eruplan.unisa.eruplan.entity.NucleoEntity;
import eruplan.unisa.eruplan.entity.RichiestaEntity;
import eruplan.unisa.eruplan.utility.VolleySingleton;

public class GestioneNucleoFamiliareRepository {

    // Endpoints
    private static final String SOTTOSISTEMA_NUCLEO_URL = BuildConfig.BASE_URL + "/sottosistema";
    private static final String NUCLEO_URL = BuildConfig.BASE_URL + "/nucleo";

    private static final String MEMBERS_ENDPOINT = SOTTOSISTEMA_NUCLEO_URL + "/membri";
    private static final String NUCLEI_ENDPOINT = SOTTOSISTEMA_NUCLEO_URL + "/nuclei";
    private static final String RESIDENZA_ENDPOINT = NUCLEI_ENDPOINT + "/residenza";
    private static final String ABBANDONA_ENDPOINT = NUCLEI_ENDPOINT + "/abbandona";
    private static final String APPOGGI_ENDPOINT = SOTTOSISTEMA_NUCLEO_URL + "/appoggi";
    private static final String INVITI_ENDPOINT = NUCLEO_URL + "/inviti";
    private static final String ACCETTA_INVITO_ENDPOINT = INVITI_ENDPOINT + "/accetta";
    private static final String CERCA_UTENTE_ENDPOINT = SOTTOSISTEMA_NUCLEO_URL + "/utenti/cerca";
    private static final String INVITA_UTENTE_ENDPOINT = SOTTOSISTEMA_NUCLEO_URL + "/inviti/nuovo";

    private final Context context;

    public GestioneNucleoFamiliareRepository(Context context) {
        this.context = context;
    }

    private String parseError(com.android.volley.VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            try {
                String responseBody = new String(response.data, StandardCharsets.UTF_8);
                JSONObject data = new JSONObject(responseBody);
                if (data.has("message")) {
                    return data.getString("message");
                } else if (data.has("error")) {
                    return data.getString("error");
                }
            } catch (Exception e) {
                // Ignora, usa messaggio generico
            }
        }
        if (response != null) {
            return context.getString(R.string.repo_server_communication_error_with_code, response.statusCode);
        }
        return context.getString(R.string.repo_server_communication_error);
    }

    public void getNucleo(final NucleoCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, RESIDENZA_ENDPOINT, null,
                response -> {
                    try {
                        NucleoEntity nucleo = new NucleoEntity();
                        nucleo.setViaPiazza(response.getString("viaPiazza"));
                        nucleo.setComune(response.getString("comune"));
                        nucleo.setRegione(response.getString("regione"));
                        nucleo.setPaese(response.getString("paese"));
                        nucleo.setCivico(response.getString("civico"));
                        nucleo.setCap(response.getString("cap"));
                        callback.onNucleoLoaded(nucleo);
                    } catch (JSONException e) {
                        callback.onError(context.getString(R.string.repo_parsing_error));
                    }
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getRichieste(final RichiesteCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, INVITI_ENDPOINT, null,
                response -> {
                    try {
                        List<RichiestaEntity> richieste = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            richieste.add(new RichiestaEntity(obj.getLong("id"), obj.getString("nomeMittente"), obj.getString("dataOra")));
                        }
                        callback.onRichiesteLoaded(richieste);
                    } catch (JSONException e) {
                        callback.onError(context.getString(R.string.repo_parsing_error));
                    }
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getMembri(final MembriCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, MEMBERS_ENDPOINT, null,
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
                        callback.onMembriLoaded(membri);
                    } catch (JSONException e) {
                        callback.onError(context.getString(R.string.repo_parsing_error));
                    }
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getAppoggi(final AppoggiCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, APPOGGI_ENDPOINT, null,
                response -> {
                    try {
                        List<AppoggioEntity> appoggi = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject json = response.getJSONObject(i);
                            AppoggioEntity appoggio = new AppoggioEntity(
                                    json.getString("viaPiazza"), json.getString("civico"), json.getString("comune"),
                                    json.getString("cap"), json.getString("provincia"), json.getString("regione"),
                                    json.getString("paese")
                            );
                            appoggio.setId(json.getLong("id"));
                            appoggi.add(appoggio);
                        }
                        callback.onAppoggiLoaded(appoggi);
                    } catch (JSONException e) {
                        callback.onError(context.getString(R.string.repo_parsing_error));
                    }
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void eliminaAppoggio(long appoggioId, final GenericCallback callback) {
        String url = APPOGGI_ENDPOINT + "/" + appoggioId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    boolean success = response.optBoolean("success");
                    String message = response.optString("message", context.getString(R.string.repo_delete_success_default));
                    if (success) callback.onSuccess(message); else callback.onError(message);
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void rimuoviMembro(String codiceFiscale, final GenericCallback callback) {
        String url = MEMBERS_ENDPOINT + "/" + codiceFiscale;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    boolean success = response.optBoolean("success");
                    String message = response.optString("message", context.getString(R.string.repo_remove_member_success_default));
                    if (success) callback.onSuccess(message); else callback.onError(message);
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void salvaMembro(MembroEntity membro, final GenericCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("nome", membro.getNome());
            body.put("cognome", membro.getCognome());
            body.put("codiceFiscale", membro.getCodiceFiscale());
            body.put("dataDiNascita", membro.getDataDiNascita());
            body.put("sesso", membro.getSesso());
            body.put("assistenza", membro.isAssistenza());
            body.put("minorenne", membro.isMinorenne());
        } catch (JSONException e) {
            callback.onError(context.getString(R.string.repo_internal_request_error));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, MEMBERS_ENDPOINT, body,
                response -> {
                    boolean success = response.optBoolean("success");
                    String message = response.optString("message", context.getString(R.string.repo_generic_operation_completed));
                    if (success) callback.onSuccess(message); else callback.onError(message);
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void salvaNucleo(NucleoEntity nucleo, final GenericCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("viaPiazza", nucleo.getViaPiazza());
            body.put("comune", nucleo.getComune());
            body.put("regione", nucleo.getRegione());
            body.put("paese", nucleo.getPaese());
            body.put("civico", nucleo.getCivico());
            body.put("cap", nucleo.getCap());
            body.put("hasVeicolo", nucleo.hasVeicolo());
            body.put("postiVeicolo", nucleo.getPostiVeicolo());
        } catch (JSONException e) {
            callback.onError(context.getString(R.string.repo_internal_request_error));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, NUCLEI_ENDPOINT, body,
                response -> {
                    boolean success = response.optBoolean("success");
                    String message = response.optString("message", context.getString(R.string.repo_create_nucleus_success_default));
                    if (success) callback.onSuccess(message); else callback.onError(message);
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void modificaResidenza(NucleoEntity nucleo, final GenericCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("viaPiazza", nucleo.getViaPiazza());
            body.put("comune", nucleo.getComune());
            body.put("regione", nucleo.getRegione());
            body.put("paese", nucleo.getPaese());
            body.put("civico", nucleo.getCivico());
            body.put("cap", nucleo.getCap());
        } catch (JSONException e) {
            callback.onError(context.getString(R.string.repo_internal_request_error));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, RESIDENZA_ENDPOINT, body,
                response -> {
                    boolean success = response.optBoolean("success");
                    String msg = response.optString("message", context.getString(R.string.repo_generic_operation_completed));
                    if (success) callback.onSuccess(msg); else callback.onError(msg);
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void salvaAppoggio(AppoggioEntity appoggio, final GenericCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("viaPiazza", appoggio.getViaPiazza());
            body.put("civico", appoggio.getCivico());
            body.put("comune", appoggio.getComune());
            body.put("cap", appoggio.getCap());
            body.put("provincia", appoggio.getProvincia());
            body.put("regione", appoggio.getRegione());
            body.put("paese", appoggio.getPaese());
        } catch (JSONException e) {
            callback.onError(context.getString(R.string.repo_internal_request_error));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, APPOGGI_ENDPOINT, body,
                response -> {
                    boolean success = response.optBoolean("success");
                    String message = response.optString("message", context.getString(R.string.repo_create_support_success_default));
                    if (success) callback.onSuccess(message); else callback.onError(message);
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void abbandonaNucleo(final GenericCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ABBANDONA_ENDPOINT, null,
                response -> {
                    boolean success = response.optBoolean("success");
                    String message = response.optString("message", context.getString(R.string.repo_leave_nucleus_success_default));
                    if (success) callback.onSuccess(message); else callback.onError(message);
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void cercaUtenteByCF(String codiceFiscale, final UtenteCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("codiceFiscale", codiceFiscale);
        } catch (JSONException e) {
            callback.onError(context.getString(R.string.repo_internal_request_error));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CERCA_UTENTE_ENDPOINT, body,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONObject data = response.getJSONObject("data");
                            MembroEntity m = new MembroEntity();
                            m.setNome(data.optString("nome"));
                            m.setCognome(data.optString("cognome"));
                            m.setCodiceFiscale(data.optString("codiceFiscale"));
                            callback.onSuccess(m);
                        } else {
                            callback.onError(response.optString("message", context.getString(R.string.repo_user_not_found)));
                        }
                    } catch (JSONException e) {
                        callback.onError(context.getString(R.string.repo_parsing_error));
                    }
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void inviaRichiestaInvito(String codiceFiscaleDestinatario, final GenericCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("codiceFiscaleDestinatario", codiceFiscaleDestinatario);
        } catch (JSONException e) {
            callback.onError(context.getString(R.string.repo_internal_request_error));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, INVITA_UTENTE_ENDPOINT, body,
                response -> {
                    boolean success = response.optBoolean("success");
                    String msg = response.optString("message", context.getString(R.string.repo_generic_operation_completed));
                    if (success) callback.onSuccess(msg); else callback.onError(msg);
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void accettaRichiestaApi(long idRichiesta, final GenericCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("idRichiesta", idRichiesta);
        } catch (JSONException e) {
            callback.onError(context.getString(R.string.repo_internal_request_error));
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ACCETTA_INVITO_ENDPOINT, body,
                response -> {
                    boolean success = response.optBoolean("success");
                    String msg = response.optString("message", context.getString(R.string.repo_generic_operation_completed));
                    if (success) callback.onSuccess(msg); else callback.onError(msg);
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
