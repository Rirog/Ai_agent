package org.example.service.config;

import jakarta.mail.*;
import jakarta.mail.search.FlagTerm;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.config.ConfigManager;
import org.example.service.impl.AuthEmail;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Properties;

@Data
public class EmailConfig {
    private Store store;
    private Folder inbox;

    private final String folder = ConfigManager.getEmailFolder();
    private final String email;

    @Getter
    private final AuthEmail authEmail;

    @SneakyThrows
    public Message[] connectionEmail() {
        Properties prop = new Properties();
        prop.put("mail.store.protocol", ConfigManager.getEmailProtocol());
        prop.put("mail.imaps.auth.mechanisms", ConfigManager.getEmailMechanisms());
        prop.setProperty("mail.imap.ssl.trust", "*");
        prop.put("mail.imap.connectiontimeout", "10000");
        prop.put("mail.imap.timeout", "30000");

        Session session = Session.getInstance(prop);
        store = session.getStore(ConfigManager.getEmailProtocol());
        authEmail.connectionEmail(store, ConfigManager.getEmailHost(), email);

        inbox = store.getFolder(folder);
        inbox.open(Folder.READ_WRITE);

        return inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));


    }

    @SneakyThrows
    public void close() {

        if (inbox != null && inbox.isOpen()) {
            inbox.close(true);
        }

        if (store != null && store.isConnected()) {
            store.close();
        }
    }
}