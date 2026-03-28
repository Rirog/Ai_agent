package org.example.service.impl;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import lombok.Data;
import lombok.SneakyThrows;
import org.example.security.HashingPassword;
import org.example.service.SmtpEmail;

@Data
public class PasswordSmtpEmailImpl implements SmtpEmail {

    private final String password;

    @Override
    @SneakyThrows
    public Authenticator buildAuthenticator(String email) {
        return new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, HashingPassword.decrypt(password));
            }
        };
    }
}
