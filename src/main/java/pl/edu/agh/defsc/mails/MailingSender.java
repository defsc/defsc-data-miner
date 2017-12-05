package pl.edu.agh.defsc.mails;

import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Component
public class MailingSender {
    private Properties props;
    private final String username = <SENDER_MAIL>;
    private final String password = <SENDER_PASSWORD>;

    public MailingSender() {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    public void sendMails(String requestCounter, String counter) {

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(<SENDER_MAIL>));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(<RECEIVER_MAILS_LIST>));

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(new Date());

            message.setSubject("[" + date + "] Data Mining raport");
            message.setText(counter
                    + "\n\n" + requestCounter);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
