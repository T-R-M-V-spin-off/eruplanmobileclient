package eruplan.unisa.eruplan.utility;

import android.app.Application;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class EruplanApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Schedula il worker per le notifiche
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(NotificationUtil.class, 180, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("familyChangeNotification", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }
}
