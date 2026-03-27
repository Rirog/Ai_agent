package org.example.service;

import jakarta.mail.Store;


public interface AuthEmail {
    void connectionEmail(Store store, String host, String email);
}
