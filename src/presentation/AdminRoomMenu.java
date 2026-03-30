package presentation;

import model.Room;
import model.RoomStatus;
import service.RoomService;
import util.InputUtil;
import util.Validator;

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
                default -> System.out.println("Lựa chọn không hợp lệ");
            }
        }
    }

    private String inputName() {
        while (true) {
            String name = InputUtil.inputString("Name: ");
            if (Validator.isBlank(name)) {
                System.out.println("Name không được để trống");
            } else {
                return name;
            }
        }
    }
    private String inputLocation() {
        while (true) {
            String location = InputUtil.inputString("Location: ");
            if (Validator.isBlank(location)) {
                System.out.println("Location không được để trống");
            } else {
                return location;
            }
        }
    }
    private void addRoom() {
        String name = inputName();
        int capacity = InputUtil.inputInt("Sức chứa: ");
        String location = inputLocation();
        RoomStatus status = RoomStatus.AVAILABLE;

        if (roomService.addRoom(name, capacity, location, status)) {
            System.out.println("Thêm phòng thành công");
        }
    }

    private void viewRooms() {
        List<Room> list = roomService.getAllRooms();
        if (list.isEmpty()) {
            System.out.println("Không có phòng nào");
            return;
        }

        System.out.println("============================================================");
        System.out.println("ID |    Tên phòng    | Sức chứa |    Vị trí    | Trạng thái|");
        System.out.println("------------------------------------------------------------");
        for (Room r : list) {
            System.out.printf("%-2d | %-15s | %-8d | %-12s | %-10s |\n",
                    r.getId(), r.getName(), r.getCapacity(), r.getLocation(), r.getStatus().toString());
        }
        System.out.println("============================================================");
    }

    private void updateRoom() {
        int choice = InputUtil.inputInt("""
                ================================
                     CẬP NHẬT PHÒNG
                ================================
                | 1 | Cập nhật thông tin phòng |
                | 2 | Cập nhật trạng thái      |
                | 0 | Quay lại                 |
                ================================
                Chọn: """);

        switch (choice) {
            case 1 -> updateRoomInfo();
            case 2 -> updateRoomStatus();
            case 0 -> { return; }
            default -> System.out.println("Lựa chọn không hợp lệ");
        }
    }

    private void updateRoomInfo() {
        int id = InputUtil.inputInt("ID phòng cần cập nhật: ");
        if (roomService.getRoomById(id) == null) {
            System.out.println("Phòng không tồn tại");
            return;
        }
        String name = InputUtil.inputString("Tên mới: ");
        int capacity = InputUtil.inputInt("Sức chứa: ");
        String location = InputUtil.inputString("Vị trí: ");

        if (roomService.updateRoom(id, name, capacity, location)) {
            System.out.println("Cập nhật thông tin thành công");
        } else {
            System.out.println("Phòng không tồn tại");
        }
    }

    private void updateRoomStatus() {
        int id = InputUtil.inputInt("Nhập ID phòng cần cập nhật trạng thái: ");
        if (roomService.getRoomById(id) == null) {
            System.out.println("Phòng không tồn tại");
            return;
        }
        int statusChoice = InputUtil.inputInt("""
                =================================
                    CẬP NHẬT TRẠNG THÁI PHÒNG
                =================================
                | 1 | AVAILABLE                 |
                | 2 | IN_USE                    |
                | 3 | MAINTENANCE               |
                =================================
                Chọn: """);

        RoomStatus status;
        switch(statusChoice) {
            case 1 -> status = RoomStatus.AVAILABLE;
            case 2 -> status = RoomStatus.IN_USE;
            case 3 -> status = RoomStatus.MAINTENANCE;
            default -> {
                System.out.println("Lựa chọn không hợp lệ");
                return;
            }
        }

        if(roomService.updateRoomStatus(id, status)) {
            System.out.println("Cập nhật trạng thái thành công");
        } else {
            System.out.println("Phòng không tồn tại");
        }
    }

    private void deleteRoom() {
        int id = InputUtil.inputInt("ID phòng cần xóa: ");

        System.out.print("Xác nhận xóa phòng ID " + id + "? (y/n): ");
        if (!confirmYesNo()) {
            System.out.println("Đã hủy thao tác xóa phòng");
            return;
        }

        if (roomService.deleteRoom(id)) {
            System.out.println("Xóa phòng thành công");
        } else {
            System.out.println("Phòng không tồn tại");
        }
    }

    private boolean confirmYesNo() {
        while (true) {
            String input = InputUtil.inputString("");
            if ("y".equalsIgnoreCase(input)) {
                return true;
            }
            if ("n".equalsIgnoreCase(input)) {
                return false;
            }
            System.out.print("Vui lòng nhập y hoặc n: ");
        }
    }
}
