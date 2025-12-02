package eruplan.unisa.eruplan;

/**
 * Classe POJO (Plain Old Java Object) che rappresenta l'entità Membro del nucleo familiare.
 * Contiene i dati anagrafici di un singolo membro.
 */
public class Membro {

    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String dataDiNascita;
    private String sesso;
    private boolean assistenza;
    private boolean minorenne;

    // Costruttore vuoto necessario per alcune librerie di serializzazione (es. Gson, Jackson)
    public Membro() {
    }

    // Costruttore con tutti i campi
    public Membro(String nome, String cognome, String codiceFiscale, String dataDiNascita, String sesso, boolean assistenza, boolean minorenne) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.dataDiNascita = dataDiNascita;
        this.sesso = sesso;
        this.assistenza = assistenza;
        this.minorenne = minorenne;
    }

    // Metodi Getter e Setter per ogni campo

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getDataDiNascita() {
        return dataDiNascita;
    }

    public void setDataDiNascita(String dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public boolean isAssistenza() {
        return assistenza;
    }

    public void setAssistenza(boolean assistenza) {
        this.assistenza = assistenza;
    }

    public boolean isMinorenne() {
        return minorenne;
    }

    public void setMinorenne(boolean minorenne) {
        this.minorenne = minorenne;
    }
}
