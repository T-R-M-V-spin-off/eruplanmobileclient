package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.content.Context;
import android.content.Intent;

import eruplan.unisa.eruplan.gestioneNucleoFamiliare.CosaVuoiFareBoundary;

/**
 * Gestisce le interazioni tra la UI (Boundary) e la logica di business (Service).
 * Inoltra le richieste dalla UI al Service e restituisce i risultati alla UI tramite callback.
 */
public class GestioneUtenteControl {

    private final GestioneUtenteService service;
    private final ControlCallback callback;
    private final Context context;

    // Interfaccia per la comunicazione verso la Boundary
    public interface ControlCallback {
        void onOperazioneSuccess(String message);
        void onOperazioneError(String message);
        void onLoginRedirect();
        void onSignupRedirect();
    }

    /**
     * Implementazione vuota di {@link ControlCallback}.
     * Permette alle classi di estenderla e implementare solo i metodi necessari,
     * evitando di dover fornire implementazioni vuote per i metodi non utilizzati.
     */
    public static class ControlCallbackAdapter implements ControlCallback {
        @Override
        public void onOperazioneSuccess(String message) {}
        @Override
        public void onOperazioneError(String message) {}
        @Override
        public void onLoginRedirect() {}
        @Override
        public void onSignupRedirect() {}
    }


    public GestioneUtenteControl(Context context, ControlCallback callback) {
        this.service = new GestioneUtenteService(context);
        this.callback = callback;
        this.context = context;
    }

    /**
     * Inoltra la richiesta di login al Service.
     */
    public void login(String codiceFiscale, String password) {
        service.login(codiceFiscale, password, new GestioneUtenteService.ServiceCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onOperazioneSuccess(message);
                Intent intent = new Intent(context, CosaVuoiFareBoundary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                callback.onLoginRedirect();
            }

            @Override
            public void onError(String message) {
                callback.onOperazioneError(message);
            }
        });
    }

    /**
     * Inoltra la richiesta di registrazione al Service.
     */
    public void registra(String nome, String cognome, String cf, String data, String sesso, String password, String confirmPass) {
        service.registra(nome, cognome, cf, data, sesso, password, confirmPass, new GestioneUtenteService.ServiceCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onOperazioneSuccess(message);
                Intent intent = new Intent(context, LoginBoundary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                callback.onSignupRedirect();
            }

            @Override
            public void onError(String message) {
                callback.onOperazioneError(message);
            }
        });
    }

    /**
     * Inoltra la richiesta di logout al Service.
     */
    public void logout() {
        service.logout(new GestioneUtenteService.ServiceCallback() {
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
