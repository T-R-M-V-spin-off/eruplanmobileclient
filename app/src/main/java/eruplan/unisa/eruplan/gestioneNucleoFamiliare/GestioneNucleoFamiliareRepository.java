package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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

    private static final String BASE_GNF_URL = BuildConfig.BASE_URL + "/gestoreNucleo";

    // Endpoints allineati al GNFControl del server
    private static final String INVITA_ENDPOINT = BASE_GNF_URL + "/invita";
    private static final String ACCETTA_ENDPOINT = BASE_GNF_URL + "/accetta";
    private static final String AGGIUNGI_MEMBRO_ENDPOINT = BASE_GNF_URL + "/aggiungiMembro";
    private static final String ABBANDONA_ENDPOINT = BASE_GNF_URL + "/abbandona";
    private static final String RICHIESTE_ENDPOINT = BASE_GNF_URL + "/richieste";
    private static final String APPOGGI_ENDPOINT = BASE_GNF_URL + "/appoggi";
    private static final String AGGIUNGI_APPOGGIO_ENDPOINT = APPOGGI_ENDPOINT + "/aggiungi";
    private static final String RIMUOVI_APPOGGIO_ENDPOINT = APPOGGI_ENDPOINT + "/rimuovi";

    // Endpoint non presenti nel controller fornito: verranno gestiti con un errore temporaneo.
    private static final String MEMBRI_ENDPOINT = BASE_GNF_URL + "/membri"; // Ipotetico
    private static final String RESIDENZA_ENDPOINT = BASE_GNF_URL + "/residenza"; // Ipotetico
    private static final String CERCA_UTENTE_ENDPOINT = BuildConfig.BASE_URL + "/gestoreUtentiMobile/utenti/cerca";

    private final Context context;

    public GestioneNucleoFamiliareRepository(Context context) {
        this.context = context;
    }

    private String parseError(com.android.volley.VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            try {
                return new String(response.data, StandardCharsets.UTF_8);
            } catch (Exception e) {
                // Ignora
            }
        }
        if (response != null) {
            return context.getString(R.string.repo_server_communication_error_with_code, response.statusCode);
        }
        return context.getString(R.string.repo_server_communication_error);
    }

    // RF-GNF.01: Invita utente
    public void inviaRichiestaInvito(String codiceFiscaleDestinatario, final GenericCallback callback) {
        String url = INVITA_ENDPOINT + "?cfInvitato=" + codiceFiscaleDestinatario;
        StringRequest request = new StringRequest(Request.Method.POST, url, callback::onSuccess, error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // RF-GNF.02: Accetta invito
    public void accettaRichiestaApi(long idRichiesta, final GenericCallback callback) {
        String url = ACCETTA_ENDPOINT + "/" + idRichiesta;
        StringRequest request = new StringRequest(Request.Method.POST, url, callback::onSuccess, error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // RF-GNF.03: Aggiungi Membro Manuale
    public void salvaMembro(MembroEntity membro, final GenericCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("nome", membro.getNome());
            body.put("cognome", membro.getCognome());
            body.put("codiceFiscale", membro.getCodiceFiscale());
            body.put("dataDiNascita", membro.getDataDiNascita());
            body.put("sesso", membro.getSesso());
        } catch (JSONException e) {
            callback.onError(context.getString(R.string.repo_internal_request_error));
            return;
        }
        StringRequest request = new StringRequest(Request.Method.POST, AGGIUNGI_MEMBRO_ENDPOINT, callback::onSuccess, error -> callback.onError(parseError(error))) {
            @Override
            public byte[] getBody() { return body.toString().getBytes(StandardCharsets.UTF_8); }
            @Override
            public String getBodyContentType() { return "application/json; charset=utf-8"; }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // RF-GNF.04: Abbandona Nucleo
    public void abbandonaNucleo(final GenericCallback callback) {
        StringRequest request = new StringRequest(Request.Method.POST, ABBANDONA_ENDPOINT, callback::onSuccess, error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // RF-GNF.06: Visualizza lista richieste
    public void getRichieste(final RichiesteCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, RICHIESTE_ENDPOINT, null,
                response -> {
                    try {
                        List<RichiestaEntity> richieste = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            richieste.add(new RichiestaEntity(obj.optLong("id"), obj.optString("nomeMittente"), obj.optString("dataOra")));
                        }
                        callback.onRichiesteLoaded(richieste);
                    } catch (JSONException e) {
                        callback.onError(context.getString(R.string.repo_parsing_error));
                    }
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // RF-GNF.09: Aggiungi Appoggio
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
        StringRequest request = new StringRequest(Request.Method.POST, AGGIUNGI_APPOGGIO_ENDPOINT, callback::onSuccess, error -> callback.onError(parseError(error))) {
            @Override
            public byte[] getBody() { return body.toString().getBytes(StandardCharsets.UTF_8); }
            @Override
            public String getBodyContentType() { return "application/json; charset=utf-8"; }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // Mostra luoghi sicuri
    public void getAppoggi(final AppoggiCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, APPOGGI_ENDPOINT, null,
                response -> {
                    try {
                        List<AppoggioEntity> appoggi = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject json = response.getJSONObject(i);
                            AppoggioEntity appoggioEntity = new AppoggioEntity(json.getString("viaPiazza"), json.getString("civico"), json.getString("comune"), json.getString("cap"), json.getString("provincia"), json.getString("regione"), json.getString("paese"));
                            appoggioEntity.setId(json.getLong("id"));
                            appoggi.add(appoggioEntity);
                        }
                        callback.onAppoggiLoaded(appoggi);
                    } catch (JSONException e) {
                        callback.onError(context.getString(R.string.repo_parsing_error));
                    }
                },
                error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // RF-GNF.10: Rimuovi Appoggio
    public void eliminaAppoggio(long appoggioId, final GenericCallback callback) {
        String url = RIMUOVI_APPOGGIO_ENDPOINT + "/" + appoggioId;
        StringRequest request = new StringRequest(Request.Method.DELETE, url, callback::onSuccess, error -> callback.onError(parseError(error)));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // --- Metodi con Endpoint non disponibili nel controller GNF --- //

    public void cercaUtenteByCF(String codiceFiscale, final UtenteCallback callback) {
        // Questa logica appartiene al gestoreUtenteMobile e la lasciamo invariata
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
                        if (response.getBoolean("success")) {
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
    
    public void getMembri(final MembriCallback callback) {
        // TODO: Endpoint non specificato nel controller, usare un URL ipotetico
        callback.onError("Endpoint non implementato sul server per getMembri");
    }

    public void getNucleo(final NucleoCallback callback) {
        // TODO: Endpoint non specificato nel controller, usare un URL ipotetico
        callback.onError("Endpoint non implementato sul server per getNucleo");
    }

    public void rimuoviMembro(String codiceFiscale, final GenericCallback callback) {
        // TODO: Endpoint non specificato nel controller
        callback.onError("Endpoint non implementato sul server per rimuoviMembro");
    }

    public void salvaNucleo(NucleoEntity nucleo, final GenericCallback callback) {
        // TODO: Endpoint non specificato nel controller
        callback.onError("Endpoint non implementato sul server per salvaNucleo");
    }

    public void modificaResidenza(NucleoEntity nucleo, final GenericCallback callback) {
        // TODO: Endpoint non specificato nel controller
        callback.onError("Endpoint non implementato sul server per modificaResidenza");
    }
}
