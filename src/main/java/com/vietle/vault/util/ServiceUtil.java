package com.vietle.vault.util;

import com.vietle.vault.domain.Credential;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ServiceUtil {
    public static BufferedWriter bufferedWriter(String fileLocation) throws IOException {
        FileWriter fw = new FileWriter(fileLocation);
        return new BufferedWriter(fw);
	}

    public static void hashString(List<Credential> credentials, String secret) {
        credentials.stream().forEach(c -> {
            String hashed = CryptoUtil.hash(secret, c.getPassword());
            c.setPassword(hashed);
        });
    }
}
