package org.example.service.impl;

import jakarta.mail.Store;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.security.OAuthTokenManager;

@Data
public class AuthEmail {

    @Getter
    private final OAuthTokenManager tokenManager;

    public AuthEmail() {
        tokenManager = new OAuthTokenManager();
        tokenManager.authorize();
    }

    @SneakyThrows
    public void connectionEmail(Store store, String host, String email) {
        String accessToken = tokenManager.getAccessToken();
        store.connect(host, email, accessToken);
    }
}