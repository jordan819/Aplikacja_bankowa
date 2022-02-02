package pl.pwsztar.rest.connect;

/**
 * Reprezentuje klienta banku.
 * Przechowuje i udostepnia informacje o uzytkowniku.
 */
public class Customer {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String idAccount;
    private final String verificationCode;
    private final boolean isVerified;


    /**
     * Tworzy obiekt reprezentujacy uzytkownika.
     *
     * @param firstName imie uzytkownika
     * @param lastName nazwisko uzytkownika
     * @param email adres email uzytkownika
     * @param password haslo uzytkownika
     * @param idAccount numer konta uzytkownika
     * @param verificationCode kod weryfikacyjny uzytkownika
     * @param isVerified informacja, czy konto zostalo zweryfikowane
     */
    public Customer(String firstName, String lastName, String email,
                        String password, String idAccount, String verificationCode, boolean isVerified) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.idAccount = idAccount;
        this.verificationCode = verificationCode;
        this.isVerified = isVerified;
    }

    /**
     * @return imie uzytkownika
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return nazwisko uzytkownika
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return adres email uzytkownika
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return haslo uzytkownika
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return numer konta uzytkownika
     */
    public String getIdAccount() {
        return idAccount;
    }

    /**
     * @return kod weryfikacyjny uzytkownika
     */
    public String getVerificationCode() {
        return verificationCode;
    }

    /**
     * @return true, jesli konto zostalo zweryfikowane
     */
    public boolean isVerified() {
        return isVerified;
    }

}
