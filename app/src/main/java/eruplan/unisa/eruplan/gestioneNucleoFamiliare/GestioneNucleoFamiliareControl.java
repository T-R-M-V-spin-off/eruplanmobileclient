package eruplan.unisa.eruplan.gestioneNucleoFamiliare;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import eruplan.unisa.eruplan.entity.AppoggioEntity;
import eruplan.unisa.eruplan.entity.MembroEntity;
import eruplan.unisa.eruplan.entity.NucleoEntity;
import eruplan.unisa.eruplan.entity.RichiestaEntity;
import eruplan.unisa.eruplan.utility.VolleySingleton;

public class GestioneNucleoFamiliareControl {

    private final GestioneNucleoFamiliareService service;
    private final Context context;

    public interface ControlCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public interface NucleoControlCallback {
        void onNucleoLoaded(NucleoEntity nucleo);
        void onError(String message);
    }

    public interface AppoggiControlCallback {
        void onAppoggiLoaded(List<AppoggioEntity> appoggi);
        void onError(String message);
    }

    public interface MembriControlCallback {
        void onMembriLoaded(List<MembroEntity> membri);
        void onError(String message);
    }

    public interface RichiesteControlCallback {
        void onRichiesteLoaded(List<RichiestaEntity> richieste);
        void onError(String message);
    }

    public GestioneNucleoFamiliareControl(Context context) {
        this.context = context;
        this.service = new GestioneNucleoFamiliareService(context);
    }

    public void getNucleo(final NucleoControlCallback callback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            callback.onError("Utente non autenticato.");
            return;
        }
        service.getNucleo(new GestioneNucleoFamiliareService.NucleoServiceCallback() {
            @Override
            public void onNucleoLoaded(NucleoEntity nucleo) {
                callback.onNucleoLoaded(nucleo);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void getRichieste(final RichiesteControlCallback callback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            callback.onError("Utente non autenticato.");
            return;
        }
        service.getRichieste(new GestioneNucleoFamiliareService.RichiesteServiceCallback() {
            @Override
            public void onRichiesteLoaded(List<RichiestaEntity> richieste) {
                callback.onRichiesteLoaded(richieste);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void getMembri(final MembriControlCallback callback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            callback.onError("Utente non autenticato.");
            return;
        }
        service.getMembri(new GestioneNucleoFamiliareService.MembriServiceCallback() {
            @Override
            public void onMembriLoaded(List<MembroEntity> membri) {
                callback.onMembriLoaded(membri);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void getAppoggi(final AppoggiControlCallback callback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            callback.onError("Utente non autenticato.");
            return;
        }
        service.getAppoggi(new GestioneNucleoFamiliareService.AppoggiServiceCallback() {
            @Override
            public void onAppoggiLoaded(List<AppoggioEntity> appoggi) {
                callback.onAppoggiLoaded(appoggi);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void eliminaAppoggio(long appoggioId, final ControlCallback callback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            callback.onError("Utente non autenticato.");
            return;
        }
        service.eliminaAppoggio(appoggioId, new GestioneNucleoFamiliareService.ServiceCallback() {
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

    public void rimuoviMembro(String codiceFiscale, final ControlCallback callback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            callback.onError("Utente non autenticato.");
            return;
        }
        service.rimuoviMembro(codiceFiscale, new GestioneNucleoFamiliareService.ServiceCallback() {
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

    public void mostraFormCreaNucleo() {
        Intent intent = new Intent(context, CreaNucleoBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void mostraFormCreaAppoggio() {
        Intent intent = new Intent(context, InserisciAppoggioBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void apriListaAppoggio(){
        Intent intent = new Intent(context, ListaAppoggioBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void mostraVisualizzaMembri() {
        Intent intent = new Intent(context, VisualizzaMembriBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void mostraListaRichieste() {
        Intent intent = new Intent(context, ListaRichiesteBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void mostraVisualizzaNucleo() {
        Intent intent = new Intent(context, VisualizzaNucleoBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void mostraAggiungiMembro() {
        Intent intent = new Intent(context, AggiungiMembroBoundary.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    
    public void mostraInvitiRicevuti() {
        Intent intent = new Intent(context, ListaRichiesteBoundary.class);
        intent.putExtra("IS_ACTIONABLE", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void inserisciMembro(String nome, String cognome, String codiceFiscale, String dataDiNascita, String sesso, boolean assistenza, boolean minorenne, final ControlCallback controlCallback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            controlCallback.onError("Utente non autenticato.");
            return;
        }
        try {
            service.creaMembro(nome, cognome, codiceFiscale, dataDiNascita, sesso, assistenza, minorenne, new GestioneNucleoFamiliareService.ServiceCallback() {
                @Override
                public void onSuccess(String message) {
                    controlCallback.onSuccess(message);
                }

                @Override
                public void onError(String message) {
                    controlCallback.onError(message);
                }
            });
        } catch (IllegalArgumentException e) {
            controlCallback.onError(e.getMessage());
        }
    }

    /**
     * Avvia il processo di creazione di un nuovo nucleo familiare.
     */
    public void creaNucleo(String viaPiazza, String comune, String regione, String paese, String civico, String cap, boolean hasVeicolo, int postiVeicolo, final ControlCallback controlCallback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            controlCallback.onError("Utente non autenticato.");
            return;
        }
        try {
            service.creaNucleo(viaPiazza, comune, regione, paese, civico, cap,hasVeicolo, postiVeicolo, new GestioneNucleoFamiliareService.ServiceCallback() {
                @Override
                public void onSuccess(String message) {
                    controlCallback.onSuccess(message);
                }

                @Override
                public void onError(String message) {
                    controlCallback.onError(message);
                }
            });
        } catch (IllegalArgumentException e) {
            controlCallback.onError(e.getMessage());
        }
    }


    public void creaAppoggio(String viaPiazza, String civico, String comune, String cap, String provincia, String regione, String paese, final ControlCallback controlCallback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            controlCallback.onError("Utente non autenticato.");
            return;
        }
        try {
            service.creaAppoggio(viaPiazza, civico, comune, cap, provincia, regione, paese, new GestioneNucleoFamiliareService.ServiceCallback() {
                @Override
                public void onSuccess(String message) {
                    controlCallback.onSuccess(message);
                }

                @Override
                public void onError(String message) {
                    controlCallback.onError(message);
                }
            });
        } catch (IllegalArgumentException e) {
            controlCallback.onError(e.getMessage());
        }
    }

    public void modificaResidenza(String viaPiazza, String comune, String regione, String paese, String civico, String cap, final ControlCallback controlCallback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            controlCallback.onError("Utente non autenticato.");
            return;
        }
        try {
            service.modificaResidenza(viaPiazza, comune, regione, paese, civico, cap, new GestioneNucleoFamiliareService.ServiceCallback() {
                @Override
                public void onSuccess(String message) {
                    controlCallback.onSuccess(message);
                }

                @Override
                public void onError(String message) {
                    controlCallback.onError(message);
                }
            });
        } catch (IllegalArgumentException e) {
            controlCallback.onError(e.getMessage());
        }
    }

    /**
     * Avvia la logica per abbandonare il nucleo familiare corrente.
     * @param controlCallback L'interfaccia per notificare l'Activity del risultato.
     */
    public void abbandonaNucleo(final ControlCallback controlCallback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            controlCallback.onError("Utente non autenticato.");
            return;
        }
        service.abbandonaNucleo(new GestioneNucleoFamiliareService.ServiceCallback() {
            @Override
            public void onSuccess(String message) {
                // Traduciamo il successo del service in un successo per l'Activity
                controlCallback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                // Traduciamo l'errore del service in un errore per l'Activity
                controlCallback.onError(message);
            }
        });
    }

    // =================================================================================
    //  NUOVI METODI E INTERFACCE PER L'INVITO (REQUISITO UC-GNF.01)
    // =================================================================================

    /**
     * Interfaccia Callback specifica per la ricerca.
     * Serve per dire all'Activity: "Ho finito di cercare, ecco il Membro trovato (o l'errore)".
     */
    public interface RicercaCallback {
        void onUtenteTrovato(MembroEntity membroEntity);
        void onError(String messaggio);
    }

    /**
     * Metodo chiamato dal bottone "Cerca" nel Dialog.
     * Fa da ponte tra Activity e Service.
     */
    public void cercaMembroPerInvito(String cf, final RicercaCallback callback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            callback.onError("Utente non autenticato.");
            return;
        }
        service.cercaUtentePerInvito(cf, new GestioneNucleoFamiliareRepository.UtenteCallback() {
            @Override
            public void onSuccess(MembroEntity membroTrovato) {
                // Il Service ha trovato l'utente, lo passiamo all'Activity
                callback.onUtenteTrovato(membroTrovato);
            }

            @Override
            public void onError(String message) {
                // Qualcosa è andato storto (non trovato o errore server)
                callback.onError(message);
            }
        });
    }

    /**
     * Metodo chiamato dal bottone "Invita" nel Dialog.
     */
    public void finalizzaInvito(String cf, final ControlCallback callback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            callback.onError("Utente non autenticato.");
            return;
        }
        service.inviaInvito(cf, new GestioneNucleoFamiliareService.ServiceCallback() {
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

    // =================================================================================
    //  METODI PER ACCETTARE RICHIESTA (REQUISITO SC-GNF.02)
    // =================================================================================

    /**
     * Interfaccia di callback specifica per l'operazione di accettazione.
     * Permette alla Boundary di sapere se l'utente è entrato nel nucleo o se c'è stato un errore.
     */
    public interface AccettaRichiestaCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    /**
     * Metodo chiamato dalla Boundary quando l'utente preme "Conferma".
     * Inoltra la chiamata al Service.
     */
    public void accettaRichiesta(long idRichiesta, final AccettaRichiestaCallback callback) {
        if (!VolleySingleton.isUserLoggedIn()) {
            callback.onError("Utente non autenticato.");
            return;
        }
        service.accettaRichiesta(idRichiesta, new GestioneNucleoFamiliareService.ServiceCallback() {
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
