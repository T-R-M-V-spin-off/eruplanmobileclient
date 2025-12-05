package eruplan.unisa.eruplan.entity;

public class AppoggioEntity {

    private long id;
    private String viaPiazza;
    private String civico;
    private String comune;
    private String cap;
    private String provincia;
    private String regione;
    private String paese;

    public AppoggioEntity(String viaPiazza, String civico, String comune, String cap, String provincia, String regione, String paese) {
        this.viaPiazza = viaPiazza;
        this.civico = civico;
        this.comune = comune;
        this.cap = cap;
        this.provincia = provincia;
        this.regione = regione;
        this.paese = paese;
    }

    // Getter e Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getViaPiazza() {
        return viaPiazza;
    }

    public void setViaPiazza(String viaPiazza) {
        this.viaPiazza = viaPiazza;
    }

    public String getCivico() {
        return civico;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
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
}
