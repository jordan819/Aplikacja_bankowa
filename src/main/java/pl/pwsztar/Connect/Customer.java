package pl.pwsztar.Connect;

public class Customer {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String idAccount;
    private final String verificationCode;
    private final boolean isVerified;


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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getIdAccount() {
        return idAccount;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public boolean isVerified() {
        return isVerified;
    }

}
