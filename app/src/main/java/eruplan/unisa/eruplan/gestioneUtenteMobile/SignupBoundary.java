package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

import eruplan.unisa.eruplan.R;

public class SignupBoundary extends AppCompatActivity implements GestioneUtenteControl.SignupCallback {

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

        gestioneUtenteControl = new GestioneUtenteControl(this);

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
        if (!validateInput()) {
            return;
        }

        String nome = etNome.getText().toString().trim();
        String cognome = etCognome.getText().toString().trim();
        String cf = etCodiceFiscale.getText().toString().trim();
        String data = etDataNascita.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        int selectedSessoId = etSesso.getCheckedRadioButtonId();
        String sesso = (selectedSessoId == R.id.radioMaschio) ? "M" : "F";

        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false);

        gestioneUtenteControl.registra(nome, cognome, cf, data, sesso, password, confirmPass, this);
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(etNome.getText())) {
            etNome.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(etCognome.getText())) {
            etCognome.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(etCodiceFiscale.getText())) {
            etCodiceFiscale.setError(getString(R.string.empty_cf_error));
            return false;
        }
        if (etCodiceFiscale.getText().length() != 16) {
            etCodiceFiscale.setError(getString(R.string.invalid_cf_length_error));
            return false;
        }
        if (TextUtils.isEmpty(etDataNascita.getText())) {
            etDataNascita.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (etSesso.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.gender_not_selected_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError(getString(R.string.empty_password_error));
            return false;
        }
        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            etConfirmPassword.setError(getString(R.string.password_mismatch_error));
            return false;
        }
        return true;
    }

    @Override
    public void onSignupSuccess(String message) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            btnSignUp.setEnabled(true);
            Toast.makeText(SignupBoundary.this, R.string.signup_success, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onSignupError(String message) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            btnSignUp.setEnabled(true);
            Toast.makeText(SignupBoundary.this, getString(R.string.signup_error, message), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onSignupRedirect() {
        finish();
    }
}
