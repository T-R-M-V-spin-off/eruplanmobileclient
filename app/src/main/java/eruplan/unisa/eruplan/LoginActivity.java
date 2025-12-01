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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Gestisce il processo di login dell'utente comunicando con un server web per l'autenticazione.
 * <p>
 * L'activity invia le credenziali (codice fiscale e password) a un endpoint del server.
 * In base alla risposta del server, l'utente viene autenticato o riceve un messaggio di errore.
 * </p>
 */
public class LoginActivity extends Activity {

    // --- PLACEHOLDER: Sostituisci con l'URL reale del tuo endpoint di login ---
    private static final String LOGIN_URL = "https://your.server.url/api/login";

    private EditText codiceFiscaleEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inizializza la coda di richieste di Volley
        requestQueue = Volley.newRequestQueue(this);

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

    /**
     * Tenta di autenticare l'utente inviando le credenziali al server web.
     */
    private void loginUser() {
        String codiceFiscale = codiceFiscaleEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(codiceFiscale)) {
            Toast.makeText(getApplicationContext(), "Inserisci il Codice Fiscale!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aggiunto controllo sulla lunghezza del Codice Fiscale
        if (codiceFiscale.length() != 16) {
            Toast.makeText(getApplicationContext(), "Il Codice Fiscale deve essere di 16 caratteri.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Inserisci la password!", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- PLACEHOLDER: Struttura JSON della richiesta ---
        // Assicurati che le chiavi ("codice_fiscale", "password") corrispondano a ciò che il server si aspetta.
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("codice_fiscale", codiceFiscale.toUpperCase());
            requestBody.put("password", password);
        } catch (JSONException e) {
            // Questo errore non dovrebbe accadere in condizioni normali
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
                            // --- PLACEHOLDER: Interpretazione della risposta del server ---
                            // Questo è un esempio. Adatta la logica alla risposta reale del tuo server.
                            boolean success = response.getBoolean("success");

                            if (success) {
                                Toast.makeText(LoginActivity.this, "Autenticazione riuscita.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                String message = response.optString("message", "Credenziali non valide.");
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // Errore nel parsing della risposta JSON
                            Toast.makeText(LoginActivity.this, "Errore nella risposta del server", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingProgressBar.setVisibility(View.GONE);
                        loginButton.setEnabled(true);

                        // Gestione degli errori di rete (es. server non raggiungibile, timeout)
                        Toast.makeText(LoginActivity.this, "Errore di connessione: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Aggiungi la richiesta alla coda
        requestQueue.add(jsonObjectRequest);
    }
}
