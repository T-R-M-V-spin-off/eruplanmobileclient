package eruplan.unisa.eruplan;

import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Funge da regista (Controller) per le operazioni relative alla gestione del nucleo familiare.
 */
public class GestioneNucleoFamiliareControl {

    private GestioneNucleoFamiliareService service;
    private Context context;

    public interface ControlCallback {
        void onInserimentoSuccesso(String message);
        void onInserimentoErrore(String message);
    }

    public interface AppoggiControlCallback {
        void onAppoggiLoaded(List<AppoggioEntity> appoggi);
        void onControlError(String message);
    }

    public GestioneNucleoFamiliareControl(Context context) {
        this.context = context;
        this.service = new GestioneNucleoFamiliareService(context);
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
     * @param controlCallback L'interfaccia per notificare l'Activity del risultato.
     */
    public void abbandonaNucleo(final ControlCallback controlCallback) {
        service.abbandonaNucleo(new GestioneNucleoFamiliareService.ServiceCallback() {
            @Override
            public void onSalvataggioSuccess(String message) {
                // Traduciamo il successo del service in un successo per l'Activity
                controlCallback.onInserimentoSuccesso(message);
            }

            @Override
            public void onSalvataggioError(String message) {
                // Traduciamo l'errore del service in un errore per l'Activity
                controlCallback.onInserimentoErrore(message);
            }
        });
    }

}
