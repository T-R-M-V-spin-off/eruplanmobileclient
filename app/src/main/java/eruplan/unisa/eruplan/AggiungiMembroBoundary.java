package eruplan.unisa.eruplan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Rappresenta l'interfaccia utente (Boundary) per l'inserimento di un nuovo membro del nucleo familiare.
 */
public class AggiungiMembroBoundary extends AppCompatActivity {

    // Componenti dell'interfaccia utente
    private EditText nomeEditText, cognomeEditText, codiceFiscaleEditText, dataNascitaEditText;
    private RadioGroup sessoRadioGroup;
    private RadioButton radioMaschio, radioFemmina;
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
        radioMaschio = findViewById(R.id.radioMaschio);
        radioFemmina = findViewById(R.id.radioFemmina);
        assistenzaCheckBox = findViewById(R.id.assistenzaCheckBox);
        minorenneCheckBox = findViewById(R.id.minorenneCheckBox);
        submitButton = findViewById(R.id.submitButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        dataNascitaEditText.setFocusable(false);
    }

    /**
     * Mostra un DatePickerDialog per permettere all'utente di selezionare la data di nascita.
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // CORREZIONE: Formato allineato a dd-MM-yyyy per coerenza.
                    String selectedDate = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear);
                    dataNascitaEditText.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    /**
     * Raccoglie i dati dal form, avvia il processo di inserimento tramite il Control
     * e gestisce la risposta in modo asincrono.
     */
    private void aggiungiMembro() {
        String nome = nomeEditText.getText().toString().trim();
        String cognome = cognomeEditText.getText().toString().trim();
        String codiceFiscale = codiceFiscaleEditText.getText().toString().trim();
        String dataNascita = dataNascitaEditText.getText().toString().trim();

        int selectedSessoId = sessoRadioGroup.getCheckedRadioButtonId();
        String sesso = "";
        if (selectedSessoId == radioMaschio.getId()) {
            sesso = "M";
        } else if (selectedSessoId == radioFemmina.getId()) {
            sesso = "F";
        }

        boolean assistenza = assistenzaCheckBox.isChecked();
        boolean minorenne = minorenneCheckBox.isChecked();

        try {
            loadingProgressBar.setVisibility(View.VISIBLE);
            submitButton.setEnabled(false);

            gestioneNucleoFamiliareControl.inserisciMembro(
                nome, cognome, codiceFiscale, dataNascita, sesso, assistenza, minorenne,
                new GestioneNucleoFamiliareControl.ControlCallback() {
                    @Override
                    public void onInserimentoSuccesso(String message) {
                        loadingProgressBar.setVisibility(View.GONE);
                        submitButton.setEnabled(true);
                        Toast.makeText(AggiungiMembroBoundary.this, message, Toast.LENGTH_LONG).show();

                        //Intent intent = new Intent(AggiungiMembroBoundary.this, VisualizzaMembroBoundary.class);
                        //startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onInserimentoErrore(String message) {
                        loadingProgressBar.setVisibility(View.GONE);
                        submitButton.setEnabled(true);
                        Toast.makeText(AggiungiMembroBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
                    }
                }
            );

        } catch (IllegalArgumentException e) {
            loadingProgressBar.setVisibility(View.GONE);
            submitButton.setEnabled(true);
            Toast.makeText(this, "Errore di validazione: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
