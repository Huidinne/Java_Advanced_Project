package presentation;

import model.Service;
import service.ServiceService;
import util.InputUtil;
import util.Validator;

import java.util.List;

public class AdminServiceMenu {

    private final ServiceService serviceService = new ServiceService();

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ================================
                          QUẢN LÍ DỊCH VỤ ĐI KÈM
                    ================================
                    | 1 | Xem danh sách dịch vụ    |
                    | 2 | Thêm dịch vụ             |
                    | 3 | Sửa dịch vụ              |
                    | 4 | Xóa dịch vụ              |
                    | 0 | Thoát                    |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> viewServicesTable();
                case 2 -> addServiceFlow();
                case 3 -> updateServiceFlow();
                case 4 -> deleteServiceFlow();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ");
            }
        }
    }

    private void viewServicesTable() {
        List<Service> services = serviceService.getAllServices();
        if (services.isEmpty()) {
            System.out.println("Không có dịch vụ nào");
            return;
        }

        System.out.println("======================================================");
        System.out.println("ID |          Tên dịch vụ         |         Giá (VND) ");
        System.out.println("------------------------------------------------------");
        for (Service s : services) {
            System.out.printf("%-2d | %-27s | %,.0f%n", s.getId(), s.getName(), s.getPrice());
        }
        System.out.println("======================================================");
    }

    private void addServiceFlow() {
        String name = inputServiceName("Tên dịch vụ: ");
        double price = inputPositivePrice("Giá dịch vụ: ");

        System.out.printf("Xác định thêm dịch vụ '%s' với giá %,.0f? (y/n): ", name, price);
        if (!confirmAction()) {
            System.out.println("Đã hủy thao tác");
            return;
        }

        try {
            if (serviceService.addService(name, price)) {
                System.out.println("Thêm dịch vụ thành công");
            } else {
                System.out.println("Thêm dịch vụ thất bại");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Lỗi hệ thống khi thêm dịch vụ: " + e.getMessage());
        }
    }

    private void updateServiceFlow() {
        int id = InputUtil.inputInt("Nhập ID dịch vụ cần sửa: ");
        Service current = serviceService.getServiceById(id);
        if (current == null) {
            System.out.println("Dịch vụ không tồn tại");
            return;
        }

        String name = inputServiceName("Tên dịch vụ mới: ");
        double price = inputPositivePrice("Giá dịch vụ mới: ");

        System.out.printf("Xác nhận sửa dịch vụ ID %d thành '%s' - %,.0f? (y/n): ", id, name, price);
        if (!confirmAction()) {
            System.out.println("Đã hủy thao tác");
            return;
        }

        try {
            if (serviceService.updateService(id, name, price)) {
                System.out.println("Cập nhật dịch vụ thành công");
            } else {
                System.out.println("Cập nhật dịch vụ thất bại");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Lỗi hệ thống khi cập nhật dịch vụ: " + e.getMessage());
        }
    }

    private void deleteServiceFlow() {
        int id = InputUtil.inputInt("Nhập ID dịch vụ muốn xóa: ");
        Service current = serviceService.getServiceById(id);
        if (current == null) {
            System.out.println("Dịch vụ không tồn tại");
            return;
        }

        System.out.printf("Xác nhận xóa dịch vụ '%s' (ID %d)? (y/n): ", current.getName(), current.getId());
        if (!confirmAction()) {
            System.out.println("Đã hủy thao tác");
            return;
        }

        try {
            if (serviceService.deleteService(id)) {
                System.out.println("Xóa dịch vụ thành công");
            } else {
                System.out.println("Xóa dịch vụ thất bại");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Không thể xóa dịch vụ. Có thể đang được sử dụng.");
        }
    }

    private String inputServiceName(String message) {
        while (true) {
            String name = InputUtil.inputString(message);
            if (Validator.isBlank(name)) {
                System.out.println("Tên dịch vụ không được để trống");
            } else {
                return name.trim();
            }
        }
    }

    private double inputPositivePrice(String message) {
        while (true) {
            double price = InputUtil.inputDouble(message);
            if (price <= 0) {
                System.out.println("Giá dịch vụ phải lớn hơn 0");
            } else {
                return price;
            }
        }
    }

    private boolean confirmAction() {
        while (true) {
            String confirm = InputUtil.inputString("");
            if ("y".equalsIgnoreCase(confirm)) {
                return true;
            }
            if ("n".equalsIgnoreCase(confirm)) {
                return false;
            }
            System.out.print("Vui lòng nhập y hoặc n: ");
        }
    }
}

