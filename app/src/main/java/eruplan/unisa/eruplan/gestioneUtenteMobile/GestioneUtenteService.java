package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.content.Context;
import eruplan.unisa.eruplan.utility.Validator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Contiene la logica di business per la gestione dell'utente.
 * Valida i dati e orchestra le operazioni tramite il Repository.
 */
public class GestioneUtenteService {

    private final GestioneUtenteRepository repository;

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
        if (codiceFiscale == null || codiceFiscale.trim().isEmpty()) {
            callback.onError("Il codice fiscale è obbligatorio.");
            return;
        }
        if (!Validator.isCodiceFiscaleValid(codiceFiscale)) {
            callback.onError("Formato codice fiscale non valido.");
            return;
        }

        if (password == null || password.isEmpty()) {
            callback.onError("La password è obbligatoria.");
            return;
        }
        if (!Validator.isPasswordValid(password)) {
            callback.onError("Password non valida. La password deve contenere almeno 8 caratteri, di cui una lettera maiuscola, una minuscola e un numero.");
            return;
        }

        // Normalizziamo il CF in maiuscolo per evitare problemi di case-sensitivity
        String cfUpper = codiceFiscale.toUpperCase();

        repository.login(cfUpper, password, new GestioneUtenteRepository.RepositoryCallback() {
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
     * Avvia il processo di logout.
     */
    public void logout(final ServiceCallback callback) {
        repository.logout(new GestioneUtenteRepository.RepositoryCallback() {
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
        if (nome == null || nome.trim().isEmpty()) {
            callback.onError("Il nome è obbligatorio.");
            return;
        }
        if (!Validator.isNomeValid(nome)) {
            callback.onError("Formato nome non valido.");
            return;
        }

        if (cognome == null || cognome.trim().isEmpty()) {
            callback.onError("Il cognome è obbligatorio.");
            return;
        }
        if (!Validator.isCognomeValid(cognome)) {
            callback.onError("Formato cognome non valido.");
            return;
        }

        if (cf == null || cf.trim().isEmpty()) {
            callback.onError("Il codice fiscale è obbligatorio.");
            return;
        }
        if (!Validator.isCodiceFiscaleValid(cf)) {
            callback.onError("Formato codice fiscale non valido.");
            return;
        }

        if (data == null || data.trim().isEmpty()) {
            callback.onError("La data di nascita è obbligatoria.");
            return;
        }
        LocalDate localDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            localDate = LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            callback.onError("Formato data non valido. Usa dd-MM-yyyy.");
            return;
        }

        if (!Validator.isDataNascitaValid(localDate)) {
            callback.onError("Data di nascita non valida (deve essere nel passato).");
            return;
        }

        if (sesso == null || sesso.isEmpty() || !Validator.isSessoValid(sesso)) {
            callback.onError("È obbligatorio specificare il sesso.");
            return;
        }

        if (password == null || password.isEmpty()) {
            callback.onError("La password è obbligatoria.");
            return;
        }
        if (!Validator.isPasswordValid(password)) {
            callback.onError("La password deve contenere almeno 8 caratteri, di cui una lettera maiuscola, una minuscola e un numero.");
            return;
        }
        if (!password.equals(confirmPass)) {
            callback.onError("Le password non coincidono.");
            return;
        }

        // Normalizziamo il CF in maiuscolo
        String cfUpper = cf.toUpperCase();

        repository.registra(nome, cognome, cfUpper, data, sesso, password, new GestioneUtenteRepository.RepositoryCallback() {
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
