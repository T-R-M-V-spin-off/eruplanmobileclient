package eruplan.unisa.eruplan.utility;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;

public class VolleySingleton {
    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        // Configurazione Globale Cookie (JSESSIONID)
        // Viene eseguita solo una volta quando l'istanza viene creata
        if (CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(new CookieManager());
        }
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() è fondamentale, impedisce di leakare l'Activity
            // o di ricreare la coda ogni volta che giri lo schermo.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static boolean isUserLoggedIn() {
        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        if (cookieManager == null) {
            return false;
        }

        // Assumendo che tutti gli endpoint dell'applicazione usino lo stesso dominio/host dell'URL di login.
        // Qui usiamo un URI fittizio, dovrai sostituirlo con il tuo vero dominio se diverso.
        try {
            java.net.URI uri = new java.net.URI("http://192.168.1.12:8080"); // <-- SOSTITUISCI CON IL TUO DOMINIO SE DIVERSO
            return cookieManager.getCookieStore().get(uri).stream()
                    .anyMatch(httpCookie -> httpCookie.getName().equals("JSESSIONID") && !httpCookie.hasExpired());
        } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Controlla se l'utente ha i permessi di amministratore.
     * Questo metodo presuppone che il server imposti un cookie chiamato "isAdmin" con valore "true"
     * per gli utenti amministratori.
     *
     * @return true se l'utente è un amministratore, false altrimenti.
     */
    public static boolean isUserAdmin() {
        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        if (cookieManager == null) {
            return false;
        }

        try {
            java.net.URI uri = new java.net.URI("http://192.168.1.12:8080"); // <-- SOSTITUISCI CON IL TUO DOMINIO SE DIVERSO
            return cookieManager.getCookieStore().get(uri).stream()
                    .anyMatch(httpCookie -> httpCookie.getName().equals("isAdmin") // <-- NOME DEL COOKIE
                                          && httpCookie.getValue().equals("true")    // <-- VALORE DEL COOKIE
                                          && !httpCookie.hasExpired());
        } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }
}
