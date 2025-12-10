package eruplan.unisa.eruplan.callback;

import java.util.List;
import eruplan.unisa.eruplan.entity.MembroEntity;

public interface MembriCallback {
    void onMembriLoaded(List<MembroEntity> membri);
    void onError(String message);
}
