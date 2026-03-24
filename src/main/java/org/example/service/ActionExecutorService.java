package org.example.service;

import jakarta.mail.Message;
import org.example.dto.response.AiResult;

public interface ActionExecutorService {

    void emailQuestion(Message message, AiResult aiResult, EmailConfig emailConfig);
    void emailMeeting(Message message, AiResult aiResult);

    void emailTask(Message message, AiResult aiResult);

    void emailSpam(Message message);
}
