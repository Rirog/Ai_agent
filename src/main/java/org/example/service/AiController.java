package org.example.service;

import jakarta.mail.Message;
import lombok.SneakyThrows;
import org.example.dto.response.AiResult;
import org.example.service.impl.ActionExecutorServiceImpl;
import org.example.service.impl.AiServiceImpl;
import org.example.ui.Input;
import org.example.ui.UserInterface;

public class AiController {


    private final AiServiceImpl aiService;
    private final Input input;
    private final UserInterface userInterface;
    private final EmailConfig emailConfig;
    private final EmailParser emailParser;
    private final ActionExecutorServiceImpl executor;

    public AiController(AiServiceImpl aiService,
                        Input input,
                        UserInterface userInterface,
                        EmailConfig emailConfig,
                        EmailParser emailParser,
                        ActionExecutorServiceImpl executor) {

        this.aiService = aiService;
        this.input = input;
        this.userInterface = userInterface;
        this.emailConfig = emailConfig;
        this.emailParser = emailParser;
        this.executor = executor;
    }

    public void start() {
        boolean running = true;

        while (running) {

            userInterface.getMenu();
            int choice = input.readChoice();

            switch (choice) {
                case 0 -> running = false;
                case 1 -> workAi();
                default -> userInterface.outputErrorChoice();
            }
        }
        input.close();
    }

    @SneakyThrows
    private void workAi() {

        Message[] messages = emailConfig.connectionEmail();

        for (Message msg : messages) {


            String prompt = emailParser.parse(msg);

            AiResult response = aiService.generateResponse(prompt);

            handle(response, msg);
        }
        emailConfig.deletedSpam();
    }


    private void handle(AiResult response, Message msg) {
        switch (response.getType()) {

            case "SPAM" -> executor.emailSpam(msg);

            case "TASK" -> executor.emailTask(msg, response);

            case "MEETING" -> executor.emailMeeting(msg, response);

            case "QUESTION" -> executor.emailQuestion(msg, response, emailConfig);

            default -> userInterface.outputErrorEmail();
        }
    }

}
