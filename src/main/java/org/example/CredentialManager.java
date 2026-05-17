package org.example;

import lombok.SneakyThrows;

import java.io.*;
import java.util.Properties;

public class CredentialManager {
    private static final String CREDENTIALS_FILE = ".credentials";

    @SneakyThrows
    public static boolean exists() {
        return new File(CREDENTIALS_FILE).exists();
    }

    @SneakyThrows
    public static void save(String email) {
        Properties properties = new Properties();
        properties.setProperty("EMAIL", email);

        try (OutputStream out = new FileOutputStream(CREDENTIALS_FILE)) {
            properties.store(out, "OAuth Credentials");
        }
    }

    @SneakyThrows
    public static String loadEmail() {
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(CREDENTIALS_FILE)) {
            properties.load(in);
        }
        return properties.getProperty("EMAIL");
    }
}