package util;

public class Validator {

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isPhoneValid(String phone) {
        return phone.matches("\\d{9,11}");
    }

    public static boolean isStrongPassword(String password) {
        return password.length() >= 6;
    }
}
