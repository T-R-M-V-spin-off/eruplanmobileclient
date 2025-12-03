package eruplan.unisa.eruplan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Gestisce il processo di login dell'utente comunicando con un server web per l'autenticazione.
 */
public class LoginActivity extends Activity {

    // URL Reale del server
    private static final String LOGIN_URL = "https://eruplanserver.azurewebsites.net/autenticazione/login";

    private EditText codiceFiscaleEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Riferimenti alle view
        codiceFiscaleEditText = findViewById(R.id.codice_fiscale);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String codiceFiscale = codiceFiscaleEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(codiceFiscale)) {
            Toast.makeText(getApplicationContext(), "Inserisci il Codice Fiscale!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (codiceFiscale.length() != 16) {
            Toast.makeText(getApplicationContext(), "Il Codice Fiscale deve essere di 16 caratteri.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Inserisci la password!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creazione JSON per il server
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("codiceFiscale", codiceFiscale.toUpperCase());
            requestBody.put("password", password);
        } catch (JSONException e) {
            Toast.makeText(this, "Errore nella creazione della richiesta", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingProgressBar.setVisibility(View.GONE);
                        loginButton.setEnabled(true);
                        try {
                            // Verifica risposta server
                            boolean success = response.optBoolean("success", true); 

                            if (success) {
                                Toast.makeText(LoginActivity.this, "Login effettuato!", Toast.LENGTH_SHORT).show();
                                
                                // Naviga verso il Menu del Nucleo Familiare (GnfActivity)
                                Intent intent = new Intent(LoginActivity.this, GnfActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = response.optString("message", "Credenziali errate.");
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Errore risposta server", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingProgressBar.setVisibility(View.GONE);
                        loginButton.setEnabled(true);
                        String err = (error.getMessage() != null) ? error.getMessage() : "Errore generico";
                        Toast.makeText(LoginActivity.this, "Errore login: " + err, Toast.LENGTH_LONG).show();
                    }
                });

        // Usa il Singleton per inviare la richiesta
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}