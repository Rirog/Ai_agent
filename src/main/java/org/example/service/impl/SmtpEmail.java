package org.example.service.impl;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import lombok.Data;
import lombok.SneakyThrows;
import org.example.security.HashingPassword;

@Data
public class SmtpEmail {

    private final String password;

    @SneakyThrows
    public Authenticator buildAuthenticator(String email) {
        String encrypt = HashingPassword.decrypt(password);
        return new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, encrypt);
            }
        };
    }
}
