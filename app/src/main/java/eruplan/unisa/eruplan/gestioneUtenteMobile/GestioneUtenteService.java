package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.content.Context;

import eruplan.unisa.eruplan.R;
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
    private final Context context;

    public interface ServiceCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public GestioneUtenteService(Context context) {
        this.repository = new GestioneUtenteRepository(context);
        this.context = context;
    }

    /**
     * Esegue la validazione e avvia il processo di login.
     */
    public void login(String codiceFiscale, String password, final ServiceCallback callback) {
        if (codiceFiscale == null || codiceFiscale.trim().isEmpty()) {
            callback.onError(context.getString(R.string.validation_cf_mandatory));
            return;
        }
        if (!Validator.isCodiceFiscaleValid(codiceFiscale)) {
            callback.onError(context.getString(R.string.validation_cf_format));
            return;
        }

        if (password == null || password.isEmpty()) {
            callback.onError(context.getString(R.string.validation_password_mandatory));
            return;
        }
        if (!Validator.isPasswordValid(password)) {
            callback.onError(context.getString(R.string.validation_password_format));
            return;
        }

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
            callback.onError(context.getString(R.string.validation_name_mandatory));
            return;
        }
        if (!Validator.isNomeValid(nome)) {
            callback.onError(context.getString(R.string.validation_name_format));
            return;
        }

        if (cognome == null || cognome.trim().isEmpty()) {
            callback.onError(context.getString(R.string.validation_lastname_mandatory));
            return;
        }
        if (!Validator.isCognomeValid(cognome)) {
            callback.onError(context.getString(R.string.validation_lastname_format));
            return;
        }

        if (cf == null || cf.trim().isEmpty()) {
            callback.onError(context.getString(R.string.validation_cf_mandatory));
            return;
        }
        if (!Validator.isCodiceFiscaleValid(cf)) {
            callback.onError(context.getString(R.string.validation_cf_format));
            return;
        }

        if (data == null || data.trim().isEmpty()) {
            callback.onError(context.getString(R.string.validation_birthdate_mandatory));
            return;
        }
        LocalDate localDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            localDate = LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            callback.onError(context.getString(R.string.validation_date_format));
            return;
        }

        if (!Validator.isDataNascitaValid(localDate)) {
            callback.onError(context.getString(R.string.validation_birthdate_past));
            return;
        }

        if (sesso == null || sesso.isEmpty() || !Validator.isSessoValid(sesso)) {
            callback.onError(context.getString(R.string.validation_gender_mandatory));
            return;
        }

        if (password == null || password.isEmpty()) {
            callback.onError(context.getString(R.string.validation_password_mandatory));
            return;
        }
        if (!Validator.isPasswordValid(password)) {
            callback.onError(context.getString(R.string.validation_password_format));
            return;
        }
        if (!password.equals(confirmPass)) {
            callback.onError(context.getString(R.string.password_mismatch_error));
            return;
        }

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
