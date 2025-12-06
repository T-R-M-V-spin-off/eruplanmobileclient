package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Context;
import java.util.List;
import java.util.regex.Pattern;

import eruplan.unisa.eruplan.entity.AppoggioEntity;
import eruplan.unisa.eruplan.entity.MembroEntity;
import eruplan.unisa.eruplan.entity.NucleoEntity;
import eruplan.unisa.eruplan.entity.RichiestaEntity;


/**
 * Contiene la logica di business (business logic) per la gestione del nucleo familiare.
 */
public class GestioneNucleoFamiliareService {

    // CORREZIONE: Pattern aggiornato per usare i trattini (dd-MM-yyyy) per coerenza.
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "^((0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-((19|20)\\d{2}))S");

    private GestioneNucleoFamiliareRepository repository;

    public interface ServiceCallback {
        void onSalvataggioSuccess(String message);
        void onSalvataggioError(String message);
    }

    public interface NucleoServiceCallback {
        void onNucleoLoaded(NucleoEntity nucleo);
        void onServiceError(String message);
    }

    public interface AppoggiServiceCallback {
        void onAppoggiLoaded(List<AppoggioEntity> appoggi);
        void onServiceError(String message);
    }

    public interface MembriServiceCallback {
        void onMembriLoaded(List<MembroEntity> membri);
        void onServiceError(String message);
    }

    public interface RichiesteServiceCallback {
        void onRichiesteLoaded(List<RichiestaEntity> richieste);
        void onServiceError(String message);
    }

    public GestioneNucleoFamiliareService(Context context) {
        this.repository = new GestioneNucleoFamiliareRepository(context);
    }

    public void getNucleo(final NucleoServiceCallback callback) {
        repository.getNucleo(new GestioneNucleoFamiliareRepository.NucleoCallback() {
            @Override
            public void onSuccess(NucleoEntity nucleo) {
                callback.onNucleoLoaded(nucleo);
            }

            @Override
            public void onError(String message) {
                callback.onServiceError(message);
            }
        });
    }

    public void getRichieste(final RichiesteServiceCallback callback) {
        repository.getRichieste(new GestioneNucleoFamiliareRepository.RichiesteCallback() {
            @Override
            public void onSuccess(List<RichiestaEntity> richieste) {
                callback.onRichiesteLoaded(richieste);
            }

            @Override
            public void onError(String message) {
                callback.onServiceError(message);
            }
        });
    }

    public void getMembri(final MembriServiceCallback callback) {
        repository.getMembri(new GestioneNucleoFamiliareRepository.MembriCallback() {
            @Override
            public void onSuccess(List<MembroEntity> membri) {
                callback.onMembriLoaded(membri);
            }

            @Override
            public void onError(String message) {
                callback.onServiceError(message);
            }
        });
    }

    public void getAppoggi(final AppoggiServiceCallback callback) {
        repository.getAppoggi(new GestioneNucleoFamiliareRepository.AppoggiCallback() {
            @Override
            public void onSuccess(List<AppoggioEntity> appoggi) {
                callback.onAppoggiLoaded(appoggi);
            }

            @Override
            public void onError(String message) {
                callback.onServiceError(message);
            }
        });
    }

    public void eliminaAppoggio(long appoggioId, final ServiceCallback callback) {
        repository.eliminaAppoggio(appoggioId, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSalvataggioSuccess(message);
            }

            @Override
            public void onError(String message) {
                callback.onSalvataggioError(message);
            }
        });
    }

    public void rimuoviMembro(String codiceFiscale, final ServiceCallback callback) {
        repository.rimuoviMembro(codiceFiscale, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSalvataggioSuccess(message);
            }

            @Override
            public void onError(String message) {
                callback.onSalvataggioError(message);
            }
        });
    }

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
        final String nomeTrimmed = validateAndTrim(nome, 2, 30, "Il nome non è valido.");
        final String cognomeTrimmed = validateAndTrim(cognome, 2, 30, "Il cognome non è valido.");
        final String cfTrimmed = validateAndTrim(codiceFiscale, 16, 16, "Il codice fiscale deve essere di 16 caratteri.");

        if (dataDiNascita == null || !DATE_PATTERN.matcher(dataDiNascita).matches()) {
            throw new IllegalArgumentException("La data di nascita non è valida (formato richiesto: dd-mm-aaaa).");
        }
        if (sesso == null || (!sesso.equals("M") && !sesso.equals("F"))) {
            throw new IllegalArgumentException("Il sesso non è valido.");
        }

        MembroEntity nuovoMembroEntity = new MembroEntity(nomeTrimmed, cognomeTrimmed, cfTrimmed, dataDiNascita, sesso, assistenza, minorenne);

        repository.salvaMembro(nuovoMembroEntity, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
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

    public void creaNucleo(String viaPiazza, String comune, String regione, String paese, String civico, String cap, boolean hasVeicolo, int postiVeicolo, final ServiceCallback serviceCallback) throws IllegalArgumentException {
        final String viaPiazzaTrimmed = validateAndTrim(viaPiazza, 1, 40, "Via/Piazza non valido.");
        final String comuneTrimmed = validateAndTrim(comune, 1, 40, "Comune non valido.");
        final String regioneTrimmed = validateAndTrim(regione, 1, 40, "Regione non valida.");
        final String paeseTrimmed = validateAndTrim(paese, 1, 40, "Paese non valido.");
        final String civicoTrimmed = validateAndTrim(civico, 1, 5, "Numero civico non valido.");
        final String capTrimmed = validateAndTrim(cap, 5, 5, "CAP non valido. Deve essere di 5 cifre.");

        if (hasVeicolo && (postiVeicolo < 2 || postiVeicolo > 9)) {
            throw new IllegalArgumentException("Il numero di posti del veicolo deve essere compreso tra 2 e 9.");
        }

        NucleoEntity nuovoNucleoEntity = new NucleoEntity(viaPiazzaTrimmed, comuneTrimmed, regioneTrimmed, paeseTrimmed, civicoTrimmed, capTrimmed, hasVeicolo, postiVeicolo);

        repository.salvaNucleo(nuovoNucleoEntity, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
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

    public void abbandonaNucleo(final ServiceCallback serviceCallback) {
        repository.abbandonaNucleo(new GestioneNucleoFamiliareRepository.RepositoryCallback() {
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

    public void creaAppoggio(String viaPiazza, String civico, String comune, String cap, String provincia, String regione, String paese, final ServiceCallback serviceCallback) {
        final String viaPiazzaTrimmed = validateAndTrim(viaPiazza, 1, 45, "Via/Piazza non valido.");
        final String civicoTrimmed = validateAndTrim(civico, 1, 4, "Numero civico non valido.");
        final String comuneTrimmed = validateAndTrim(comune, 1, 40, "Comune non valido.");
        final String capTrimmed = validateAndTrim(cap, 5, 5, "CAP non valido. Deve essere di 5 cifre.");
        final String provinciaTrimmed = validateAndTrim(provincia, 1, 40, "Provincia non valida.");
        final String regioneTrimmed = validateAndTrim(regione, 1, 40, "Regione non valida.");
        final String paeseTrimmed = validateAndTrim(paese, 1, 40, "Paese non valido.");

        AppoggioEntity nuovoAppoggio = new AppoggioEntity(viaPiazzaTrimmed, civicoTrimmed, comuneTrimmed, capTrimmed, provinciaTrimmed, regioneTrimmed, paeseTrimmed);

        repository.salvaAppoggio(nuovoAppoggio, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
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

    public void modificaResidenza(String viaPiazza, String comune, String regione, String paese, String civico, String cap, final ServiceCallback serviceCallback) throws IllegalArgumentException {
        final String viaPiazzaTrimmed = validateAndTrim(viaPiazza, 1, 40, "Via/Piazza non valido.");
        final String comuneTrimmed = validateAndTrim(comune, 1, 40, "Comune non valido.");
        final String regioneTrimmed = validateAndTrim(regione, 1, 40, "Regione non valida.");
        final String paeseTrimmed = validateAndTrim(paese, 1, 40, "Paese non valido.");
        final String civicoTrimmed = validateAndTrim(civico, 1, 5, "Numero civico non valido.");
        final String capTrimmed = validateAndTrim(cap, 5, 5, "CAP non valido. Deve essere di 5 cifre.");

        NucleoEntity nucleoModificato = new NucleoEntity(viaPiazzaTrimmed, comuneTrimmed, regioneTrimmed, paeseTrimmed, civicoTrimmed, capTrimmed, false, 0);

        repository.modificaResidenza(nucleoModificato, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
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

    // =================================================================================
    //  NUOVI METODI PER L'INVITO (REQUISITO UC-GNF.01)
    // =================================================================================

    /**
     * Logica per la ricerca dell'utente.
     * 1. Pulisce la stringa del CF e controlla che sia di 16 caratteri.
     * 2. Se valido, chiama il repository per cercare nel server.
     */
    public void cercaUtentePerInvito(String codiceFiscale, final GestioneNucleoFamiliareRepository.UtenteCallback callback) {
        try {
            // STEP 1: Validazione locale
            // Se il CF non è di 16 caratteri, validateAndTrim lancia un'eccezione e blocca tutto subito.
            String cfTrimmed = validateAndTrim(codiceFiscale, 16, 16, "Il Codice Fiscale deve essere di 16 caratteri esatti.");

            // STEP 2: Se siamo qui, il CF è formalmente valido. Chiamiamo il server.
            repository.cercaUtenteByCF(cfTrimmed, callback);

        } catch (IllegalArgumentException e) {
            // Se la validazione fallisce, restituiamo subito l'errore senza chiamare il server
            callback.onError(e.getMessage());
        }
    }

    /**
     * Logica per l'invio dell'invito.
     */
    public void inviaInvito(String codiceFiscale, final ServiceCallback callback) {
        // Controllo di sicurezza base
        if (codiceFiscale == null || codiceFiscale.length() != 16) {
            callback.onSalvataggioError("Codice fiscale non valido per l'invio.");
            return;
        }

        // Passo il controllo al repository
        repository.inviaRichiestaInvito(codiceFiscale, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                // Traduciamo il callback del repository in quello del service
                callback.onSalvataggioSuccess(message);
            }

            @Override
            public void onError(String message) {
                callback.onSalvataggioError(message);
            }
        });
    }
}
