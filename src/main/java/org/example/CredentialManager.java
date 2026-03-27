package org.example;

import lombok.SneakyThrows;
import org.example.security.HashingPassword;

import java.io.*;
import java.util.Properties;

public class CredentialManager {
    private static final String file = ".env";

    @SneakyThrows
    public static boolean exists() {
        return new File(file).exists();
    }

    @SneakyThrows
    public static void save(String email, String password) {
        String encrypted = HashingPassword.encrypt(password);
        Properties properties = new Properties();

        properties.setProperty("EMAIL", email);
        properties.setProperty("PASSWORD", encrypted);

        try (OutputStream out = new FileOutputStream(file)) {
            properties.store(out, "Credentials");
        }
    }


    @SneakyThrows
    public static String[] load(){
        Properties properties = new Properties();

        try (InputStream in = new FileInputStream(file)) {
            properties.load(in);
        }

        String email = properties.getProperty("EMAIL");
        String encryptedPassword = properties.getProperty("PASSWORD");

        if (email == null || encryptedPassword == null) {
            return null;
        }

        return new String[]{email, encryptedPassword};
    }
}
