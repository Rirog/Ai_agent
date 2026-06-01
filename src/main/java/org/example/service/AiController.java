package org.example.service;

import jakarta.mail.Message;

import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.example.dto.response.AiResult;
import org.example.service.impl.ActionExecutorServiceImpl;
import org.example.service.impl.AiServiceImpl;
import org.example.service.config.EmailConfig;

import java.lang.reflect.Method;

public class AiController {


    private final AiServiceImpl aiService;
    private final EmailConfig emailConfig;
    private final EmailParser emailParser;
    private final ActionExecutorServiceImpl executor;

    public AiController(AiServiceImpl aiService,
                        EmailConfig emailConfig,
                        EmailParser emailParser,
                        ActionExecutorServiceImpl executor) {

        this.aiService = aiService;
        this.emailConfig = emailConfig;
        this.emailParser = emailParser;
        this.executor = executor;
    }

    @SneakyThrows
    public void workAi() {
        Message[] messages = emailConfig.connectionEmail();
        if (messages == null || messages.length == 0) return;

        for (Message msg : messages) {

            String prompt = emailParser.parse(msg);

            AiResult response = aiService.generateResponse(prompt);

            handle(response, msg);
        }
    }


    @SneakyThrows
    private void handle(AiResult response, Message msg) {
        Class<?> executorClass = executor.getClass();
        Method method = executorClass.getMethod(response.getType(), Message.class, AiResult.class);
        method.invoke(executor, msg, response);
    }

    public void reloadApiKey() {
        aiService.reloadApi();
    }
}
