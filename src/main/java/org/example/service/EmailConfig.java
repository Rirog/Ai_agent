package org.example.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.FlagTerm;
import lombok.Data;
import lombok.SneakyThrows;
import org.example.config.ConfigManager;

import java.util.Properties;


@Data
public class EmailConfig {
    private Store store;
    private final String folder = "INBOX";
    private Folder inbox;


    @SneakyThrows
    public Message[] connectionEmail(String email, String password) {
        Properties prop = new Properties();
        prop.put(ConfigManager.getEmailProtocolDefault(), ConfigManager.getEmailProtocol());

        Session session = Session.getInstance(prop);

        store = session.getStore(ConfigManager.getEmailProtocol());
        store.connect(ConfigManager.getEmailHost(), email, password);

        inbox = store.getFolder(folder);

        inbox.open(Folder.READ_WRITE);

        return inbox.search(
                new FlagTerm(new Flags(Flags.Flag.SEEN), false)
        );
    }

    @SneakyThrows
    public void deletedSpam() {
        inbox.expunge();
        inbox.close();
    }

    @SneakyThrows
    public void send(String to, String subject, String text) {

        to = extractEmail(to);

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");


        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, appPassword);
                    }
                }
        );

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(email));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to)
        );

        message.setSubject(subject);

        message.setText(text);

        Transport.send(message);
    }

    private String extractEmail(String raw) {

        if (raw.contains("<")) {
            return raw.substring(
                    raw.indexOf("<") + 1,
                    raw.indexOf(">")
            );
        }
        return raw;
    }
}
