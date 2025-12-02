package eruplan.unisa.eruplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity di avvio dell'applicazione.
 * Permette all'utente di scegliere se effettuare il Login o registrarsi (Sign Up).
 */
public class StartupActivity extends AppCompatActivity {

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
                Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnGotoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apre la schermata di Registrazione
                Intent intent = new Intent(StartupActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}