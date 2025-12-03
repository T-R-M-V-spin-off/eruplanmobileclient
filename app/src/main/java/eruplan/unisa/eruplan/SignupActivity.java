package eruplan.unisa.eruplan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    private EditText etNome, etCognome, etCodiceFiscale, etDataNascita, etSesso;
    private EditText etPassword, etConfirmPassword;
    private Button btnSignUp, btnBack;
    private ProgressBar progressBar;

    // URL del server per la registrazione
    private static final String SIGNUP_URL = "https://eruplanserver.azurewebsites.net/autenticazione/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();

        etDataNascita.setOnClickListener(v -> showDatePicker());

        btnSignUp.setOnClickListener(v -> attemptRegistration());

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etNome = findViewById(R.id.et_nome);
        etCognome = findViewById(R.id.et_cognome);
        etCodiceFiscale = findViewById(R.id.et_codice_fiscale);
        etDataNascita = findViewById(R.id.et_data_nascita);
        etSesso = findViewById(R.id.et_sesso);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btn_signup);
        btnBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progressBar);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    etDataNascita.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void attemptRegistration() {
        String nome = etNome.getText().toString().trim();
        String cognome = etCognome.getText().toString().trim();
        String cf = etCodiceFiscale.getText().toString().trim();
        String data = etDataNascita.getText().toString().trim();
        String sesso = etSesso.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // Validazione
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cognome) || TextUtils.isEmpty(cf)) {
            showToast("Compila tutti i dati anagrafici!");
            return;
        }

        if (TextUtils.isEmpty(data)) {
            showToast("Inserisci la data di nascita.");
            return;
        }

        if (cf.length() != 16) {
            etCodiceFiscale.setError("Codice fiscale non valido (16 caratteri)");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Minimo 6 caratteri");
            return;
        }

        if (!password.equals(confirmPass)) {
            etConfirmPassword.setError("Le password non coincidono");
            return;
        }

        // Preparazione richiesta
        btnSignUp.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("nome", nome);
            requestBody.put("cognome", cognome);
            requestBody.put("codiceFiscale", cf);
            requestBody.put("dataDiNascita", data);
            requestBody.put("sesso", sesso);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SIGNUP_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        btnSignUp.setEnabled(true);

                        try {
                            boolean success = response.optBoolean("success", true);
                            String message = response.optString("message", "Registrazione avvenuta con successo.");

                            if (success) {
                                showToast(message);
                                Intent intent = new Intent(SignupActivity.this, GnfActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Errore: " + message);
                            }
                        } catch (Exception e) {
                            showToast("Errore nella risposta del server.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        btnSignUp.setEnabled(true);
                        showToast("Errore di connessione: " + error.getMessage());
                    }
                });

        // Usa il Singleton
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}