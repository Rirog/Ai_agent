package org.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.security.ApiKeyManager;
import org.example.service.AiController;

public class ApiKeySettingsDialog {

    public static void show(Stage owner, AiController aiController) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Настройки API-ключа");
        dialog.setResizable(false);

        Label title = new Label("Ollama API-ключ");
        title.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #EF5765;"
        );

        Label currentKey = new Label("Текущий: " + ApiKeyManager.getMaskedKey());
        currentKey.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #666;"
        );

        PasswordField newKeyField = new PasswordField();
        newKeyField.setPromptText("Введите новый ключ");
        newKeyField.setPrefWidth(280);

        Label status = new Label();
        status.setStyle(
                "-fx-text-fill: #EF5765;" +
                "-fx-font-size: 11px;"
        );
        status.setVisible(false);

        Button saveBtn = new Button("Сохранить");
        saveBtn.setStyle(
                "-fx-background-color: #EF5765;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px 16px;" +
                "-fx-background-radius: 4;"
        );
        saveBtn.setCursor(javafx.scene.Cursor.HAND);

        Button deleteBtn = new Button("Удалить");
        deleteBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px 16px;" +
                "-fx-background-radius: 4;"
        );
        deleteBtn.setCursor(javafx.scene.Cursor.HAND);

        Button cancelBtn = new Button("Отмена");
        cancelBtn.setStyle(
                "-fx-background-color: #ccc;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px 16px;" +
                "-fx-background-radius: 4;"
        );
        cancelBtn.setCursor(javafx.scene.Cursor.HAND);

        HBox actions = new HBox(10, saveBtn, deleteBtn, cancelBtn);
        actions.setAlignment(Pos.CENTER);

        saveBtn.setOnAction(e -> {
            String newKey = newKeyField.getText().trim();
            if (newKey.isEmpty()) {
                showStatus(status, "Введите ключ", true);
                return;
            }
            try {
                ApiKeyManager.changeApiKey(newKey);
                aiController.reloadApiKey();
                showStatus(status, "Ключ обновлён", true);
                currentKey.setText("Текущий: " + ApiKeyManager.getMaskedKey());
                newKeyField.clear();
            } catch (Exception ex) {
                showStatus(status, "Ошибка: " + ex.getMessage(), true);
            }
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Подтверждение");
            confirm.setHeaderText("Удалить API-ключ?");
            confirm.setContentText("После удаления потребуется ввести ключ заново.");
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            if (confirm.showAndWait().filter(ButtonType.YES::equals).isPresent()) {
                ApiKeyManager.clearApiKey();
                currentKey.setText("Текущий: Не задан");
                aiController.reloadApiKey();
                showStatus(status, "Ключ удалён", true);
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        VBox root = new VBox(15, title, currentKey, newKeyField, status, actions);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #FFFFFF;");

        dialog.setScene(new Scene(root, 360, 240));
        dialog.centerOnScreen();
        dialog.showAndWait();
    }

    private static void showStatus(Label label, String text, boolean visible) {
        label.setText(text);
        label.setVisible(visible);
    }
}