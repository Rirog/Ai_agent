package org.example.security;


import com.google.api.client.auth.oauth2.Credential;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;

public class OAuthTokenManager {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY = "tokens";

    private static final int LOCAL_SERVER_PORT = 8888;

    private static final List<String> SCOPES = List.of(
            "https://mail.google.com/",
            "https://www.googleapis.com/auth/gmail.modify",
            "https://www.googleapis.com/auth/gmail.readonly"
    );

    @Getter
    private Credential credential;


    @SneakyThrows
    public void authorize(String email) {

        Reader credentialsReader = new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/credentials.json"))
        );

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, credentialsReader
        );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder
                (
                        new NetHttpTransport(),
                        JSON_FACTORY,
                        clientSecrets,
                        SCOPES
                )
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY)))
                .setAccessType("offline")
                .build();

        Credential savedCredential = flow.loadCredential(email);

        if (savedCredential != null) {
            credential = savedCredential;
        } else {
            LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                    .setPort(LOCAL_SERVER_PORT)
                    .build();
            credential = new AuthorizationCodeInstalledApp(flow, receiver)
                    .authorize(email);
        }

    }

    @SneakyThrows
    public String getAccessToken() {
        if (credential == null) {
            throw new IllegalStateException();
        }

        if (credential.getExpiresInSeconds() == null || credential.getExpiresInSeconds() <= 60) {
            credential.refreshToken();
        }

        return credential.getAccessToken();
    }

}
