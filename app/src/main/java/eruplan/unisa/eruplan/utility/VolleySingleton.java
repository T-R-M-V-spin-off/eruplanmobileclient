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
    // Dominio del server centralizzato per la gestione dei cookie
    private static final String SERVER_DOMAIN = "https://eruplanserver.azurewebsites.net";

    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        // Configurazione Globale Cookie (JSESSIONID)
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
        try {
            java.net.URI uri = new java.net.URI(SERVER_DOMAIN);
            return cookieManager.getCookieStore().get(uri).stream()
                    .anyMatch(httpCookie -> httpCookie.getName().equals("JSESSIONID") && !httpCookie.hasExpired());
        } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isUserAdmin() {
        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        if (cookieManager == null) {
            return false;
        }

        try {
            java.net.URI uri = new java.net.URI(SERVER_DOMAIN);
            return cookieManager.getCookieStore().get(uri).stream()
                    .anyMatch(httpCookie -> httpCookie.getName().equals("isAdmin")
                                          && httpCookie.getValue().equals("true")
                                          && !httpCookie.hasExpired());
        } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void logout() {
        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        if (cookieManager != null) {
            cookieManager.getCookieStore().removeAll();
        }
    }
}
