package eruplan.unisa.eruplan.entity;

public class ResidenzaEntity {
    private String viaPiazza;
    private String comune;
    private String regione;
    private String paese;
    private String civico;
    private String cap;

    public ResidenzaEntity() {
    }

    public ResidenzaEntity(String viaPiazza, String comune, String regione, String paese, String civico, String cap) {
        this.viaPiazza = viaPiazza;
        this.comune = comune;
        this.regione = regione;
        this.paese = paese;
        this.civico = civico;
        this.cap = cap;
    }

    // Getters
    public String getViaPiazza() {
        return viaPiazza;
    }

    public String getComune() {
        return comune;
    }

    public String getRegione() {
        return regione;
    }

    public String getPaese() {
        return paese;
    }

    public String getCivico() {
        return civico;
    }

    public String getCap() {
        return cap;
    }

    // Setters
    public void setViaPiazza(String viaPiazza) {
        this.viaPiazza = viaPiazza;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public void setPaese(String paese) {
        this.paese = paese;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }
}
