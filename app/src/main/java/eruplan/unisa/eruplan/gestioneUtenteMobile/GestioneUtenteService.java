package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.content.Context;
import android.text.TextUtils;
import java.util.regex.Pattern;

/**
 * Contiene la logica di business per la gestione dell'utente.
 * Valida i dati e orchestra le operazioni tramite il Repository.
 */
public class GestioneUtenteService {

    private GestioneUtenteRepository repository;
    
    // Regex per validare la data nel formato dd-mm-yyyy
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "^((0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-((19|20)\\d{2}))$");

    public interface ServiceCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public GestioneUtenteService(Context context) {
        this.repository = new GestioneUtenteRepository(context);
    }

    /**
     * Metodo di utilità per validare e trimmare stringhe.
     */
    private String validateAndTrim(String value, int minLength, int maxLength, String errorMessage) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        String trimmedValue = value.trim();
        if (trimmedValue.length() < minLength || trimmedValue.length() > maxLength) {
            throw new IllegalArgumentException(errorMessage);
        }
        return trimmedValue;
    }

    /**
     * Esegue la validazione e avvia il processo di login.
     */
    public void login(String codiceFiscale, String password, final ServiceCallback callback) {
        try {
            // Validazione
            String cfTrimmed = validateAndTrim(codiceFiscale, 16, 16, "Il codice fiscale deve essere di 16 caratteri.");
            // Normalizziamo il CF in maiuscolo per evitare problemi di case-sensitivity
            cfTrimmed = cfTrimmed.toUpperCase();
            
            // Per la password non facciamo trim o uppercase, la passiamo com'è (solo check vuoto)
            if (TextUtils.isEmpty(password)) {
                throw new IllegalArgumentException("La password non può essere vuota.");
            }

            repository.login(cfTrimmed, password, new GestioneUtenteRepository.RepositoryCallback() {
                @Override
                public void onSuccess(String message) {
                    callback.onSuccess(message);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });

        } catch (IllegalArgumentException e) {
            callback.onError(e.getMessage());
        }
    }

    /**
     * Esegue la validazione e avvia il processo di registrazione.
     */
    public void registra(String nome, String cognome, String cf, String data, String sesso, String password, String confirmPass, final ServiceCallback callback) {
        try {
            // Validazione campi stringa
            String nomeTrimmed = validateAndTrim(nome, 2, 30, "Nome troppo breve o mancante.");
            String cognomeTrimmed = validateAndTrim(cognome, 2, 30, "Cognome troppo breve o mancante.");
            String cfTrimmed = validateAndTrim(cf, 16, 16, "Il codice fiscale deve essere di 16 caratteri.");
            cfTrimmed = cfTrimmed.toUpperCase();

            // Validazione data
            if (TextUtils.isEmpty(data) || !DATE_PATTERN.matcher(data).matches()) {
                throw new IllegalArgumentException("La data di nascita non è valida (formato richiesto: dd-mm-aaaa).");
            }

            // Validazione sesso
            if (TextUtils.isEmpty(sesso) || (!sesso.equals("M") && !sesso.equals("F"))) {
                throw new IllegalArgumentException("Il sesso deve essere 'M' o 'F'.");
            }

            // Validazione password
            if (TextUtils.isEmpty(password) || password.length() < 8) {
                throw new IllegalArgumentException("La password deve essere di almeno 8 caratteri.");
            }
            if (!password.equals(confirmPass)) {
                throw new IllegalArgumentException("Le password non coincidono.");
            }

            repository.registra(nomeTrimmed, cognomeTrimmed, cfTrimmed, data, sesso, password, new GestioneUtenteRepository.RepositoryCallback() {
                @Override
                public void onSuccess(String message) {
                    callback.onSuccess(message);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });

        } catch (IllegalArgumentException e) {
            callback.onError(e.getMessage());
        }
    }
}
