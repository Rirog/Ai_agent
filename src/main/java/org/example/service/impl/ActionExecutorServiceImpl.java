package org.example.service.impl;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import lombok.SneakyThrows;
import org.example.dto.response.AiResult;
import org.example.service.ActionExecutorService;
import org.example.service.config.CalendarConfig;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActionExecutorServiceImpl implements ActionExecutorService {

    private final CalendarConfig calendarConfig;

    public ActionExecutorServiceImpl(CalendarConfig calendarConfig) {
        this.calendarConfig = calendarConfig;
    }

    @Override
    @SneakyThrows
    public void emailMeeting(Message message, AiResult aiResult) {
        calendarConfig.initCalendar();
        Calendar calendar = calendarConfig.getCalendar();


        String date = aiResult.getDate();     // YYYY-MM-DD а тут годы месяцы и деньки
        String time = aiResult.getTime();     // HH-MM типа часы и минуты

        String summary = aiResult.getSummary() != null ? aiResult.getSummary() : message.getSubject();
        String description = aiResult.getTask() != null ? aiResult.getTask() : "";

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(date + " " + time, dateTimeFormatter);
        LocalDateTime endDateTime = startDateTime.plusHours(1);

        Event event = new Event()
                .setSummary(summary)
                .setDescription("Из письма: " + message.getSubject() + "\n" + description);

        EventDateTime start = new EventDateTime()
                .setDateTime(new DateTime(startDateTime + ":00+03:00"))
                .setTimeZone("Europe/Moscow");

        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(endDateTime + ":00+03:00"))
                .setTimeZone("Europe/Moscow");
        event.setEnd(end);

        String calendarId = "primary";
        calendar.events().insert(calendarId, event).execute();

        emailSeen(message);
//
//        String fileName = "meeting.txt";    // пока так, думаю что делать
//        String separator = "---------------\n";
//
//        try (FileWriter writer = new FileWriter(fileName)) {
//            writer.write(message.getSubject());
//
//            writer.write(aiResult.getDate() + "\n");
//            writer.write(aiResult.getTime() + "\n");
//
//            writer.write(separator);
//        }
//        emailSeen(message);
    }

    @Override
    @SneakyThrows
    public void emailTask(Message message, AiResult aiResult) {
        String fileName = "tasks.txt";
        String separator = "---------------\n";

        try (FileWriter writer = new FileWriter(fileName)) {

            writer.write(aiResult.getDate() + "\n");
            writer.write(aiResult.getTask() + "\n");

            writer.write(separator);
        }
        emailSeen(message);
    }

    @Override
    @SneakyThrows
    public void emailSpam(Message message, AiResult aiResult) {
        message.setFlag(Flags.Flag.DELETED, true);
        String fileName = "spamDeleted.txt"; // чисто вывод того что удалилось
        String separator = "---------------\n";

        try (FileWriter writer = new FileWriter(fileName)) {

            writer.write(aiResult.getDate() + "\n");
            writer.write(aiResult.getSummary() + "\n");

            writer.write(separator);
        }
        emailSeen(message);
    }

    @SneakyThrows
    private void emailSeen(Message message) {
        message.setFlag(Flags.Flag.SEEN, true);
    }
}
