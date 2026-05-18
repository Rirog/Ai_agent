package org.example;

import lombok.SneakyThrows;
import org.example.security.OAuthTokenManager;
import org.example.service.*;
import org.example.service.config.CalendarConfig;
import org.example.service.impl.ActionExecutorServiceImpl;
import org.example.service.impl.AiServiceImpl;
import org.example.service.impl.AuthEmail;
import org.example.service.config.EmailConfig;
import org.example.ui.Input;
import org.example.ui.UserInterface;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {

        Input input = new Input();
        UserInterface userInterface = new UserInterface();


        AiServiceImpl aiService = new AiServiceImpl();

        EmailConfig emailConfig = credentialUser(userInterface, input);
        EmailParser emailParser = new EmailParser();

        OAuthTokenManager tokenManager = emailConfig.getAuthEmail().getTokenManager();

        CalendarConfig calendarConfig = new CalendarConfig(tokenManager);

        ActionExecutorServiceImpl actionExecutorService = new ActionExecutorServiceImpl(calendarConfig);


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

    @SneakyThrows
    private static EmailConfig credentialUser(UserInterface ui, Input input) {
        if (CredentialManager.exists()) {
            String credential = CredentialManager.loadEmail();
            if (credential != null) {
                return createEmailConfig(credential);
            }
        }

        ui.email();
        String email = input.readText();

        CredentialManager.save(email);
        return createEmailConfig(email);
    }

    @SneakyThrows
    private static EmailConfig createEmailConfig(String email) {

        AuthEmail authEmail = new AuthEmail(email);

        return new EmailConfig(email, authEmail);
    }
}