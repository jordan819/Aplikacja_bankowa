package pl.pwsztar.rest.connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Umozliwia wyslanie maila, oraz sprawdzenie poprawnosci adresu email.
 */
public abstract class SendEmailTLS {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailTLS.class);

    /**
     * Umozliwia wyslanie maila.
     * Adres nadawcy jest zdefiniowany na stale,
     * wymagane jest podanie adresu odbiorcy, oraz tematu i tresci wysylanej wiadomosci.
     *
     * @param addressee adresat wiadomosci
     * @param subject temat wiadomosci
     * @param text tresc wiadomosci
     */
    public static void send(String addressee, String subject, String text){
        LOGGER.info("Wysyłanie wiadomości na adres {}...", addressee);

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
            LOGGER.info("Wiadomość została wysłana");

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
