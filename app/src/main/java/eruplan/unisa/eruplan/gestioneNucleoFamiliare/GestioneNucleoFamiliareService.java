package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.callback.AppoggiCallback;
import eruplan.unisa.eruplan.callback.GenericCallback;
import eruplan.unisa.eruplan.callback.MembriCallback;
import eruplan.unisa.eruplan.callback.NucleoCallback;
import eruplan.unisa.eruplan.callback.RichiesteCallback;
import eruplan.unisa.eruplan.callback.UtenteCallback;
import eruplan.unisa.eruplan.entity.AppoggioEntity;
import eruplan.unisa.eruplan.entity.MembroEntity;
import eruplan.unisa.eruplan.entity.NucleoEntity;
import eruplan.unisa.eruplan.utility.Validator;

public class GestioneNucleoFamiliareService {

    private final GestioneNucleoFamiliareRepository repository;
    private final Context context;

    public GestioneNucleoFamiliareService(Context context) {
        this.repository = new GestioneNucleoFamiliareRepository(context);
        this.context = context;
    }

    public void getNucleo(final NucleoCallback callback) {
        repository.getNucleo(callback);
    }

    public void checkNucleoExists(final GenericCallback callback) {
        repository.checkNucleoExists(callback);
    }

    public void getRichieste(final RichiesteCallback callback) {repository.getRichieste(callback);}
    public void getMembri(final MembriCallback callback) {
        repository.getMembri(callback);
    }
    public void getAppoggi(final AppoggiCallback callback) {
        repository.getAppoggi(callback);
    }


    public void eliminaAppoggio(long appoggioId, final GenericCallback callback) {repository.eliminaAppoggio(appoggioId, callback);}
    public void rimuoviMembro(String codiceFiscale, final GenericCallback callback) {repository.rimuoviMembro(codiceFiscale, callback);}

    public void abbandonaNucleo(final GenericCallback callback)  {repository.abbandonaNucleo(callback);}

    public void creaMembro(String nome, String cognome, String cf, String data, String sesso, boolean assistenza, boolean minorenne, final GenericCallback callback) {
        if (!Validator.isNomeValid(nome)) { callback.onError(context.getString(R.string.validation_invalid_name)); return; }
        if (!Validator.isCognomeValid(cognome)) { callback.onError(context.getString(R.string.validation_invalid_lastname)); return; }
        if (!Validator.isCodiceFiscaleValid(cf)) { callback.onError(context.getString(R.string.validation_invalid_cf_format)); return; }
        if (!Validator.isSessoValid(sesso)) { callback.onError(context.getString(R.string.validation_invalid_gender)); return; }
        try {
            LocalDate d = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            if (!Validator.isDataNascitaValid(d)) { callback.onError(context.getString(R.string.validation_invalid_birthdate)); return; }
        } catch (DateTimeParseException e) {
            callback.onError(context.getString(R.string.validation_invalid_birthdate_format)); return;
        }
        repository.salvaMembro(new MembroEntity(nome, cognome, cf, data, sesso, assistenza, minorenne), callback);
    }

    public void creaNucleo(String via, String comune, String regione, String paese, String civico, String cap, boolean hasVeicolo, int posti, final GenericCallback callback) {
        if (validateAndTrim(via, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_address)); return; }
        if (validateAndTrim(comune, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_municipality)); return; }
        if (validateAndTrim(regione, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_region)); return; }
        if (validateAndTrim(paese, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_country)); return; }
        if (validateAndTrim(civico, 1, 5) == null) { callback.onError(context.getString(R.string.validation_invalid_house_number)); return; }
        if (validateAndTrim(cap, 5, 5) == null) { callback.onError(context.getString(R.string.validation_invalid_zip)); return; }
        if (hasVeicolo && (posti < 2 || posti > 9)) { callback.onError(context.getString(R.string.validation_invalid_vehicle_seats)); return; }
        repository.salvaNucleo(new NucleoEntity(via, comune, regione, paese, civico, cap, hasVeicolo, posti), callback);
    }

    public void creaAppoggio(String via, String civico, String comune, String cap, String provincia, String regione, String paese, final GenericCallback callback) {
        if (validateAndTrim(via, 1, 45) == null) { callback.onError(context.getString(R.string.validation_invalid_address)); return; }
        if (validateAndTrim(civico, 1, 4) == null) { callback.onError(context.getString(R.string.validation_invalid_house_number)); return; }
        if (validateAndTrim(comune, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_municipality)); return; }
        if (validateAndTrim(cap, 5, 5) == null) { callback.onError(context.getString(R.string.validation_invalid_zip)); return; }
        if (validateAndTrim(provincia, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_province)); return; }
        if (validateAndTrim(regione, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_region)); return; }
        if (validateAndTrim(paese, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_country)); return; }
        repository.salvaAppoggio(new AppoggioEntity(via, civico, comune, cap, provincia, regione, paese), callback);
    }

    public void modificaResidenza(String via, String comune, String regione, String paese, String civico, String cap, final GenericCallback callback) {
        if (validateAndTrim(via, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_address)); return; }
        if (validateAndTrim(comune, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_municipality)); return; }
        if (validateAndTrim(regione, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_region)); return; }
        if (validateAndTrim(paese, 1, 40) == null) { callback.onError(context.getString(R.string.validation_invalid_country)); return; }
        if (validateAndTrim(civico, 1, 5) == null) { callback.onError(context.getString(R.string.validation_invalid_house_number)); return; }
        if (validateAndTrim(cap, 5, 5) == null) { callback.onError(context.getString(R.string.validation_invalid_zip)); return; }
        repository.modificaResidenza(new NucleoEntity(via, comune, regione, paese, civico, cap, false, 0), callback);
    }

    public void cercaUtentePerInvito(String cf, final UtenteCallback callback) {
        if (!Validator.isCodiceFiscaleValid(cf)) { callback.onError(context.getString(R.string.invalid_cf_length_error)); return; }
        repository.cercaUtenteByCF(cf, callback);
    }

    public void inviaInvito(String cf, final GenericCallback callback) {
        if (!Validator.isCodiceFiscaleValid(cf)) { callback.onError(context.getString(R.string.validation_cf_not_valid_for_invite)); return; }
        repository.inviaRichiestaInvito(cf, callback);
    }

    public void accettaRichiesta(long idRichiesta, final GenericCallback callback) {
        if (idRichiesta <= 0) { callback.onError(context.getString(R.string.validation_invalid_request_id)); return; }
        repository.accettaRichiestaApi(idRichiesta, callback);
    }

    private String validateAndTrim(String value, int min, int max) {
        if (value == null) return null;
        String trimmed = value.trim();
        return (trimmed.length() >= min && trimmed.length() <= max) ? trimmed : null;
    }
}
