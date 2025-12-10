package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.callback.GenericCallback;

public class AggiungiMembroBoundary extends AppCompatActivity {

    private EditText nomeEditText, cognomeEditText, codiceFiscaleEditText, dataNascitaEditText;
    private RadioGroup sessoRadioGroup;
    private CheckBox assistenzaCheckBox, minorenneCheckBox;
    private Button submitButton;
    private ProgressBar loadingProgressBar;

    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_membro);

        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);
        initViews();

        dataNascitaEditText.setOnClickListener(v -> showDatePicker());
        submitButton.setOnClickListener(v -> aggiungiMembro());
    }

    private void initViews() {
        nomeEditText = findViewById(R.id.nomeEditText);
        cognomeEditText = findViewById(R.id.cognomeEditText);
        codiceFiscaleEditText = findViewById(R.id.codiceFiscaleEditText);
        dataNascitaEditText = findViewById(R.id.dataNascitaEditText);
        sessoRadioGroup = findViewById(R.id.sessoRadioGroup);
        assistenzaCheckBox = findViewById(R.id.assistenzaCheckBox);
        minorenneCheckBox = findViewById(R.id.minorenneCheckBox);
        submitButton = findViewById(R.id.submitButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        dataNascitaEditText.setFocusable(false);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear);
                    dataNascitaEditText.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(nomeEditText.getText())) {
            nomeEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(cognomeEditText.getText())) {
            cognomeEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (TextUtils.isEmpty(codiceFiscaleEditText.getText())) {
            codiceFiscaleEditText.setError(getString(R.string.empty_cf_error));
            return false;
        }
        if (codiceFiscaleEditText.getText().length() != 16) {
            codiceFiscaleEditText.setError(getString(R.string.invalid_cf_length_error));
            return false;
        }
        if (TextUtils.isEmpty(dataNascitaEditText.getText())) {
            dataNascitaEditText.setError(getString(R.string.empty_field_error));
            return false;
        }
        if (sessoRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.gender_not_selected_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void aggiungiMembro() {
        if (!validateInput()) {
            return;
        }

        String nome = nomeEditText.getText().toString().trim();
        String cognome = cognomeEditText.getText().toString().trim();
        String codiceFiscale = codiceFiscaleEditText.getText().toString().trim();
        String dataNascita = dataNascitaEditText.getText().toString().trim();

        RadioButton selectedRadioButton = findViewById(sessoRadioGroup.getCheckedRadioButtonId());
        String sesso = selectedRadioButton.getText().toString().startsWith("M") ? "M" : "F";

        boolean assistenza = assistenzaCheckBox.isChecked();
        boolean minorenne = minorenneCheckBox.isChecked();

        loadingProgressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);

        gestioneNucleoFamiliareControl.inserisciMembro(
                nome, cognome, codiceFiscale, dataNascita, sesso, assistenza, minorenne,
                new GenericCallback() {
                    @Override
                    public void onSuccess(String message) {
                        loadingProgressBar.setVisibility(View.GONE);
                        submitButton.setEnabled(true);
                        Toast.makeText(AggiungiMembroBoundary.this, message, Toast.LENGTH_LONG).show();
                        // Ritorna alla schermata precedente
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        loadingProgressBar.setVisibility(View.GONE);
                        submitButton.setEnabled(true);
                        Toast.makeText(AggiungiMembroBoundary.this, getString(R.string.generic_error, message), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
