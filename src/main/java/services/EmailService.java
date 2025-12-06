package services;

import configs.AppConfig;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    private final Session session;
    private final String from;

    public EmailService() {
        this.from = resolveSender();
        this.session = buildSession(from, resolvePassword());
    }

    public boolean sendEmail(String to, String subject, String body) {
        if (session == null || from == null || from.isBlank() || to == null || to.isBlank()) {
            return false;
        }
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            return true;
        } catch (AuthenticationFailedException ex) {
            LOGGER.log(Level.WARNING,
                    "No se pudo enviar el mail a " + to
                            + ". Verificá que la contraseña de la cuenta tenga acceso de aplicación específica.",
                    ex);
            return false;
        } catch (MessagingException ex) {
            LOGGER.log(Level.WARNING, "No se pudo enviar el mail a " + to, ex);
            return false;
        }
    }

    private String resolveSender() {
        String configured = AppConfig.get("mail.username", AppConfig.get("company.email", ""));
        return configured != null ? configured.trim() : null;
    }

    private String resolvePassword() {
        String configured = AppConfig.get("mail.password", "");
        return configured != null ? configured.trim() : null;
    }

    private Session buildSession(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        return Session.getInstance(props, auth);
    }
}
