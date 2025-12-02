package eruplan.unisa.eruplan;

import android.content.Context;
import java.util.regex.Pattern;

/**
 * Contiene la logica di business (business logic) per la gestione del nucleo familiare.
 */
public class GestioneNucleoFamiliareService {

    private static final Pattern DATE_PATTERN = Pattern.compile(
        "^((0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((19|20)\\d{2}))$");

    private GestioneNucleoFamiliareRepository repository;

    public interface ServiceCallback {
        void onSalvataggioSuccess(String message);
        void onSalvataggioError(String message);
    }

    public GestioneNucleoFamiliareService(Context context) {
        this.repository = new GestioneNucleoFamiliareRepository(context);
    }

    /**
     * Metodo di utility per validare e "pulire" una stringa.
     * Controlla che la stringa non sia nulla e che la sua lunghezza (dopo il trim)
     * rientri in un intervallo specificato. Gestisce anche controlli di lunghezza esatta
     * passando lo stesso valore a minLength e maxLength.
     *
     * @param value La stringa da validare.
     * @param minLength La lunghezza minima consentita (inclusa).
     * @param maxLength La lunghezza massima consentita (inclusa).
     * @param errorMessage Il messaggio da lanciare in caso di errore.
     * @return La stringa "pulita" (trimmed).
     * @throws IllegalArgumentException se la validazione fallisce.
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

    public void creaMembro(String nome, String cognome, String codiceFiscale, String dataDiNascita, String sesso, boolean assistenza, boolean minorenne, final ServiceCallback serviceCallback) throws IllegalArgumentException {
        // La validazione delle stringhe è ora delegata al metodo di utility.
        final String nomeTrimmed = validateAndTrim(nome, 2, 30, "Il nome non è valido.");
        final String cognomeTrimmed = validateAndTrim(cognome, 2, 30, "Il cognome non è valido.");
        final String cfTrimmed = validateAndTrim(codiceFiscale, 16, 16, "Il codice fiscale deve essere di 16 caratteri.");

        // Validazioni specifiche che non riguardano la lunghezza della stringa.
        if (dataDiNascita == null || !DATE_PATTERN.matcher(dataDiNascita).matches()) {
            throw new IllegalArgumentException("La data di nascita non è valida (formato richiesto: gg/mm/aaaa).");
        }
        if (sesso == null || (!sesso.equals("M") && !sesso.equals("F"))) {
            throw new IllegalArgumentException("Il sesso non è valido.");
        }

        Membro nuovoMembro = new Membro(nomeTrimmed, cognomeTrimmed, cfTrimmed, dataDiNascita, sesso, assistenza, minorenne);

        repository.salvaMembro(nuovoMembro, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                serviceCallback.onSalvataggioSuccess(message);
            }

            @Override
            public void onError(String message) {
                serviceCallback.onSalvataggioError(message);
            }
        });
    }

    public void creaNucleo(String viaPiazza, String comune, String regione, String paese, String civico, String cap, final ServiceCallback serviceCallback) throws IllegalArgumentException {
        // La validazione delle stringhe è ora delegata al metodo di utility.
        final String viaPiazzaTrimmed = validateAndTrim(viaPiazza, 1, 40, "Via/Piazza non valido.");
        final String comuneTrimmed = validateAndTrim(comune, 1, 40, "Comune non valido.");
        final String regioneTrimmed = validateAndTrim(regione, 1, 40, "Regione non valida.");
        final String paeseTrimmed = validateAndTrim(paese, 1, 40, "Paese non valido.");
        final String civicoTrimmed = validateAndTrim(civico, 1, 5, "Numero civico non valido.");
        final String capTrimmed = validateAndTrim(cap, 5, 5, "CAP non valido. Deve essere di 5 cifre.");

        Nucleo nuovoNucleo = new Nucleo(viaPiazzaTrimmed, comuneTrimmed, regioneTrimmed, paeseTrimmed, civicoTrimmed, capTrimmed);

        repository.salvaNucleo(nuovoNucleo, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                serviceCallback.onSalvataggioSuccess(message);
            }

            @Override
            public void onError(String message) {
                serviceCallback.onSalvataggioError(message);
            }
        });
    }
}
