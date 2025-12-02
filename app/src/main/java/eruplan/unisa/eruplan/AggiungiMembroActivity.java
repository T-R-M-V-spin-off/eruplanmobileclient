package eruplan.unisa.eruplan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AggiungiMembroActivity extends AppCompatActivity {

    private EditText nomeEditText, cognomeEditText, codiceFiscaleEditText, dataNascitaEditText;
    private RadioGroup sessoRadioGroup;
    private RadioButton radioMaschio, radioFemmina;
    private CheckBox assistenzaCheckBox, minorenneCheckBox;
    private Button submitButton;

    private GestioneNucleoFamiliareControl gestioneNucleoFamiliareControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_membro);

        // Inizializza il Control
        gestioneNucleoFamiliareControl = new GestioneNucleoFamiliareControl();

        // Collega i componenti del layout
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggiungiMembro();
            }
        });
    }

    private void aggiungiMembro() {
        // Raccoglie i dati dal form
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
            // Chiama il Control per avviare l'inserimento
            Membro nuovoMembro = gestioneNucleoFamiliareControl.inserisciMembro(
                nome, cognome, codiceFiscale, dataNascita, sesso, assistenza, minorenne
            );

            // Se tutto va a buon fine...
            Toast.makeText(this, "Membro \"" + nuovoMembro.getNome() + " \" aggiunto con successo!", Toast.LENGTH_LONG).show();
            
            // Qui potresti chiudere l'activity o pulire il form
            // finish(); 

        } catch (IllegalArgumentException e) {
            // Cattura gli errori di validazione dal Service e li mostra all'utente
            Toast.makeText(this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
