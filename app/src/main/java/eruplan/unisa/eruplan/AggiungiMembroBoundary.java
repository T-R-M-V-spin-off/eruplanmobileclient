package eruplan.unisa.eruplan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Rappresenta l'interfaccia utente (Boundary) per l'inserimento di un nuovo membro del nucleo familiare.
 * Questa classe è responsabile di:
 * 1. Disegnare e gestire i componenti dell'interfaccia (EditText, Button, etc.).
 * 2. Raccogliere l'input dell'utente.
 * 3. Inoltrare la richiesta di inserimento al livello di Control.
 * 4. Gestire l'esito finale dell'operazione (successo o errore) aggiornando l'UI.
 */
public class AggiungiMembroBoundary extends AppCompatActivity {

    // Componenti dell'interfaccia utente
    private EditText nomeEditText, cognomeEditText, codiceFiscaleEditText, dataNascitaEditText;
    private RadioGroup sessoRadioGroup;
    private RadioButton radioMaschio, radioFemmina;
    private CheckBox assistenzaCheckBox, minorenneCheckBox;
    private Button submitButton;
    private ProgressBar loadingProgressBar;

    // Riferimento al livello di Control
    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_membro);

        // Inizializza il livello di Control, passando il contesto necessario.
        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl(this);

        // Associa le variabili ai componenti definiti nel file XML del layout.
        initViews();

        // Imposta il listener per gestire il click sul pulsante di invio.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia il processo di aggiunta del membro quando il pulsante viene cliccato.
                aggiungiMembro();
            }
        });
    }

    /**
     * Metodo di utility per inizializzare i componenti della vista.
     */
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
    }

    /**
     * Raccoglie i dati dal form, avvia il processo di inserimento tramite il Control
     * e gestisce la risposta in modo asincrono.
     */
    private void aggiungiMembro() {
        // 1. Raccoglie i dati inseriti dall'utente e li "pulisce" (es. con trim()).
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
            // 2. Prepara l'UI per l'operazione asincrona (mostra il loader).
            loadingProgressBar.setVisibility(View.VISIBLE);
            submitButton.setEnabled(false);

            // 3. Chiama il Control per avviare l'operazione.
            //    L'Activity passa al Control un'implementazione della sua callback (ControlCallback)
            //    per essere notificata quando l'intera operazione sarà completata.
            gestioneNucleoFamiliareControl.inserisciMembro(
                nome, cognome, codiceFiscale, dataNascita, sesso, assistenza, minorenne,
                new GestioneNucleoFamiliareControl.ControlCallback() {
                    @Override
                    public void onInserimentoSuccesso(String message) {
                        // 4a. Il Control ha notificato che l'operazione è riuscita.
                        //     Aggiorniamo l'UI di conseguenza.
                        loadingProgressBar.setVisibility(View.GONE);
                        submitButton.setEnabled(true);
                        Toast.makeText(AggiungiMembroBoundary.this, message, Toast.LENGTH_LONG).show();

                        // Reindirizza l'utente e chiude questa activity.
                        //Intent intent = new Intent(AggiungiMembroActivity.this, VisualizzaMembroBoundary.class);
                        //startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onInserimentoErrore(String message) {
                        // 4b. Il Control ha notificato un errore.
                        //     Aggiorniamo l'UI mostrando l'errore.
                        loadingProgressBar.setVisibility(View.GONE);
                        submitButton.setEnabled(true);
                        Toast.makeText(AggiungiMembroBoundary.this, "Errore: " + message, Toast.LENGTH_LONG).show();
                    }
                }
            );

        } catch (IllegalArgumentException e) {
            // 5. Gestisce gli errori di validazione, che sono sincroni e immediati.
            //    Questi errori vengono lanciati dal Service se i dati non sono validi.
            loadingProgressBar.setVisibility(View.GONE);
            submitButton.setEnabled(true);
            Toast.makeText(this, "Errore di validazione: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
