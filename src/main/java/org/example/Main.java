package org.example;


import org.example.service.*;
import org.example.service.impl.ActionExecutorServiceImpl;
import org.example.service.impl.AiServiceImpl;
import org.example.ui.Input;
import org.example.ui.UserInterface;

public class Main {

    public static void main(String[] args) {
        Input input = new Input();
        AiServiceImpl aiService = new AiServiceImpl();
        UserInterface userInterface = new UserInterface();
        EmailConfig emailConfig = new EmailConfig();
        EmailParser emailParser = new EmailParser();
        ActionExecutorServiceImpl actionExecutorService = new ActionExecutorServiceImpl();

        AiController aiController = new AiController(
                aiService,
                input,
                userInterface,
                emailConfig,
                emailParser,
                actionExecutorService
        );
        aiController.start();
    }
}