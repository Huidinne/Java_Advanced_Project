package presentation;

import model.Room;
import service.RoomService;
import util.InputUtil;

import java.util.List;

public class AdminRoomMenu {

    private RoomService roomService = new RoomService();

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ================================
                            QUẢN LÝ PHÒNG
                    ================================
                    | 1 | Thêm phòng              |
                    | 2 | Xem danh sách phòng     |
                    | 3 | Cập nhật phòng          |
                    | 4 | Xóa phòng               |
                    | 0 | Thoát                   |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> addRoom();
                case 2 -> viewRooms();
                case 3 -> updateRoom();
                case 4 -> deleteRoom();
                case 0 -> { return; }
            }
        }
    }

    private void addRoom() {
        String name = InputUtil.inputString("Tên phòng: ");
        int capacity = InputUtil.inputInt("Sức chứa: ");
        String location = InputUtil.inputString("Vị trí: ");

        if (roomService.addRoom(name, capacity, location)) {
            System.out.println("Thêm thành công");
        }
    }

    private void viewRooms() {
        List<Room> list = roomService.getAllRooms();

        for (Room r : list) {
            System.out.println(r.getId() + " | " + r.getName()
                    + " | " + r.getCapacity()
                    + " | " + r.getLocation());
        }
    }

    private void updateRoom() {
        int id = InputUtil.inputInt("ID phòng: ");
        String name = InputUtil.inputString("Tên mới: ");
        int capacity = InputUtil.inputInt("Sức chứa: ");
        String location = InputUtil.inputString("Vị trí: ");

        if (roomService.updateRoom(id, name, capacity, location)) {
            System.out.println("Update thành công");
        }
    }

    private void deleteRoom() {
        int id = InputUtil.inputInt("ID phòng cần xóa: ");

        if (roomService.deleteRoom(id)) {
            System.out.println("Xóa thành công");
        }
    }
}
