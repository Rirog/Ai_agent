package org.example.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.exception.InvalidApiKeyException;
import org.example.security.ApiKeyManager;
import org.example.security.OAuthTokenManager;
import org.example.service.AiController;
import org.example.service.config.EmailConfig;
import org.example.utils.NetworkUtils;

public class MainWindow {
    private final Stage stage;
    private final EmailConfig emailConfig;
    private final String email;
    private final OAuthTokenManager tokenManager;


    private final AiController aiController;

    private ToggleButton powerBtn;
    private volatile boolean running = false;
    private Thread worker;

    public MainWindow(
            Stage parent,
            EmailConfig emailConfig,
            String email,
            OAuthTokenManager tokenManager,
            AiController aiController
    ) {
        this.stage = parent;
        this.emailConfig = emailConfig;
        this.email = email;
        this.aiController = aiController;
        this.tokenManager = tokenManager;
    }

    public void show() {
        var emailLbl = new Label(" " + email);
        emailLbl.setStyle(
                "-fx-font-size: 13px; " +
                "-fx-text-fill: #EF5765;"
        );

        Button settingsBtn = new Button("⚙");
        settingsBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-background-radius: 40%;"

        );
        settingsBtn.setCursor(javafx.scene.Cursor.HAND);
        settingsBtn.setOnAction(e -> {
            if (running) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Настройки заблокированы");
                alert.setHeaderText(null);
                alert.setContentText("Нельзя изменить API-ключ, пока агент работает.\nСначала остановите агента.");
                alert.getDialogPane().setStyle(
                        "-fx-background-color: #f8f9fa;" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: #e74c3c;"
                );
                alert.showAndWait();
                return;
            }
            ApiKeySettingsDialog.show(stage, aiController);
        });

        var logoutBtn = new Button("Выйти из аккаунта");
        logoutBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #e74c3c;" +
                "-fx-font-size: 12px;" +
                "-fx-underline: true;" +
                "-fx-padding: 0 0 0 30"
        );
        logoutBtn.setCursor(javafx.scene.Cursor.HAND);
        logoutBtn.setOnAction(e -> {
            if (running) {
                stopWorker();
            }
            logout();
        });

        HBox header = new HBox(10, settingsBtn, emailLbl, new Region(), logoutBtn);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 0, 20));

        powerBtn = new ToggleButton();
        powerBtn.setPrefSize(200, 200);
        powerBtn.setCursor(javafx.scene.Cursor.HAND);
        updateStyle(false);
        powerBtn.setOnAction(e -> toggle());

        var center = new StackPane(powerBtn);
        VBox.setVgrow(center, Priority.ALWAYS);

        var root = new VBox(header, center);
        root.setStyle("-fx-background-color: #FFFFFF;");

        stage.setScene(new Scene(root, 380, 400));
        stage.setTitle("AI Agent");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    private void toggle() {

        if (!running) {

            if (!NetworkUtils.isOnline()) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Нет интернета",
                        "Агент не запустится без подключения."
                );
                return;
            }

            if (!ApiKeyManager.hasApiKey()) {

                String apiKey = ApiKeyDialog.showDialog();

                if (apiKey == null || apiKey.isBlank()) {
                    powerBtn.setSelected(false);
                    updateStyle(false);
                    return;
                }

                ApiKeyManager.saveApiKey(apiKey);

                aiController.reloadApiKey();
            }
        }

        running = !running;
        updateStyle(running);

        if (running) {
            startWorker();
        } else {
            stopWorker();
        }
    }

    private void updateStyle(boolean on) {
        powerBtn.setStyle(
                "-fx-background-color: " + (on ? "#EF5765" : "#42AAFF") + "; " +
                "-fx-background-radius: 80%;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: 700;"
        );
        powerBtn.setText((on ? "ВКЛ" : "ВЫКЛ"));
    }

    private void startWorker() {
        worker = new Thread(() -> {
            while (running) {
                try {
                    if (!NetworkUtils.isOnline()) {
                        Platform.runLater(() ->
                                showAlert(
                                        Alert.AlertType.WARNING,
                                        "Нет интернета",
                                        "Проверьте подключение."
                                ));
                        break;
                    }
                    aiController.workAi();
                    Thread.sleep(60000);
                } catch (InterruptedException e) {

                    break;
                } catch (InvalidApiKeyException ex) {

                        running = false;

                        Platform.runLater(() -> {

                            updateStyle(false);

                            showAlert(
                                    Alert.AlertType.ERROR,
                                    "Ошибка API ключа",
                                    "Введите корректный API ключ."
                            );

                            String newKey = ApiKeyDialog.showDialog();

                            if (newKey != null && !newKey.isBlank()) {
                                ApiKeyManager.changeApiKey(newKey);
                                aiController.reloadApiKey();
                            }
                        });
                        break;
                    } catch (Exception ex) {
                    System.err.println("Ошибка: " + ex.getMessage());
                }
            }
            if (running) {
                running = false;
                Platform.runLater(() -> updateStyle(false));
            }
        }, "AgentWorker");
        worker.setDaemon(true);
        worker.start();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }

    private void stopWorker() {
        running = false;
        if (worker != null) worker.interrupt();
    }

    private void logout() {
        stopWorker();
        try {
            emailConfig.close();
        } catch (Exception ignored) {
        }

        tokenManager.clearTokens();

        Platform.runLater(() -> {
            stage.close();
            new AuthWindow().show();
        });
    }
}