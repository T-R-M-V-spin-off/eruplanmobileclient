package eruplan.unisa.eruplan;

/**
 * Classe POJO che rappresenta l'invito ad accedere al nucleo familiare.
 */
public class Richiesta {
    private String nomeMittente;
    private String dataOra;

    // Costruttore utilizzato per creare l'oggetto dai dati del server
    public Richiesta(String nomeMittente, String dataOra) {
        this.nomeMittente = nomeMittente;
        this.dataOra = dataOra;
    }

    // Getter coerenti con l'uso nell'Adapter
    public String getNomeMittente() { return nomeMittente; }
    public String getDataOra() { return dataOra; }
}