package com.typetrack.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for hashing user passwords securely using SHA-256 algorithm.
 */
public class PasswordHasher {

    /**
     * Hashes a raw password string into a 64-character SHA-256 hexadecimal string.
     */
    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password to hash cannot be null.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Critical security error: SHA-256 algorithm not supported by platform.", e);
        }
    }
}
