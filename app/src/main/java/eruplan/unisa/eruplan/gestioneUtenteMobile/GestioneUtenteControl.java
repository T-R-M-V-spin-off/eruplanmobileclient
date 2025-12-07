package eruplan.unisa.eruplan.gestioneUtenteMobile;

import android.content.Context;
import android.content.Intent;

import eruplan.unisa.eruplan.gestioneNucleoFamiliare.CosaVuoiFareBoundary;

/**
 * Gestisce le interazioni tra la UI (Boundary) e la logica di business (Service).
 */
public class GestioneUtenteControl {

    private final GestioneUtenteService service;
    private final Context context;

    // Interfacce per la comunicazione verso le Boundary, specifiche per operazione
    public interface LoginCallback {
        void onLoginSuccess(String message);
        void onLoginError(String message);
        void onLoginRedirect();
    }

    public interface SignupCallback {
        void onSignupSuccess(String message);
        void onSignupError(String message);
        void onSignupRedirect();
    }

    public interface OperationCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public GestioneUtenteControl(Context context) {
        this.service = new GestioneUtenteService(context);
        this.context = context;
    }

    /**
     * Inoltra la richiesta di login al Service e gestisce la risposta.
     */
    public void login(String codiceFiscale, String password, final LoginCallback callback) {
        service.login(codiceFiscale, password, new GestioneUtenteService.ServiceCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onLoginSuccess(message);
                // La logica di navigazione rimane qui per centralizzare i cambi di activity
                Intent intent = new Intent(context, CosaVuoiFareBoundary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                callback.onLoginRedirect();
            }

            @Override
            public void onError(String message) {
                callback.onLoginError(message);
            }
        });
    }

    /**
     * Inoltra la richiesta di registrazione al Service e gestisce la risposta.
     */
    public void registra(String nome, String cognome, String cf, String data, String sesso, String password, String confirmPass, final SignupCallback callback) {
        service.registra(nome, cognome, cf, data, sesso, password, confirmPass, new GestioneUtenteService.ServiceCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSignupSuccess(message);
                // La logica di navigazione rimane qui
                Intent intent = new Intent(context, LoginBoundary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                callback.onSignupRedirect();
            }

            @Override
            public void onError(String message) {
                callback.onSignupError(message);
            }
        });
    }

    /**
     * Inoltra la richiesta di logout al Service.
     */
    public void logout(final OperationCallback callback) {
        service.logout(new GestioneUtenteService.ServiceCallback() {
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
