package eruplan.unisa.eruplan;

import android.content.Context;
import android.text.TextUtils;

/**
 * Contiene la logica di business per la gestione dell'utente.
 * Valida i dati e orchestra le operazioni tramite il Repository.
 */
public class GestioneUtenteService {

    private GestioneUtenteRepository repository;

    public interface ServiceCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public GestioneUtenteService(Context context) {
        this.repository = new GestioneUtenteRepository(context);
    }

    /**
     * Esegue la validazione e avvia il processo di login.
     */
    public void login(String codiceFiscale, String password, final ServiceCallback callback) {
        if (TextUtils.isEmpty(codiceFiscale) || TextUtils.isEmpty(password)) {
            callback.onError("Codice fiscale e password non possono essere vuoti.");
            return;
        }

        repository.login(codiceFiscale, password, new GestioneUtenteRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    /**
     * Esegue la validazione e avvia il processo di registrazione.
     */
    public void registra(String nome, String cognome, String cf, String data, String sesso, String password, String confirmPass, final ServiceCallback callback) {
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cognome) || TextUtils.isEmpty(cf) || TextUtils.isEmpty(data) || TextUtils.isEmpty(sesso)) {
            callback.onError("Tutti i campi sono obbligatori.");
            return;
        }
        if (cf.length() != 16) {
            callback.onError("Il codice fiscale deve essere di 16 caratteri.");
            return;
        }
        if (password.length() < 6) {
            callback.onError("La password deve essere di almeno 6 caratteri.");
            return;
        }
        if (!password.equals(confirmPass)) {
            callback.onError("Le password non coincidono.");
            return;
        }

        repository.registra(nome, cognome, cf, data, sesso, password, new GestioneUtenteRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
}
