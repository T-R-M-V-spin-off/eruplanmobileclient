package eruplan.unisa.eruplan.callback;

import eruplan.unisa.eruplan.entity.MembroEntity;

public interface UtenteCallback {
    void onSuccess(MembroEntity membroTrovato);
    void onError(String message);
}
