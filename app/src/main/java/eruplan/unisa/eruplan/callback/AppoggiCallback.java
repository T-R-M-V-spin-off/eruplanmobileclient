package eruplan.unisa.eruplan.callback;

import java.util.List;
import eruplan.unisa.eruplan.entity.AppoggioEntity;

public interface AppoggiCallback {
    void onAppoggiLoaded(List<AppoggioEntity> appoggi);
    void onError(String message);
}
