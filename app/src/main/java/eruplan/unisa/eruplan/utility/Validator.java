package eruplan.unisa.eruplan.utility;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validator {

    /*
     * REGEX E PATTERN GUM
     */
    private static final String SESSO_REGEX = "^[MF]$";
    private static final Pattern SESSO_PATTERN = Pattern.compile(SESSO_REGEX);

    /*
     * REGEX E PATTERN GPE (Nuovi)
     */
    private static final String NOME_PIANO_REGEX = "^[a-zA-Z0-9\\s]{3,35}$";
    private static final Pattern NOME_PIANO_PATTERN = Pattern.compile(NOME_PIANO_REGEX);

    /*
     * REGEX E PATTERN COMUNI
     */
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final String NOME_PERSONA_REGEX = "^[a-zA-Z\\s]{2,30}$";
    private static final Pattern NOME_PERSONA_PATTERN = Pattern.compile(NOME_PERSONA_REGEX);

    // Pattern legacy: accetta 0-9 e lettere (utile per validazione generale esistente)
    private static final String CODICE_FISCALE_REGEX = "^[A-Za-z0-9]{16}$";
    private static final Pattern CODICE_FISCALE_PATTERN = Pattern.compile(CODICE_FISCALE_REGEX);

    // Nuovo pattern per TD-M-20: 16 caratteri, solo A-Za-z e cifre 1-9 (ZERO ESCLUSO)
    private static final String CODICE_FISCALE_CHARS_REGEX = "^[A-Za-z1-9]{16}$";
    private static final Pattern CODICE_FISCALE_CHARS_PATTERN = Pattern.compile(CODICE_FISCALE_CHARS_REGEX);

    private static final String COGNOME_PERSONA_REGEX="^[a-zA-Z\\s]{2,30}$";
    private static final Pattern COGNOME_PERSONA_PATTERN = Pattern.compile(COGNOME_PERSONA_REGEX);

    /*
     * METODI DI VALIDAZIONE
     */
    public static boolean isNomeValid(String nome) {
        if (nome == null || nome.compareTo("") == 0) return false;
        return NOME_PERSONA_PATTERN.matcher(nome).matches();
    }

    /**
     * Validazione "completa" legacy (mantengo per retrocompatibilità)
     */
    public static boolean isCodiceFiscaleValid(String cf) {
        if (cf == null || cf.compareTo("") == 0) return false;
        return CODICE_FISCALE_PATTERN.matcher(cf).matches();
    }

    /**
     * Verifica la sola lunghezza (TD: LC)
     * @param cf codice fiscale
     * @return true se cf.length() == 16
     */
    public static boolean isCodiceFiscaleLengthValid(String cf) {
        return cf != null && cf.length() == 16;
    }

    /**
     * Verifica i soli caratteri ammessi secondo TD (A-Za-z e cifre 1-9, ZERO escluso)
     * IMPORTANTE: questo metodo assume CF già lungo 16 (la chiamata dei test / controller
     * dovrebbe prima controllare la lunghezza).
     * @param cf codice fiscale
     * @return true se cf contiene solo caratteri ammessi (no '0') e lunghezza 16
     */
    public static boolean isCodiceFiscaleCharactersValid(String cf) {
        if (cf == null) return false;
        return CODICE_FISCALE_CHARS_PATTERN.matcher(cf).matches();
    }

    public static boolean isSessoValid(String sesso) {
        if (sesso == null || sesso.compareTo("") == 0) return false;
        return SESSO_PATTERN.matcher(sesso).matches();
    }

    public static boolean isPasswordValid(String password) {
        if (password == null || password.compareTo("") == 0) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    public static boolean isCognomeValid(String cognome){
        if (cognome == null || cognome.compareTo("") == 0) return false;
        return COGNOME_PERSONA_PATTERN.matcher(cognome).matches();
    }

    public static boolean isDataNascitaValid(LocalDate data) {
        return data != null && data.isBefore(LocalDate.now());
    }

    /**
     * Verifica se il nome del piano di evacuazione è valido.
     *
     * @param nomePiano Il nome da verificare
     * @return true se valido, false altrimenti
     */
    public static boolean isNomePianoValid(String nomePiano) {
        if (nomePiano == null || nomePiano.trim().isEmpty()) {
            return false;
        }
        return NOME_PIANO_PATTERN.matcher(nomePiano.trim()).matches();
    }
}
