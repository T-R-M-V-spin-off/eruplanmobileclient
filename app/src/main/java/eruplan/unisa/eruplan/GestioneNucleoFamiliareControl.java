package eruplan.unisa.eruplan;

/**
 * Classe Control che gestisce le interazioni tra l'interfaccia utente (Boundary) 
 * e la logica di business (Service) per la gestione del nucleo familiare.
 */
public class GestioneNucleoFamiliareControl {

    private GestioneNucleoFamiliareService service;

    public GestioneNucleoFamiliareControl() {
        this.service = new GestioneNucleoFamiliareService();
    }

    /**
     * Avvia il processo di inserimento di un nuovo membro del nucleo familiare.
     * Inoltra la richiesta al Service per la validazione e la creazione.
     *
     * @return un oggetto Membro se i dati sono validi.
     * @throws IllegalArgumentException se la validazione dei dati fallisce.
     */
    public Membro inserisciMembro(String nome, String cognome, String codiceFiscale, String dataDiNascita, String sesso, boolean assistenza, boolean minorenne) throws IllegalArgumentException {
        
        // Inoltra la richiesta al Service per la validazione e la creazione
        Membro nuovoMembro = service.creaMembro(nome, cognome, codiceFiscale, dataDiNascita, sesso, assistenza, minorenne);
        
        System.out.println("Control: Membro creato con successo: " + nuovoMembro.getCodiceFiscale());

        // In un'implementazione reale, qui si potrebbe notificare l'interfaccia utente.
        return nuovoMembro;
    }
}
