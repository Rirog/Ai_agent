package org.example.ui;

import org.example.utils.NetworkUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.example.security.OAuthTokenManager;
import org.example.service.AiController;
import org.example.service.EmailParser;
import org.example.service.config.CalendarConfig;
import org.example.service.config.EmailConfig;
import org.example.service.config.TaskConfig;
import org.example.service.impl.ActionExecutorServiceImpl;
import org.example.service.impl.AiServiceImpl;
import org.example.service.impl.AuthEmail;

import java.awt.*;

import java.net.URI;
import java.net.UnknownHostException;


public class AuthWindow {
    private final Stage stage;
    private final OAuthTokenManager tokenManager;

    public AuthWindow() {
        this.stage = new Stage();
        this.tokenManager = new OAuthTokenManager();
    }


    public void show() {
        Label title = label("AI Email Agent", 24, "#EF5765", true);
        Button retryLink = new Button("Закрыли окно авторизации?");
        retryLink.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #EF5765;" +
                        "-fx-font-size: 12px;" +
                        "-fx-underline: true;"
        );
        retryLink.setVisible(false);
        Button btn = button("Войти через Google", "#EF5765");
        ProgressIndicator loader = new ProgressIndicator();
        loader.setVisible(false);

        VBox root = new VBox(20, title, btn, retryLink, loader);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #FFFFFF;");

        btn.setOnAction(e -> runAuth(btn, loader, retryLink));

        retryLink.setCursor(javafx.scene.Cursor.HAND);
        retryLink.setOnAction(e -> {
            try {
                String url = tokenManager.getAuthorizationUrl();
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI.create(url));
                }
            } catch (Exception ignored) {
            }
        });

        stage.setScene(new Scene(root, 380, 260));
        stage.setTitle("Email-agent");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    private void runAuth(Button btn, ProgressIndicator loader, Button retryBtn) {
        if (!NetworkUtils.isOnline()) {
            showAlert("Нет интернета", "Проверьте подключение и попробуйте снова.");
            return;
        }

        btn.setDisable(true);
        loader.setVisible(true);
        retryBtn.setVisible(true);

        Thread thread = new Thread(() -> {
            try {
                AuthEmail authEmail = new AuthEmail();
                String email = authEmail.getTokenManager().getEmailUser();
                EmailConfig emailConfig = new EmailConfig(email, authEmail);

                Platform.runLater(() -> {
                    stage.close();
                    onAuthSuccess(emailConfig, email, authEmail);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    btn.setDisable(false);
                    loader.setVisible(false);

                    if (NetworkUtils.isNetworkError(ex)) {
                        showAlert("Нет интернета", "Проверьте подключение и попробуйте снова.");
                    } else {
                        showAlert(" Ошибка", "Не удалось войти: " + ex.getMessage());
                    }
                });
            }
        }, "OAuth");
        thread.setDaemon(true);
        thread.start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }


    private Label label(String text, double size, String color, boolean bold) {
        var l = new Label(text);
        l.setStyle(
                "-fx-font-size: " + size + "px;" +
                        "-fx-text-fill: " + color + (bold ? ";" +
                        "-fx-font-weight: bold" : "") + ";"
        );
        return l;
    }

    private Button button(String text, String bg) {
        var b = new Button(text);
        b.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 6;"
        );
        b.setCursor(javafx.scene.Cursor.HAND);
        return b;
    }

    @SneakyThrows
    private void onAuthSuccess(EmailConfig emailConfig, String email, AuthEmail authEmail) {
        AiController aiController = new AiController(
                new AiServiceImpl(),
                emailConfig,
                new EmailParser(),
                new ActionExecutorServiceImpl(
                        new CalendarConfig(emailConfig.getAuthEmail().getTokenManager()),
                        new TaskConfig(emailConfig.getAuthEmail().getTokenManager())
                )
        );
        MainWindow mainApp = new MainWindow(stage, emailConfig, email, authEmail.getTokenManager(), aiController);
        mainApp.show();
    }
}