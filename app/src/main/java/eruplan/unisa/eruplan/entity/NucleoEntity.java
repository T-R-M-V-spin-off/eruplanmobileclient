package eruplan.unisa.eruplan.entity;

/**
 * Classe POJO (Plain Old Java Object) che rappresenta l'entità Nucleo Familiare.
 * Contiene i dati relativi all'indirizzo del nucleo.
 */
public class NucleoEntity {

    private String viaPiazza;
    private String comune;
    private String regione;
    private String paese;
    private String civico;
    private String cap;

    public NucleoEntity() {
        // Costruttore vuoto
    }

    public NucleoEntity(String viaPiazza, String comune, String regione, String paese, String civico, String cap) {
        this.viaPiazza = viaPiazza;
        this.comune = comune;
        this.regione = regione;
        this.paese = paese;
        this.civico = civico;
        this.cap = cap;
    }

    // Metodi Getter e Setter

    public String getViaPiazza() {
        return viaPiazza;
    }

    public void setViaPiazza(String viaPiazza) {
        this.viaPiazza = viaPiazza;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public String getPaese() {
        return paese;
    }

    public void setPaese(String paese) {
        this.paese = paese;
    }

    public String getCivico() {
        return civico;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }
}
