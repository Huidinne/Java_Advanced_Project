package presentation;
import util.DBConnection;
public class Main {
    public static void main(String[] args) {
        DBConnection.getConnection();
        AuthMenu authMenu = new AuthMenu();
        authMenu.show();
    }
}