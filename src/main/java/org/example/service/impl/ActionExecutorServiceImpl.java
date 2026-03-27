package org.example.service.impl;

import jakarta.mail.Flags;
import jakarta.mail.Message;
import lombok.SneakyThrows;
import org.example.dto.response.AiResult;
import org.example.service.ActionExecutorService;
import org.example.service.EmailConfig;

import java.io.FileWriter;

public class ActionExecutorServiceImpl implements ActionExecutorService {


    @Override
    @SneakyThrows
    public void emailQuestion(Message message, AiResult aiResult, EmailConfig emailConfig) {
        emailConfig.send(message.getFrom()[0].toString(), message.getSubject(), aiResult.getSummary());
    }

    @Override
    @SneakyThrows
    public void emailMeeting(Message message, AiResult aiResult) {

        String fileName = "meeting.txt";    // пока так, думаю что делать
        String separator = "---------------\n";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(message.getSubject());

            writer.write(aiResult.getDate() + "\n");
            writer.write(aiResult.getTime() + "\n");

            writer.write(separator);
        }
        emailSeen(message);
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
    public void emailSpam(Message message) {
        message.setFlag(Flags.Flag.DELETED, true);
    }

    @SneakyThrows
    private void emailSeen(Message message) {
        message.setFlag(Flags.Flag.SEEN, true);
    }
}
