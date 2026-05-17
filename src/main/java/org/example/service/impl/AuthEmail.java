package org.example.service.impl;

import jakarta.mail.Store;
import lombok.Data;
import lombok.SneakyThrows;
import org.example.security.OAuthTokenManager;

@Data
public class AuthEmail {

    private final OAuthTokenManager tokenManager;

    public AuthEmail(String email) {
        this.tokenManager = new OAuthTokenManager();
        this.tokenManager.authorize(email);
    }

    @SneakyThrows
    public void connectionEmail(Store store, String host, String email) {
        String accessToken = tokenManager.getAccessToken();
        store.connect(host, email, accessToken);
    }

    public String getAccessToken() {
        return tokenManager.getAccessToken();
    }
}