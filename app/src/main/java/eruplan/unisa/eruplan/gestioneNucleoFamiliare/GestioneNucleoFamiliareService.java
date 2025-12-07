package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import eruplan.unisa.eruplan.entity.AppoggioEntity;
import eruplan.unisa.eruplan.entity.MembroEntity;
import eruplan.unisa.eruplan.entity.NucleoEntity;
import eruplan.unisa.eruplan.entity.RichiestaEntity;
import eruplan.unisa.eruplan.utility.Validator;


/**
 * Contiene la logica di business (business logic) per la gestione del nucleo familiare.
 */
public class GestioneNucleoFamiliareService {

    private final GestioneNucleoFamiliareRepository repository;

    public interface ServiceCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public interface NucleoServiceCallback {
        void onNucleoLoaded(NucleoEntity nucleo);
        void onError(String message);
    }

    public interface AppoggiServiceCallback {
        void onAppoggiLoaded(List<AppoggioEntity> appoggi);
        void onError(String message);
    }

    public interface MembriServiceCallback {
        void onMembriLoaded(List<MembroEntity> membri);
        void onError(String message);
    }

    public interface RichiesteServiceCallback {
        void onRichiesteLoaded(List<RichiestaEntity> richieste);
        void onError(String message);
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
                callback.onError(message);
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
                callback.onError(message);
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
                callback.onError(message);
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
                callback.onError(message);
            }
        });
    }

    public void eliminaAppoggio(long appoggioId, final ServiceCallback callback) {
        repository.eliminaAppoggio(appoggioId, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
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

    public void rimuoviMembro(String codiceFiscale, final ServiceCallback callback) {
        repository.rimuoviMembro(codiceFiscale, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
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
        if (!Validator.isNomeValid(nome)) {
            throw new IllegalArgumentException("Il nome non è valido.");
        }
        if (!Validator.isCognomeValid(cognome)) {
            throw new IllegalArgumentException("Il cognome non è valido.");
        }
        if (!Validator.isCodiceFiscaleValid(codiceFiscale)) {
            throw new IllegalArgumentException("Il codice fiscale deve essere di 16 caratteri.");
        }
        if (!Validator.isSessoValid(sesso)) {
            throw new IllegalArgumentException("Il sesso non è valido.");
        }

        try {
            LocalDate data = LocalDate.parse(dataDiNascita, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            if (!Validator.isDataNascitaValid(data)) {
                throw new IllegalArgumentException("La data di nascita non è valida.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La data di nascita non è valida (formato richiesto: dd-mm-aaaa).");
        }


        MembroEntity nuovoMembroEntity = new MembroEntity(nome, cognome, codiceFiscale, dataDiNascita, sesso, assistenza, minorenne);

        repository.salvaMembro(nuovoMembroEntity, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                serviceCallback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                serviceCallback.onError(message);
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
                serviceCallback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                serviceCallback.onError(message);
            }
        });
    }

    public void abbandonaNucleo(final ServiceCallback serviceCallback) {
        repository.abbandonaNucleo(new GestioneNucleoFamiliareRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                serviceCallback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                serviceCallback.onError(message);
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
                serviceCallback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                serviceCallback.onError(message);
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
                serviceCallback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                serviceCallback.onError(message);
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
        if (!Validator.isCodiceFiscaleValid(codiceFiscale)) {
            callback.onError("Il Codice Fiscale deve essere di 16 caratteri esatti.");
            return;
        }
        repository.cercaUtenteByCF(codiceFiscale, callback);

    }

    /**
     * Logica per l'invio dell'invito.
     */
    public void inviaInvito(String codiceFiscale, final ServiceCallback callback) {
        if (!Validator.isCodiceFiscaleValid(codiceFiscale)) {
            callback.onError("Codice fiscale non valido per l'invio.");
            return;
        }

        // Passo il controllo al repository
        repository.inviaRichiestaInvito(codiceFiscale, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
            @Override
            public void onSuccess(String message) {
                // Traduciamo il callback del repository in quello del service
                callback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    // =================================================================================
    //  METODI PER ACCETTARE RICHIESTA (REQUISITO SC-GNF.02)
    // =================================================================================

    /**
     * Logica per accettare la richiesta.
     * Verifica la validità dell'ID e chiama il Repository.
     */
    public void accettaRichiesta(long idRichiesta, final ServiceCallback callback) {
        // Validazione minima: l'ID deve essere positivo
        if (idRichiesta <= 0) {
            callback.onError("ID richiesta non valido.");
            return;
        }

        // Chiama il metodo del repository
        repository.accettaRichiestaApi(idRichiesta, new GestioneNucleoFamiliareRepository.RepositoryCallback() {
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
