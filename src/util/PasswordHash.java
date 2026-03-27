package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHash {

    private PasswordHash() {}

    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Mậu khẩu không được để trống");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return false;
        }
        if (hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
