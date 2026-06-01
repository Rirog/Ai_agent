package org.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class ApiKeyDialog {

    public static String showDialog() {

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Ollama API Key");
        stage.setResizable(false);

        final String[] result = new String[1];

        Label title = new Label("API ключ Ollama");
        title.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #EF5765;"
        );

        Label info = new Label("""
                Инструкция:
                    1. Авторизоваться в ollama;
                    2. Открыть настроки(Settings) профиля;
                    3. Окрыть 'Keys';
                    4. Создать 'API Keys';
                    5. Скопировать ключ;
                """);

        info.setWrapText(true);
        info.setStyle("-fx-text-fill: #EF5765;");

        PasswordField field = new PasswordField();
        field.setPromptText("Введите API ключ");

        Button saveBtn = new Button("Сохранить");
        saveBtn.setStyle(
                "-fx-background-color: #EF5765;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 8px 20px;" +
                "-fx-background-radius: 6;"
        );

        Label status = new Label();
        status.setStyle(
                "-fx-text-fill: #e74c3c;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        status.setVisible(false);
        status.setManaged(false);

        saveBtn.setCursor(javafx.scene.Cursor.HAND);
        saveBtn.setOnAction(e -> {
            String key = field.getText().trim();
            if (key.isEmpty()) {
                status.setText("Введите ключ");
                status.setVisible(true);
                return;
            }
            try {
                org.example.security.ApiKeyManager.saveApiKey(key);
                result[0] = key;
                stage.close();
            } catch (Exception ex) {
                status.setText("Ошибка: " + ex.getMessage());
                status.setVisible(true);
            }
        });

        Button backBtn = new Button("Назад");

        backBtn.setOnAction(e -> {
            result[0] = null;
            stage.close();
        });

        Button helpBtn = new Button("Где получить ключ?");
        helpBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #e74c3c;" +
                "-fx-font-size: 11px;" +
                "-fx-padding: 8px 20px;" +
                "-fx-underline: true;"
        );
        helpBtn.setCursor(javafx.scene.Cursor.HAND);
        helpBtn.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(
                        URI.create("https://ollama.com/settings/keys")
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox root = new VBox(15,
                title,
                info,
                field,
                helpBtn,
                saveBtn

        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        stage.setScene(new Scene(root, 420, 400));
        stage.showAndWait();

        return result[0];
    }
}