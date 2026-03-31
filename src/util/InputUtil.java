package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtil {

    private static final Scanner sc = new Scanner(System.in);

    public static int inputInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Vui lòng nhập số!");
            }
        }
    }

    public static String inputString(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    public static double inputDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Double.parseDouble(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Vui lòng nhập số!");
            }
        }
    }

    public static int inputPositiveInt(String message) {
        while (true) {
            int value = inputInt(message);
            if (value > 0) {
                return value;
            }
            System.out.println("Giá trị phải lớn hơn 0");
        }
    }

    public static int inputNonNegativeInt(String message) {
        while (true) {
            int value = inputInt(message);
            if (value >= 0) {
                return value;
            }
            System.out.println("Giá trị không được âm");
        }
    }

    public static boolean inputYesNo(String message) {
        while (true) {
            String value = inputString(message);
            if ("y".equalsIgnoreCase(value)) {
                return true;
            }
            if ("n".equalsIgnoreCase(value)) {
                return false;
            }
            System.out.println("Vui lòng nhập y hoặc n");
        }
    }

    public static LocalDateTime inputDateTime(String message, DateTimeFormatter formatter) {
        while (true) {
            String raw = inputString(message);
            try {
                return LocalDateTime.parse(raw, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Định dạng không hợp lệ. Ví dụ: 2026-03-30 08:00");
            }
        }
    }
}
