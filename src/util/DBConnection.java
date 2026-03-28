package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/meeting_management?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456789";

    private static boolean isConnectedLogged = false;

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            if (!isConnectedLogged) {
                System.out.println("Kết nối DB thành công");
                isConnectedLogged = true;
            }

            return conn;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi kết nối DB", e);
        }
    }

}



