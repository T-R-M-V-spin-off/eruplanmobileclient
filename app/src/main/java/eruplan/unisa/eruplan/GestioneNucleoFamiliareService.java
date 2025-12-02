package eruplan.unisa.eruplan;

import android.content.Context;
import java.util.regex.Pattern;

/**
 * Contiene la logica di business (business logic) per la gestione del nucleo familiare.
 * Questa classe ha la responsabilità di validare i dati secondo le regole dell'applicazione
 * prima che questi vengano salvati, garantendo l'integrità del sistema.
 * Funge da intermediario tra il Control e il Repository.
 */
public class GestioneNucleoFamiliareService {

    /** Pattern per la validazione del formato della data (gg/mm/aaaa). */
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "^((0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((19|20)\\d{2}))$");

    private GestioneNucleoFamiliareRepository repository;

    /**
     * Interfaccia di callback per notificare al Control l'esito dell'operazione di salvataggio,
     * una volta che questa è stata completata dal Repository.
     */
    public interface ServiceCallback {
        /** Chiamato quando il salvataggio è andato a buon fine. */
        void onSalvataggioSuccess(String message);
        /** Chiamato quando si è verificato un errore durante il salvataggio. */
        void onSalvataggioError(String message);
    }

    /**
     * Costruttore del Service.
     * @param context Il contesto necessario per inizializzare il Repository.
     */
    public GestioneNucleoFamiliareService(Context context) {
        this.repository = new GestioneNucleoFamiliareRepository(context);
    }

    /**
     * Esegue la validazione dei dati e, se corretti, avvia il processo di salvataggio.
     * Questo metodo rappresenta il "guardiano" delle regole di business.
     *
     * @param nome Dati grezzi provenienti dal livello superiore (Control).
     * @param cognome Dati grezzi provenienti dal livello superiore (Control).
     * @param codiceFiscale Dati grezzi provenienti dal livello superiore (Control).
     * @param dataDiNascita Dati grezzi provenienti dal livello superiore (Control).
     * @param sesso Dati grezzi provenienti dal livello superiore (Control).
     * @param assistenza Dati grezzi provenienti dal livello superiore (Control).
     * @param minorenne Dati grezzi provenienti dal livello superiore (Control).
     * @param serviceCallback Il callback per notificare al Control l'esito finale dell'operazione.
     * @throws IllegalArgumentException se uno dei controlli di validazione fallisce.
     */
    public void creaMembro(String nome, String cognome, String codiceFiscale, String dataDiNascita, String sesso, boolean assistenza, boolean minorenne, final ServiceCallback serviceCallback) throws IllegalArgumentException {
        
        // 1. Pulizia e normalizzazione dell'input. Pratica di programmazione difensiva.
        final String nomeTrimmed = (nome != null) ? nome.trim() : null;
        final String cognomeTrimmed = (cognome != null) ? cognome.trim() : null;
        final String cfTrimmed = (codiceFiscale != null) ? codiceFiscale.trim() : null;

        // 2. Esecuzione della validazione secondo le regole di business.
        if (nomeTrimmed == null || nomeTrimmed.length() < 2 || nomeTrimmed.length() > 30) {
            throw new IllegalArgumentException("Il nome non è valido.");
        }
        if (cognomeTrimmed == null || cognomeTrimmed.length() < 2 || cognomeTrimmed.length() > 30) {
            throw new IllegalArgumentException("Il cognome non è valido.");
        }
        if (cfTrimmed == null || cfTrimmed.length() != 16) {
            throw new IllegalArgumentException("Il codice fiscale deve essere di 16 caratteri.");
        }
        if (dataDiNascita == null || !DATE_PATTERN.matcher(dataDiNascita).matches()) {
            throw new IllegalArgumentException("La data di nascita non è valida (formato richiesto: gg/mm/aaaa).");
        }
        if (sesso == null || (!sesso.equals("M") && !sesso.equals("F"))) {
            throw new IllegalArgumentException("Il sesso non è valido.");
        }

        // 3. Se la validazione ha successo, crea l'oggetto Entità.
        Membro nuovoMembro = new Membro(nomeTrimmed, cognomeTrimmed, cfTrimmed, dataDiNascita, sesso, assistenza, minorenne);

        // 4. Inoltra l'entità al Repository per la persistenza e gestisce la risposta asincrona.
        repository.salvaMembro(nuovoMembro, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                // Il Repository ha finito, notifichiamo il nostro chiamante (il Control).
                serviceCallback.onSalvataggioSuccess(message);
            }

            @Override
            public void onError(String message) {
                // Il Repository ha fallito, notifichiamo il nostro chiamante (il Control).
                serviceCallback.onSalvataggioError(message);
            }
        });
    }
}
