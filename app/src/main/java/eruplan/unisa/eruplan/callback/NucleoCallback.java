package eruplan.unisa.eruplan.callback;

import eruplan.unisa.eruplan.entity.NucleoEntity;

public interface NucleoCallback {
    void onNucleoLoaded(NucleoEntity nucleo);
    void onError(String message);
}
