package pl.pwsztar.Connect;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Klasa umozliwiajaca wyslanie wiadomosci email do pojedynczego adresata.
 *
 * <p>
 *     Adres nadawcy jest zdefiniowany na stale,
 *     mozliwe jest podanie adresu odbiorcy, oraz tematu i tresci wiadomosci.
 * </p>
 * <p>
 *     Klasa posiada publiczna metode, pozwalajaca sprawdzic poprawnosc dowolnego adresu email.
 * </p>
 */
public class SendEmailTLS {

    private static String addressee;
    private static final String subject = "Kod weryfikacyjny";
    private static String text;


    public SendEmailTLS(String addressee, String code) {
        setAddressee(addressee);
        setText(code);

        send();
    }

    private static void setAddressee(String addressee) {
        SendEmailTLS.addressee = addressee;
    }

    private static void setText(String code) {
        SendEmailTLS.text = "Witaj, tu Twój bank.\n\n" +
                "Oto Twój kod weryfikacyjny: " + code;
    }

    private static void send(){
        System.out.println("Wysyłanie wiadomości na adres " + addressee + "...");

        final String sender = "twoj.kochany.bank@gmail.com";
        final String password = "ujzkgjdamtqpfzqa";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(addressee)
            );
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);


            System.out.println("Wiadomość została wysłana\n");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda pozwalajaca sprawdzic, czy podany adres email zawiera poprawna skladnie.
     *
     * <p>
     *     Nie jest sprawdzane czy adres istnieje, jedynie czy jego zapis jest poprawny.
     * </p>
     *
     * @param email adres email, ktorego poprawnosc jest sprawdzana
     * @return true, jesli adres jest poprawny, false w przeciwnym razie
     */
    public static boolean isEmailAddressValid(String email) {
        boolean result = true;
        try {
            InternetAddress InternetAddress = new InternetAddress(email);
            InternetAddress.validate();
        } catch (AddressException | NullPointerException ex ) {
            result = false;
        }
        return result;
    }
}