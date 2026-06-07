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
import com.google.api.services.gmail.Gmail;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;

public class OAuthTokenManager {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY = System.getProperty("user.home") +
            File.separator + ".ai_agent" +
            File.separator + "tokens";

    private static final int LOCAL_SERVER_PORT = 8889;

    private static final List<String> SCOPES = List.of(
            "https://mail.google.com/",
            "https://www.googleapis.com/auth/gmail.modify",
            "https://www.googleapis.com/auth/gmail.readonly",
            "https://www.googleapis.com/auth/calendar",
            "https://www.googleapis.com/auth/calendar.events",
            "https://www.googleapis.com/auth/tasks"
    );

    @Getter
    private Credential credential;

    @SneakyThrows
    private void ensureTokensDirectoryExists() {
        File dir = new File(TOKENS_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @SneakyThrows
    public String getAuthorizationUrl() {
        ensureTokensDirectoryExists();
        Reader reader = new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/credentials.json"))
        );
        var secrets = GoogleClientSecrets.load(JSON_FACTORY, reader);

        var flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, secrets, SCOPES
        )
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY)))
                .setAccessType("offline")
                .build();

        return flow.newAuthorizationUrl()
                .setRedirectUri("http://localhost:" + LOCAL_SERVER_PORT + "/Callback")
                .build();
    }

    @SneakyThrows
    public void authorize() {
        Reader reader = new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/credentials.json"))
        );
        var secrets = GoogleClientSecrets.load(JSON_FACTORY, reader);

        var flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, secrets, SCOPES
        )
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY)))
                .setAccessType("offline")
                .build();

        Credential saved = flow.loadCredential("user");
        credential = saved != null ? saved :
                new AuthorizationCodeInstalledApp(
                        flow,
                        new LocalServerReceiver.Builder().setPort(LOCAL_SERVER_PORT).build()
                ).authorize("user");
    }

    @SneakyThrows
    public String getEmailUser() {
        if (credential == null) {
            throw new IllegalStateException("Сначало надо авторизоваться");
        }
        Gmail gmail = new Gmail.Builder(new NetHttpTransport(), JSON_FACTORY, credential)
                .setApplicationName("Email Agent")
                .build();

        return gmail.users().getProfile("me").execute().getEmailAddress();
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
    @SneakyThrows
    public void clearTokens() {
        File dir = new File(TOKENS_DIRECTORY);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) f.delete();
            }
            dir.delete();
        }
        credential = null;
    }
}
