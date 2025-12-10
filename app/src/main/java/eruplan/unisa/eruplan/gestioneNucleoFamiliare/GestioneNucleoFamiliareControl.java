package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Context;

import eruplan.unisa.eruplan.callback.AppoggiCallback;
import eruplan.unisa.eruplan.callback.GenericCallback;
import eruplan.unisa.eruplan.callback.MembriCallback;
import eruplan.unisa.eruplan.callback.NucleoCallback;
import eruplan.unisa.eruplan.callback.RichiesteCallback;
import eruplan.unisa.eruplan.callback.UtenteCallback;

public class GestioneNucleoFamiliareControl {

    private final GestioneNucleoFamiliareService service;

    public GestioneNucleoFamiliareControl(Context context) {
        this.service = new GestioneNucleoFamiliareService(context.getApplicationContext());
    }

    public void getNucleo(final NucleoCallback callback) {
        service.getNucleo(callback);
    }

    public void getRichieste(final RichiesteCallback callback) {
        service.getRichieste(callback);
    }

    public void getMembri(final MembriCallback callback) {
        service.getMembri(callback);
    }

    public void getAppoggi(final AppoggiCallback callback) {
        service.getAppoggi(callback);
    }

    public void eliminaAppoggio(long appoggioId, final GenericCallback callback) {
        service.eliminaAppoggio(appoggioId, callback);
    }

    public void rimuoviMembro(String codiceFiscale, final GenericCallback callback) {
        service.rimuoviMembro(codiceFiscale, callback);
    }

    public void inserisciMembro(String nome, String cognome, String cf, String data, String sesso, boolean assistenza, boolean minorenne, final GenericCallback callback) {
        service.creaMembro(nome, cognome, cf, data, sesso, assistenza, minorenne, callback);
    }

    public void creaNucleo(String viaPiazza, String comune, String regione, String paese, String civico, String cap, boolean hasVeicolo, int posti, final GenericCallback callback) {
        service.creaNucleo(viaPiazza, comune, regione, paese, civico, cap, hasVeicolo, posti, callback);
    }

    public void creaAppoggio(String via, String civico, String comune, String cap, String provincia, String regione, String paese, final GenericCallback callback) {
        service.creaAppoggio(via, civico, comune, cap, provincia, regione, paese, callback);
    }

    public void modificaResidenza(String via, String comune, String regione, String paese, String civico, String cap, final GenericCallback callback) {
        service.modificaResidenza(via, comune, regione, paese, civico, cap, callback);
    }

    public void abbandonaNucleo(final GenericCallback callback) {
        service.abbandonaNucleo(callback);
    }

    public void cercaUtentePerInvito(String cf, final UtenteCallback callback) {
        service.cercaUtentePerInvito(cf, callback);
    }

    public void finalizzaInvito(String cf, final GenericCallback callback) {
        service.inviaInvito(cf, callback);
    }

    public void accettaRichiesta(long idRichiesta, final GenericCallback callback) {
        service.accettaRichiesta(idRichiesta, callback);
    }
}
