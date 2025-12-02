package eruplan.unisa.eruplan;

import android.content.Context;

/**
 * Funge da regista (Controller) per le operazioni relative alla gestione del nucleo familiare.
 * Disaccoppia l'interfaccia utente (Boundary) dalla logica di business (Service).
 * Il suo unico compito è ricevere le richieste dall'UI e inoltrarle al Service appropriato.
 */
public class GestioneNucleoFamiliareControl {

    private GestioneNucleoFamiliareService service;

    /**
     * Interfaccia di callback per notificare all'Activity (Boundary) l'esito finale
     * dell'operazione di inserimento, una volta che tutta la catena (Control -> Service -> Repository) è stata completata.
     */
    public interface ControlCallback {
        /** Chiamato quando l'inserimento è andato a buon fine. */
        void onInserimentoSuccesso(String message);
        /** Chiamato quando si è verificato un errore durante l'inserimento. */
        void onInserimentoErrore(String message);
    }

    /**
     * Costruttore del Control.
     * @param context Il contesto necessario per inizializzare i livelli sottostanti (in questo caso, il Service).
     */
    public GestioneNucleoFamiliareControl(Context context) {
        this.service = new GestioneNucleoFamiliareService(context);
    }

    /**
     * Avvia il processo di inserimento di un nuovo membro.
     * Fa da ponte tra l'Activity e il Service, nascondendo completamente i dettagli implementativi del Service all'Activity.
     * @param nome Dati grezzi provenienti dall'interfaccia utente.
     * @param cognome Dati grezzi provenienti dall'interfaccia utente.
     * @param codiceFiscale Dati grezzi provenienti dall'interfaccia utente.
     * @param dataDiNascita Dati grezzi provenienti dall'interfaccia utente.
     * @param sesso Dati grezzi provenienti dall'interfaccia utente.
     * @param assistenza Dati grezzi provenienti dall'interfaccia utente.
     * @param minorenne Dati grezzi provenienti dall'interfaccia utente.
     * @param controlCallback Il callback per notificare l'Activity quando l'operazione è terminata.
     * @throws IllegalArgumentException se la validazione dei dati (eseguita dal Service) fallisce.
     */
    public void inserisciMembro(String nome, String cognome, String codiceFiscale, String dataDiNascita, String sesso, boolean assistenza, boolean minorenne, final ControlCallback controlCallback) throws IllegalArgumentException {
        
        // 1. Inoltra la richiesta al Service.
        // 2. Passa al Service una nuova implementazione del suo callback (ServiceCallback).
        //    Questo permette al Control di essere avvisato quando il Service ha finito.
        service.creaMembro(nome, cognome, codiceFiscale, dataDiNascita, sesso, assistenza, minorenne, new GestioneNucleoFamiliareService.ServiceCallback() {
            @Override
            public void onSalvataggioSuccess(String message) {
                // 3a. Il Service ha notificato il successo. Ora il Control notifica l'Activity.
                controlCallback.onInserimentoSuccesso(message);
            }

            @Override
            public void onSalvataggioError(String message) {
                // 3b. Il Service ha notificato un errore. Ora il Control notifica l'Activity.
                controlCallback.onInserimentoErrore(message);
            }
        });
    }
}
