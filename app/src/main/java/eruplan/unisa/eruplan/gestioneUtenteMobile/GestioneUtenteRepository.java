package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import eruplan.unisa.eruplan.BuildConfig;
import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.utility.VolleySingleton;

/**
 * Gestisce la persistenza e la comunicazione con il server per le operazioni relative all'utente.
 */
public class GestioneUtenteRepository {

    private static final String LOGIN_URL = BuildConfig.BASE_URL + "/gestoreUtentiMobile/login";
    private static final String SIGNUP_URL = BuildConfig.BASE_URL + "/gestoreUtentiMobile/registra";
    private static final String LOGOUT_URL = BuildConfig.BASE_URL + "/gestoreUtentiMobile/logout";
    private final Context context;

    public interface RepositoryCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public GestioneUtenteRepository(Context context) {
        this.context = context;
    }

    public void login(String codiceFiscale, String password, final RepositoryCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("codiceFiscale", codiceFiscale);
            requestBody.put("password", password);
        } catch (JSONException e) {
            callback.onError(context.getString(R.string.repo_internal_request_error));
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> callback.onSuccess(context.getString(R.string.repo_login_success)),
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

    public void logout(final RepositoryCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, LOGOUT_URL,
                response -> {
                    VolleySingleton.logout();
                    callback.onSuccess(context.getString(R.string.repo_logout_success));
                },
                error -> {
                    VolleySingleton.logout();
                    String errorMessage = parseError(error);
                    callback.onError(errorMessage);
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

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
            callback.onError(context.getString(R.string.repo_signup_request_error));
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SIGNUP_URL, requestBody,
                response -> {
                    try {
                        boolean success = response.optBoolean("success", true);
                        String message = response.optString("message", context.getString(R.string.repo_signup_success_default));
                        if (success) {
                            callback.onSuccess(message);
                        } else {
                            callback.onError(message);
                        }
                    } catch (Exception e) {
                        callback.onError(context.getString(R.string.repo_invalid_server_response));
                    }
                },
                error -> {
                    String errorMessage = parseError(error);
                    callback.onError(errorMessage);
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
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
                // Ignora l'errore di parsing, verrà usato un messaggio generico
            }
        }
        if (response != null) {
            return context.getString(R.string.repo_server_communication_error_with_code, response.statusCode);
        }
        return context.getString(R.string.repo_server_communication_error);
    }
}
