package org.example.security;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HashingPassword {
    private static final String KEY = "1234567890123456";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final byte[] IV = new byte[16];

    @SneakyThrows
    public static String encrypt(String data) {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), KEY_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    @SneakyThrows
    public static String decrypt(String encrypted) {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), KEY_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
    }
}