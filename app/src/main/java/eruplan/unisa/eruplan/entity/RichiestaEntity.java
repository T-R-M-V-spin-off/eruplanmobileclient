package eruplan.unisa.eruplan.entity;

/**
 * Rappresenta i dati di un singolo invito.
 * Aggiornato per includere l'ID univoco necessario per l'accettazione.
 */
public class RichiestaEntity {

    // COMMENTO: L'ID serve al server per identificare la richiesta nel database.
    private final long id;
    private final String nomeMittente;
    private final String dataOra;

    // Costruttore completo
    public RichiestaEntity(long id, String nomeMittente, String dataOra) {
        this.id = id;
        this.nomeMittente = nomeMittente;
        this.dataOra = dataOra;
    }

    public long getId() { return id; }
    public String getNomeMittente() { return nomeMittente; }
    public String getDataOra() { return dataOra; }
}