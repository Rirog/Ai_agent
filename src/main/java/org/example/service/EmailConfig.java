package org.example.service;

import jakarta.mail.*;
import jakarta.mail.search.FlagTerm;
import lombok.Data;
import lombok.SneakyThrows;
import org.example.config.ConfigManager;
import org.example.service.impl.AuthEmail;

import java.util.Properties;

@Data
public class EmailConfig implements GmailConfig {
    private Store store;
    private Folder inbox;

    private final String folder = ConfigManager.getEmailFolder();
    private final String email;
    private final AuthEmail authEmail;

    @Override
    @SneakyThrows
    public Message[] connectionEmail() {
        Properties prop = new Properties();
        prop.put("mail.store.protocol", ConfigManager.getEmailProtocol());
        prop.put("mail.imap.auth.mechanisms", ConfigManager.getEmailMechanisms());
        prop.put("mail.imap.ssl.trust", "*");
        prop.put("mail.imap.port", "993");

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
        if (inbox != null && inbox.isOpen()) {
            inbox.expunge();
            inbox.close();
        }
        if (store != null) {
            store.close();
        }
    }
}