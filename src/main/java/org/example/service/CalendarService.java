package org.example.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import lombok.SneakyThrows;
import org.example.security.OAuthTokenManager;

public class CalendarService {

    @SneakyThrows
    public static Calendar getCalendarService(OAuthTokenManager tokenManager) {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + tokenManager.getAccessToken())
        )
                .setApplicationName("Email Agent")
                .build();
    }
}