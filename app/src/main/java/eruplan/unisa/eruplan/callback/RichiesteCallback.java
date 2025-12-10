package eruplan.unisa.eruplan.callback;

import java.util.List;
import eruplan.unisa.eruplan.entity.RichiestaEntity;

public interface RichiesteCallback {
    void onRichiesteLoaded(List<RichiestaEntity> richieste);
    void onError(String message);
}
