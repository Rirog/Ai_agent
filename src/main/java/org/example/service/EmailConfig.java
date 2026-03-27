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
    private Folder inbox;

    private final String folder = ConfigManager.getEmailFolder();
    private final String email;
    private final AuthEmail authEmail;
    private final SmtpEmail smtpEmail;

    @SneakyThrows
    public Message[] connectionEmail() {
        Properties prop = new Properties();
        prop.put("mail.store.protocol", ConfigManager.getEmailProtocol());

        Session session = Session.getInstance(prop);

        store = session.getStore(ConfigManager.getEmailProtocol());
        authEmail.connectionEmail(store, ConfigManager.getEmailHost(), email);

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

        String truth = "true";

        to = extractEmail(to);

        Properties props = new Properties();

        props.put("mail.smtp.auth", truth);
        props.put("mail.smtp.starttls.enable", truth);
        props.put("mail.smtp.host", ConfigManager.getEmailSmtpHost());
        props.put("mail.smtp.port", ConfigManager.getEmailSmtpPort());


        Session session = Session.getInstance(props, smtpEmail.buildAuthenticator(email));

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
