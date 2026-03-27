package org.example;

import lombok.SneakyThrows;
import org.example.service.*;
import org.example.service.impl.ActionExecutorServiceImpl;
import org.example.service.impl.AiServiceImpl;
import org.example.service.impl.PasswordAuthEmailImpl;
import org.example.service.impl.PasswordSmtpEmailImpl;
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
    @SneakyThrows
    private static EmailConfig credentialUser(UserInterface ui, Input input) {
        if (CredentialManager.exists()) {
            String[] creds = CredentialManager.load();
            if (creds != null) {
                return createEmailConfig(creds[0], creds[1]);
            }
        }

        ui.email();
        String email = input.readText();
        ui.appPassword();
        String password = input.readText();

        CredentialManager.save(email, password);
        return createEmailConfig(email, password);
    }
    @SneakyThrows
    private static EmailConfig createEmailConfig(String email, String password) {

        AuthEmail authEmail = new PasswordAuthEmailImpl(password);
        SmtpEmail smtpEmail = new PasswordSmtpEmailImpl(password);

        return new EmailConfig(email, authEmail, smtpEmail);
    }
}