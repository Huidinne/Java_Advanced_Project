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
        try {
            String name = InputUtil.inputString("Tên thiết bị: ");
            int totalQuantity = InputUtil.inputPositiveInt("Số lượng: ");
            String status = InputUtil.inputString("Trạng thái (AVAILABLE/MAINTENANCE/BROKEN): ");

            if (equipmentService.addEquipment(name, totalQuantity, status)) {
                System.out.println("Thêm thiết bị thành công");
            } else {
                System.out.println("Thêm thiết bị thất bại");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Lỗi hệ thống khi thêm thiết bị: " + e.getMessage());
        }
    }

    private void viewEquipment() {
        List<Equipment> list = equipmentService.getAllEquipment();

        if (list.isEmpty()) {
            System.out.println("Không có thiết bị nào");
            return;
        }

        System.out.println("============================================================================");
        System.out.println("ID | Tên thiết bị      | Tổng số lượng | Khả dụng | Trạng thái");
        System.out.println("----------------------------------------------------------------------------");
        for (Equipment e : list) {
            System.out.printf("%-2d | %-15s | %-12d | %-8d | %-12s%n",
                    e.getId(), e.getName(), e.getTotalQuantity(), e.getAvailableQuantity(), e.getStatus());
        }
        System.out.println("============================================================================");
    }

    private void updateEquipment() {
        try {
            int id = InputUtil.inputPositiveInt("ID thiết bị: ");
            String name = InputUtil.inputString("Tên mới: ");
            int totalQuantity = InputUtil.inputPositiveInt("Số lượng: ");
            String status = InputUtil.inputString("Trạng thái: ");

            if (equipmentService.updateEquipment(id, name, totalQuantity, status)) {
                System.out.println("Cập nhật thành công");
            } else {
                System.out.println("Cập nhật thất bại");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Lỗi hệ thống khi cập nhật thiết bị: " + e.getMessage());
        }
    }

    private void updateAvailableQuantity() {
        try {
            int id = InputUtil.inputPositiveInt("ID thiết bị: ");
            int availableQuantity = InputUtil.inputNonNegativeInt("Số lượng khả dụng: ");

            if (equipmentService.updateAvailableQuantity(id, availableQuantity)) {
                System.out.println("Cập nhật số lượng thành công");
            } else {
                System.out.println("Cập nhật số lượng thất bại");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Lỗi hệ thống khi cập nhật số lượng: " + e.getMessage());
        }
    }

    private void deleteEquipment() {
        int id = InputUtil.inputPositiveInt("ID thiết bị cần xóa: ");

        if (!InputUtil.inputYesNo("Xác nhận xóa thiết bị ID " + id + "? (y/n): ")) {
            System.out.println("Đã hủy thao tác xóa thiết bị");
            return;
        }

        try {
            if (equipmentService.deleteEquipment(id)) {
                System.out.println("Xóa thiết bị thành công");
            } else {
                System.out.println("Xóa thiết bị thất bại");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Không thể xóa thiết bị. Có thể đang được sử dụng.");
        }
    }
}
