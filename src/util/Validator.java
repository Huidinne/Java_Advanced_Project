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

    public static boolean isPositive(int value) {
        return value > 0;
    }

    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    public static boolean isPositive(double value) {
        return value > 0;
    }
}
