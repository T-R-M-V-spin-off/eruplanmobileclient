package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import eruplan.unisa.eruplan.entity.AppoggioEntity;
import eruplan.unisa.eruplan.entity.MembroEntity;
import eruplan.unisa.eruplan.entity.ResidenzaEntity;
import eruplan.unisa.eruplan.entity.RichiestaEntity;

public class GestioneNucleoFamiliareControl {

    private GestioneNucleoFamiliareService service;
    private Context context;

    public interface ControlCallback {
        void onInserimentoSuccesso(String message);
        void onInserimentoErrore(String message);
    }

    public interface ResidenzaControlCallback {
        void onResidenzaLoaded(ResidenzaEntity residenza);
        void onControlError(String message);
    }

    public interface AppoggiControlCallback {
        void onAppoggiLoaded(List<AppoggioEntity> appoggi);
        void onControlError(String message);
    }

    public interface MembriControlCallback {
        void onMembriLoaded(List<MembroEntity> membri);
        void onControlError(String message);
    }

    public interface RichiesteControlCallback {
        void onRichiesteLoaded(List<RichiestaEntity> richieste);
        void onControlError(String message);
    }

    public GestioneNucleoFamiliareControl(Context context) {
        this.context = context;
        this.service = new GestioneNucleoFamiliareService(context);
    }

    public void getResidenza(final ResidenzaControlCallback callback) {
        service.getResidenza(new GestioneNucleoFamiliareService.ResidenzaServiceCallback() {
            @Override
            public void onResidenzaLoaded(ResidenzaEntity residenza) {
                callback.onResidenzaLoaded(residenza);
            }

            @Override
            public void onServiceError(String message) {
                callback.onControlError(message);
            }
        });
    }

    public void getRichieste(final RichiesteControlCallback callback) {
        service.getRichieste(new GestioneNucleoFamiliareService.RichiesteServiceCallback() {
            @Override
            public void onRichiesteLoaded(List<RichiestaEntity> richieste) {
                callback.onRichiesteLoaded(richieste);
            }

            @Override
            public void onServiceError(String message) {
                callback.onControlError(message);
            }
        });
    }

    public void getMembri(final MembriControlCallback callback) {
        service.getMembri(new GestioneNucleoFamiliareService.MembriServiceCallback() {
            @Override
            public void onMembriLoaded(List<MembroEntity> membri) {
                callback.onMembriLoaded(membri);
            }

            @Override
            public void onServiceError(String message) {
                callback.onControlError(message);
            }
        });
    }

    public void getAppoggi(final AppoggiControlCallback callback) {
        service.getAppoggi(new GestioneNucleoFamiliareService.AppoggiServiceCallback() {
            @Override
            public void onAppoggiLoaded(List<AppoggioEntity> appoggi) {
                callback.onAppoggiLoaded(appoggi);
            }

            @Override
            public void onServiceError(String message) {
                callback.onControlError(message);
            }
        });
    }

    public void eliminaAppoggio(long appoggioId, final ControlCallback callback) {
        service.eliminaAppoggio(appoggioId, new GestioneNucleoFamiliareService.ServiceCallback() {
            @Override
            public void onSalvataggioSuccess(String message) {
                callback.onInserimentoSuccesso(message);
            }

            @Override
            public void onSalvataggioError(String message) {
                callback.onInserimentoErrore(message);
            }
        });
    }

    public void mostraFormCreaNucleo() {
        Intent intent = new Intent(context, CreaNucleoBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void mostraFormCreaAppoggio() {
        Intent intent = new Intent(context, InserisciAppoggioBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void apriListaAppoggio(){
        Intent intent = new Intent(context, ListaAppoggioBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void mostraVisualizzaResidenza() {
        Intent intent = new Intent(context, VisualizzaResidenzaBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void mostraFormAggiungiMembro() {
        Intent intent = new Intent(context, AggiungiMembroBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void inserisciMembro(String nome, String cognome, String codiceFiscale, String dataDiNascita, String sesso, boolean assistenza, boolean minorenne, final ControlCallback controlCallback) throws IllegalArgumentException {
        service.creaMembro(nome, cognome, codiceFiscale, dataDiNascita, sesso, assistenza, minorenne, new GestioneNucleoFamiliareService.ServiceCallback() {
            @Override
            public void onSalvataggioSuccess(String message) {
                controlCallback.onInserimentoSuccesso(message);
            }

            @Override
            public void onSalvataggioError(String message) {
                controlCallback.onInserimentoErrore(message);
            }
        });
    }

    /**
     * Avvia il processo di creazione di un nuovo nucleo familiare.
     */
    public void creaNucleo(String viaPiazza, String comune, String regione, String paese, String civico, String cap, final ControlCallback controlCallback) throws IllegalArgumentException {
        service.creaNucleo(viaPiazza, comune, regione, paese, civico, cap, new GestioneNucleoFamiliareService.ServiceCallback() {
            @Override
            public void onSalvataggioSuccess(String message) {
                controlCallback.onInserimentoSuccesso(message);
            }

            @Override
            public void onSalvataggioError(String message) {
                controlCallback.onInserimentoErrore(message);
            }
        });
    }


    public void creaAppoggio(String viaPiazza, String civico, String comune, String cap, String provincia, String regione, String paese, final ControlCallback controlCallback) {
        service.creaAppoggio(viaPiazza, civico, comune, cap, provincia, regione, paese, new GestioneNucleoFamiliareService.ServiceCallback() {
            @Override
            public void onSalvataggioSuccess(String message) {
                controlCallback.onInserimentoSuccesso(message);
            }

            @Override
            public void onSalvataggioError(String message) {
                controlCallback.onInserimentoErrore(message);
            }
        });
    }

    /**
     * Avvia la logica per abbandonare il nucleo familiare corrente.
     * @param controlCallback L\'interfaccia per notificare l\'Activity del risultato.
     */
    public void abbandonaNucleo(final ControlCallback controlCallback) {
        service.abbandonaNucleo(new GestioneNucleoFamiliareService.ServiceCallback() {
            @Override
            public void onSalvataggioSuccess(String message) {
                // Traduciamo il successo del service in un successo per l\'Activity
                controlCallback.onInserimentoSuccesso(message);
            }

            @Override
            public void onSalvataggioError(String message) {
                // Traduciamo l\'errore del service in un errore per l\'Activity
                controlCallback.onInserimentoErrore(message);
            }
        });
    }

    // =================================================================================
    //  NUOVI METODI E INTERFACCE PER L\'INVITO (REQUISITO UC-GNF.01)
    // =================================================================================

    /**
     * Interfaccia Callback specifica per la ricerca.
     * Serve per dire all\'Activity: "Ho finito di cercare, ecco il Membro trovato (o l\'errore)".
     */
    public interface RicercaCallback {
        void onUtenteTrovato(eruplan.unisa.eruplan.entity.MembroEntity membroEntity);
        void onErrore(String messaggio);
    }

    /**
     * Metodo chiamato dal bottone "Cerca" nel Dialog.
     * Fa da ponte tra Activity e Service.
     */
    public void cercaMembroPerInvito(String cf, final RicercaCallback callback) {
        service.cercaUtentePerInvito(cf, new GestioneNucleoFamiliareRepository.UtenteCallback() {
            @Override
            public void onSuccess(eruplan.unisa.eruplan.entity.MembroEntity membroTrovato) {
                // Il Service ha trovato l\'utente, lo passiamo all\'Activity
                callback.onUtenteTrovato(membroTrovato);
            }

            @Override
            public void onError(String message) {
                // Qualcosa è andato storto (non trovato o errore server)
                callback.onErrore(message);
            }
        });
    }

    /**
     * Metodo chiamato dal bottone "Invita" nel Dialog.
     */
    public void finalizzaInvito(String cf, final ControlCallback callback) {
        service.inviaInvito(cf, new GestioneNucleoFamiliareService.ServiceCallback() {
            @Override
            public void onSalvataggioSuccess(String message) {
                callback.onInserimentoSuccesso(message);
            }

            @Override
            public void onSalvataggioError(String message) {
                callback.onInserimentoErrore(message);
            }
        });
    }

}
