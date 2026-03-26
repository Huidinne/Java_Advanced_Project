package util;

import java.security.MessageDigest;

public class PasswordHash {

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password");
        }
    }

    public static boolean verify(String inputPassword, String storedHash) {
        String inputHash = hash(inputPassword);
        return inputHash.equals(storedHash);
    }
}
