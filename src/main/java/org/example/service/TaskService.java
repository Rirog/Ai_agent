package org.example.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import lombok.SneakyThrows;
import org.example.security.OAuthTokenManager;

public class TaskService {

    @SneakyThrows
    public static Tasks getTasksService(OAuthTokenManager tokenManager) {
        return new Tasks.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + tokenManager.getAccessToken())
        )
                .setApplicationName("Email Agent")
                .build();
    }
}
