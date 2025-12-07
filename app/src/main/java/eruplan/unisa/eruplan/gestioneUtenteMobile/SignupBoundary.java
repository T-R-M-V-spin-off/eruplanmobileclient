package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

import eruplan.unisa.eruplan.R;

public class SignupBoundary extends AppCompatActivity implements GestioneUtenteControl.ControlCallback {

    private EditText etNome, etCognome, etCodiceFiscale, etDataNascita;
    private RadioGroup etSesso;
    private EditText etPassword, etConfirmPassword;
    private MaterialButton btnSignUp, btnBack;
    private ProgressBar progressBar;

    private GestioneUtenteControl gestioneUtenteControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Inizializza il Control passando il callback
        gestioneUtenteControl = new GestioneUtenteControl(this, this);

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year);
                    etDataNascita.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void attemptRegistration() {
        String nome = etNome.getText().toString().trim();
        String cognome = etCognome.getText().toString().trim();
        String cf = etCodiceFiscale.getText().toString().trim();
        String data = etDataNascita.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        int selectedSessoId = etSesso.getCheckedRadioButtonId();
        String sesso = "";
        if (selectedSessoId == R.id.radioMaschio) {
            sesso = "M";
        } else if (selectedSessoId == R.id.radioFemmina) {
            sesso = "F";
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false);

        // Chiama il Control per avviare il processo di registrazione
        gestioneUtenteControl.registra(nome, cognome, cf, data, sesso, password, confirmPass);
    }

    @Override
    public void onOperazioneSuccess(String message) {
        progressBar.setVisibility(View.GONE);
        btnSignUp.setEnabled(true);
        Toast.makeText(SignupBoundary.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOperazioneError(String message) {
        progressBar.setVisibility(View.GONE);
        btnSignUp.setEnabled(true);
        Toast.makeText(SignupBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginRedirect() {
        // Non implementato in questa boundary
    }

    @Override
    public void onSignupRedirect() {
        finish();
    }
}
