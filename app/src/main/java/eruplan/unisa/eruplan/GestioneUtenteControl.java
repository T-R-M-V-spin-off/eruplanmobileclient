package eruplan.unisa.eruplan;

import android.content.Context;

/**
 * Funge da regista (Controller) per le operazioni relative alla gestione dell'utente.
 * Disaccoppia l'interfaccia utente (Boundary) dalla logica di business (Service).
 */
public class GestioneUtenteControl {

    private GestioneUtenteService service;

    /**
     * Interfaccia di callback per notificare all'Activity (Boundary) l'esito finale dell'operazione.
     */
    public interface ControlCallback {
        void onOperazioneSuccess(String message);
        void onOperazioneError(String message);
    }

    public GestioneUtenteControl(Context context) {
        this.service = new GestioneUtenteService(context);
    }

    /**
     * Avvia il processo di login.
     */
    public void login(String codiceFiscale, String password, final ControlCallback callback) {
        service.login(codiceFiscale, password, new GestioneUtenteService.ServiceCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onOperazioneSuccess(message);
            }

            @Override
            public void onError(String message) {
                callback.onOperazioneError(message);
            }
        });
    }

    /**
     * Avvia il processo di registrazione.
     */
    public void registra(String nome, String cognome, String cf, String data, String sesso, String password, String confirmPass, final ControlCallback callback) {
        service.registra(nome, cognome, cf, data, sesso, password, confirmPass, new GestioneUtenteService.ServiceCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onOperazioneSuccess(message);
            }

            @Override
            public void onError(String message) {
                callback.onOperazioneError(message);
            }
        });
    }
}
