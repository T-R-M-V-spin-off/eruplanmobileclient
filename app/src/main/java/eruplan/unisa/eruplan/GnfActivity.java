package eruplan.unisa.eruplan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class GnfActivity extends AppCompatActivity {

    // Riferimento al pulsante di logout nel layout.
    private Button logoutButton;

    // Classe di destinazione (dove reindirizziamo l'utente dopo il logout)
    // Per ora non esiste una classe per la schermata d'accesso quindi usiamo LoginActivity
    private final Class<?> LOGIN_ACTIVITY_CLASS = LoginActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carica il layout del menu GNF
        setContentView(R.layout.activity_gnf);

        // Collega il pulsante "Logout" dal layout (dobbiamo decidere l'id).
        logoutButton = findViewById(R.id.btnLogout);

        // Gestisci il click del logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia il processo di logout.
                performLogout();
            }
        });
    }

    // Metodo per eseguire il logout
    private void performLogout() {

        // Navigazione alla schermata di accesso.
        navigateToLoginScreen();
    }

    // Metodo per reindirizzare l'utente alla schermata di accesso
    private void navigateToLoginScreen() {
        Intent intent = new Intent(GnfActivity.this, LOGIN_ACTIVITY_CLASS);

        // I flag servono a impedire all'utente di tornare al menu dopo il logout.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        // Chiude definitivamente questa Activity.
        finish();
    }
}