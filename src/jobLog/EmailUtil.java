package jobLog;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtil {

    public static void sendEmail(String subject, String body, String recipients) {
        String from = "faizan.ppc@gmail.com";  // Sender email
        final String username = "faizan.ppc@gmail.com"; // Gmail ID
        final String password = "vcdv tvmi kkop akgo";    // Gmail App Password

        // SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Auth
        Session session = Session.getInstance(props,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            // Multiple recipients (comma separated)
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipients)
            );

            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("[EmailUtil] Email sent successfully to " + recipients);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
