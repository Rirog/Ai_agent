package org.example.security;

import lombok.SneakyThrows;
import org.example.utils.CryptoUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class ApiKeyManager {

    private static final Path FILE = Path.of(
            System.getProperty("user.home"),
            ".ai_agent",
            "api.key"
    );

    @SneakyThrows
    public static void saveApiKey(String apiKey) {

        Files.createDirectories(FILE.getParent());

        Files.writeString(
                FILE,
                CryptoUtils.encrypt(apiKey)
        );
    }
    @SneakyThrows
    public static void changeApiKey(String newKey) {
        clearApiKey();
        saveApiKey(newKey);
    }
    public static String getMaskedKey() {
        if (!hasApiKey()) return "Не задан";
        String key = loadApiKey();
        if (key == null || key.isEmpty()) return "Не задан";
        if (key.length() <= 8) return "••••••••";
        return "••••" + key.substring(key.length() - 4);
    }
    @SneakyThrows
    public static String loadApiKey() {

        if (!Files.exists(FILE)) {
            return null;
        }

        String encrypted = Files.readString(FILE);

        return CryptoUtils.decrypt(encrypted);
    }

    public static boolean hasApiKey() {
        return Files.exists(FILE);
    }

    public static void clearApiKey() {
        try {
            Files.deleteIfExists(FILE);
        } catch (Exception ignored) {}
    }
}