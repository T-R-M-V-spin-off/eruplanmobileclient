package eruplan.unisa.eruplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Gestisce l'interfaccia utente per il login.
 */
public class LoginBoundary extends AppCompatActivity {

    private EditText codiceFiscaleEditText, passwordEditText;
    private Button loginButton, btnToSignUp;
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

        btnToSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginBoundary.this, SignupBoundary.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        codiceFiscaleEditText = findViewById(R.id.et_username);
        passwordEditText = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        btnToSignUp = findViewById(R.id.btn_to_signup);
        loadingProgressBar = findViewById(R.id.progressBar);
    }

    private void attemptLogin() {
        String codiceFiscale = codiceFiscaleEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Prepara l'UI per l'attesa
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Chiama il Control per avviare il processo di login
        gestioneUtenteControl.login(codiceFiscale, password, new GestioneUtenteControl.ControlCallback() {
            @Override
            public void onOperazioneSuccess(String message) {
                // Operazione riuscita
                loadingProgressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                Toast.makeText(LoginBoundary.this, message, Toast.LENGTH_SHORT).show();

                // Reindirizza l'utente alla schermata principale
                Intent intent = new Intent(LoginBoundary.this, CosaVuoiFareBoundary.class); // o MainActivity, etc.
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onOperazioneError(String message) {
                // Gestisce l'errore (validazione o rete)
                loadingProgressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                Toast.makeText(LoginBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
