package eruplan.unisa.eruplan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    private EditText etNome, etCognome, etCodiceFiscale, etDataNascita, etSesso;
    private EditText etPassword, etConfirmPassword;
    private Button btnSignUp, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();

        etDataNascita.setOnClickListener(v -> showDatePicker());

        btnSignUp.setOnClickListener(v -> attemptRegistration());

        btnBack.setOnClickListener(v -> {
            finish();
        });
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
        // Usate (trim) per togliere gli spazi
        String nome = etNome.getText().toString().trim();
        String cognome = etCognome.getText().toString().trim();
        String cf = etCodiceFiscale.getText().toString().trim();
        String data = etDataNascita.getText().toString().trim();
        String sesso = etSesso.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // Validazione superficiale per ora
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cognome) || TextUtils.isEmpty(cf)) {
            showToast("Compila tutti i dati anagrafici!");
            return;
        }

        if (TextUtils.isEmpty(data)) {
            showToast("Inserisci la data di nascita.");
            return;
        }

        if (etCodiceFiscale.getText().toString().length() != 16) {
            etCodiceFiscale.setError("Codice fiscale non valido");
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

        //Qui andrebbe il codice per salvare nel DB (Room, Firebase, API, ecc.)

        showToast("Registrazione simulata con successo!");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(GnfActivity.class);
        finish();
    }

    // Metodo helper per non scrivere Toast.makeText ogni volta
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}