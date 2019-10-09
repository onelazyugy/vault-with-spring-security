package com.vietle.vault.util;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class CryptoUtil {
    public static String hash(String secret, String password) {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(secret, 10000, 128);
        return encoder.encode(password);
    }

    public static String encryptString(String textToEncrypt, String credentialEncKey) {
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(credentialEncKey);
        return textEncryptor.encrypt(textToEncrypt);
    }

    public static String decryptString(String textToDecrypt, String credentialEncKey) {
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(credentialEncKey);
        return textEncryptor.decrypt(textToDecrypt);
    }
}
