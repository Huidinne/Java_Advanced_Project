package presentation;
import util.DBConnection;

public class Main {
    public static void main(String[] args) {
        try {
            DBConnection.getConnection();
            AuthMenu authMenu = new AuthMenu();
            authMenu.show();
        } catch (Exception e) {
            System.out.println("Đã xảy ra lỗi hệ thống. Vui lòng thử lại hoặc kiểm tra kết nối CSDL.");
        }
    }
}