package presentation;

import model.Equipment;
import service.EquipmentService;
import util.InputUtil;

import java.util.List;

public class AdminEquipmentMenu {

    private EquipmentService equipmentService = new EquipmentService();

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ================================
                        QUẢN LÝ THIẾT BỊ DI ĐỘNG
                    ================================
                    | 1 | Thêm thiết bị           |
                    | 2 | Xem danh sách thiết bị  |
                    | 3 | Cập nhật thiết bị       |
                    | 4 | Cập nhật số lượng       |
                    | 5 | Xóa thiết bị            |
                    | 0 | Thoát                   |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> addEquipment();
                case 2 -> viewEquipment();
                case 3 -> updateEquipment();
                case 4 -> updateAvailableQuantity();
                case 5 -> deleteEquipment();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }

    private void addEquipment() {
        String name = InputUtil.inputString("Tên thiết bị: ");
        int totalQuantity = InputUtil.inputInt("Số lượng: ");
        String status = InputUtil.inputString("Trạng thái (AVAILABLE/MAINTENANCE): ");

        if (equipmentService.addEquipment(name, totalQuantity, status)) {
            System.out.println("Thêm thiết bị thành công");
        } else {
            System.out.println("Thêm thiết bị thất bại");
        }
    }

    private void viewEquipment() {
        List<Equipment> list = equipmentService.getAllEquipment();

        if (list.isEmpty()) {
            System.out.println("Không có thiết bị nào");
            return;
        }

        System.out.println("=== DANH SÁCH THIẾT BỊ ===");
        for (Equipment e : list) {
            System.out.printf("ID: %d | Tên: %s | Tổng SL: %d | SL Khả dụng: %d | Trạng thái: %s%n",
                    e.getId(), e.getName(), e.getTotalQuantity(), e.getAvailableQuantity(), e.getStatus());
        }
    }

    private void updateEquipment() {
        int id = InputUtil.inputInt("ID thiết bị: ");
        String name = InputUtil.inputString("Tên mới: ");
        int totalQuantity = InputUtil.inputInt("Số lượng: ");
        String status = InputUtil.inputString("Trạng thái: ");

        if (equipmentService.updateEquipment(id, name, totalQuantity, status)) {
            System.out.println("Cập nhật thành công");
        } else {
            System.out.println("Cập nhật thất bại");
        }
    }

    private void updateAvailableQuantity() {
        int id = InputUtil.inputInt("ID thiết bị: ");
        int availableQuantity = InputUtil.inputInt("Số lượng khả dụng: ");

        if (equipmentService.updateAvailableQuantity(id, availableQuantity)) {
            System.out.println("Cập nhật số lượng thành công");
        } else {
            System.out.println("Cập nhật số lượng thất bại");
        }
    }

    private void deleteEquipment() {
        int id = InputUtil.inputInt("ID thiết bị cần xóa: ");

        if (equipmentService.deleteEquipment(id)) {
            System.out.println("Xóa thiết bị thành công");
        } else {
            System.out.println("Xóa thiết bị thất bại");
        }
    }
}
