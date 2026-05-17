package org.example.service.impl;

import jakarta.mail.Store;
import lombok.Data;
import lombok.SneakyThrows;
import org.example.security.HashingPassword;
import org.example.service.AuthEmail;

@Data
public class PasswordAuthEmailImpl implements AuthEmail {

    private final String password;

    @Override
    @SneakyThrows
    public void connectionEmail(Store store, String host, String email) {
        String decrypt = HashingPassword.decrypt(password);
        store.connect(host, email ,decrypt);
    }
}
