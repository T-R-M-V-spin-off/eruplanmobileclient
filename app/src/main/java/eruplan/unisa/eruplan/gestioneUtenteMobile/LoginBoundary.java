package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import eruplan.unisa.eruplan.R;

/**
 * Gestisce l'interfaccia utente per il login.
 */
public class LoginBoundary extends AppCompatActivity implements GestioneUtenteControl.ControlCallback {

    private EditText codiceFiscaleEditText, passwordEditText;
    private MaterialButton loginButton;
    private ProgressBar loadingProgressBar;

    private GestioneUtenteControl gestioneUtenteControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inizializza il Control passando il callback
        gestioneUtenteControl = new GestioneUtenteControl(this, this);

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

        // Prepara l'UI per l'attesa
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Chiama il Control per avviare il processo di login
        gestioneUtenteControl.login(codiceFiscale, password);
    }

    @Override
    public void onOperazioneSuccess(String message) {
        // Operazione riuscita
        loadingProgressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);


        Toast.makeText(LoginBoundary.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOperazioneError(String message) {
        // Gestisce l'errore (validazione o rete)
        loadingProgressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);

        Toast.makeText(LoginBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginRedirect() {
        finish();
    }

    @Override
    public void onSignupRedirect() {

    }
}
