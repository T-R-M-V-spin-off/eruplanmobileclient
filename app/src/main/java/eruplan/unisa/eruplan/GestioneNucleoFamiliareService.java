package eruplan.unisa.eruplan;

import java.util.regex.Pattern;

/**
 * Classe Service che contiene la logica di business per la gestione del nucleo familiare.
 * Si occupa della validazione dei dati e della creazione di nuove entità.
 */
public class GestioneNucleoFamiliareService {

    // Pattern per validare il formato della data (gg/mm/aaaa)
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "^((0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((19|20)\\d\\d))$");

    // In futuro qui verrà iniettato il Repository
    // private GestioneNucleoFamiliareRepository repository;

    public GestioneNucleoFamiliareService() {
        // this.repository = new GestioneNucleoFamiliareRepository();
    }

    /**
     * Valida i dati di un membro e, se validi, procede alla creazione.
     *
     * @return un oggetto Membro se i dati sono validi, null altrimenti.
     * @throws IllegalArgumentException se uno dei controlli di validazione fallisce.
     */
    public Membro creaMembro(String nome, String cognome, String codiceFiscale, String dataDiNascita, String sesso, boolean assistenza, boolean minorenne) throws IllegalArgumentException {
        
        // 4.3 Esegue la validazione dei dati
        if (nome == null || nome.trim().length() < 2 || nome.trim().length() > 30) {
            throw new IllegalArgumentException("Il nome non è valido.");
        }
        if (cognome == null || cognome.trim().length() < 2 || cognome.trim().length() > 30) {
            throw new IllegalArgumentException("Il cognome non è valido.");
        }
        if (codiceFiscale == null || codiceFiscale.trim().length() != 16) {
            throw new IllegalArgumentException("Il codice fiscale deve essere di 16 caratteri.");
        }
        if (dataDiNascita == null || !DATE_PATTERN.matcher(dataDiNascita).matches()) {
            throw new IllegalArgumentException("La data di nascita non è valida (formato richiesto: gg/mm/aaaa).");
        }
        if (sesso == null || (!sesso.equals("M") && !sesso.equals("F"))) {
            throw new IllegalArgumentException("Il sesso non è valido.");
        }

        System.out.println("Service: Validazione superata per " + codiceFiscale);

        // 4.2 Crea l'oggetto Membro
        Membro nuovoMembro = new Membro(nome, cognome, codiceFiscale, dataDiNascita, sesso, assistenza, minorenne);

        // 5.1 Inoltra la richiesta al Repository per il salvataggio
        // repository.salvaMembro(nuovoMembro);

        return nuovoMembro;
    }
}
