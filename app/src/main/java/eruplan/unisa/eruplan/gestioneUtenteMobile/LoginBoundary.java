package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import eruplan.unisa.eruplan.R;

/**
 * Gestisce l'interfaccia utente per il login, implementando la callback specifica per il login.
 */
public class LoginBoundary extends AppCompatActivity implements GestioneUtenteControl.LoginCallback {

    private EditText codiceFiscaleEditText, passwordEditText;
    private MaterialButton loginButton;
    private ProgressBar loadingProgressBar;

    private GestioneUtenteControl gestioneUtenteControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inizializza il Control
        gestioneUtenteControl = new GestioneUtenteControl(this);

        initViews();

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void initViews() {
        codiceFiscaleEditText = findViewById(R.id.et_codice_fiscale);
        passwordEditText = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.progressBar);
    }

    private void attemptLogin() {
        String codiceFiscale = codiceFiscaleEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validazione dell'input
        if (TextUtils.isEmpty(codiceFiscale)) {
            codiceFiscaleEditText.setError(getString(R.string.empty_cf_error));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.empty_password_error));
            return;
        }

        // Prepara l'UI per l'attesa
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Chiama il Control per avviare il processo di login con la nuova callback
        gestioneUtenteControl.login(codiceFiscale, password, this);
    }

    @Override
    public void onLoginSuccess(String message) {
        // Operazione riuscita
        runOnUiThread(() -> {
            loadingProgressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            Toast.makeText(LoginBoundary.this, message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onLoginError(String message) {
        // Gestisce l'errore (validazione o rete)
        runOnUiThread(() -> {
            loadingProgressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            Toast.makeText(LoginBoundary.this, getString(R.string.login_error, message), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onLoginRedirect() {
        // Il redirect è gestito nel Control, qui chiudiamo la boundary
        finish();
    }
}
