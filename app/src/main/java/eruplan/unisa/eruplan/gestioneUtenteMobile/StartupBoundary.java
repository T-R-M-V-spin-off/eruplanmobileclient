package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import eruplan.unisa.eruplan.R;

/**
 * Activity di avvio dell'applicazione.
 * Permette all'utente di scegliere se effettuare il Login o registrarsi (Sign Up).
 */
public class StartupBoundary extends AppCompatActivity {

    private Button btnGotoLogin;
    private Button btnGotoSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        btnGotoLogin = findViewById(R.id.btn_goto_login);
        btnGotoSignup = findViewById(R.id.btn_goto_signup);

        btnGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apre la schermata di Login
                Intent intent = new Intent(StartupBoundary.this, LoginBoundary.class);
                startActivity(intent);
            }
        });

        btnGotoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apre la schermata di Registrazione
                Intent intent = new Intent(StartupBoundary.this, SignupBoundary.class);
                startActivity(intent);
            }
        });
    }
}