package org.example.service;

import jakarta.mail.Authenticator;

public interface SmtpEmail {
    Authenticator buildAuthenticator(String email);
}
