package org.example.service.config;

import com.google.api.services.calendar.Calendar;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.security.OAuthTokenManager;
import org.example.service.CalendarService;

public class CalendarConfig {
    private final OAuthTokenManager tokenManager;

    @Getter
    private Calendar calendar;

    public CalendarConfig(OAuthTokenManager tokenManager) {
        this.tokenManager = tokenManager;
        initCalendar();
    }

    @SneakyThrows
    public void initCalendar() {
        calendar = CalendarService.getCalendarService(tokenManager);
    }
}
