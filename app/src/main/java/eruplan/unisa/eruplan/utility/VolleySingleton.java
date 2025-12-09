package eruplan.unisa.eruplan.utility;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;

public class VolleySingleton {
    private static final String TAG = "VolleySingleton";
    private static volatile VolleySingleton instance;
    private final RequestQueue requestQueue;
    private final CookieManager cookieManager;

    // Dominio del server centralizzato per la gestione dei cookie
    private static final URI SERVER_URI;
    static {
        URI uri = null;
        try {
            uri = new URI("https://eruplanserver.azurewebsites.net");
        } catch (URISyntaxException e) {
            Log.e(TAG, "Server URI non valido", e);
        }
        SERVER_URI = uri;
    }

    private VolleySingleton(Context context) {
        // Usa l'application context per evitare memory leak
        Context appContext = context.getApplicationContext();
        this.requestQueue = Volley.newRequestQueue(appContext);

        // Configurazione Globale Cookie (JSESSIONID)
        if (CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(new CookieManager());
        }
        this.cookieManager = (CookieManager) CookieHandler.getDefault();
    }

    public static VolleySingleton getInstance(Context context) {
        if (instance == null) {
            // Double-checked locking for thread safety
            synchronized (VolleySingleton.class) {
                if (instance == null) {
                    instance = new VolleySingleton(context);
                }
            }
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        requestQueue.add(req);
    }

    public boolean isUserLoggedIn() {
        if (cookieManager == null || SERVER_URI == null) {
            return false;
        }
        return cookieManager.getCookieStore().get(SERVER_URI).stream()
                .anyMatch(httpCookie -> httpCookie.getName().equals("JSESSIONID") && !httpCookie.hasExpired());
    }

    public boolean isUserAdmin() {
        if (cookieManager == null || SERVER_URI == null) {
            return false;
        }
        return cookieManager.getCookieStore().get(SERVER_URI).stream()
                .anyMatch(httpCookie -> httpCookie.getName().equals("isAdmin")
                                      && "true".equals(httpCookie.getValue())
                                      && !httpCookie.hasExpired());
    }

    public void logout() {
        if (cookieManager != null) {
            cookieManager.getCookieStore().removeAll();
        }
    }
}
