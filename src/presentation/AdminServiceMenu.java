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
                        QUAN LY DICH VU DI KEM
                    ================================
                    | 1 | Xem danh sach dich vu    |
                    | 2 | Them dich vu             |
                    | 3 | Sua dich vu              |
                    | 4 | Xoa dich vu              |
                    | 0 | Thoat                     |
                    ================================
                    Chon: """);

            switch (choice) {
                case 1 -> viewServicesTable();
                case 2 -> addServiceFlow();
                case 3 -> updateServiceFlow();
                case 4 -> deleteServiceFlow();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Lua chon khong hop le");
            }
        }
    }

    private void viewServicesTable() {
        List<Service> services = serviceService.getAllServices();
        if (services.isEmpty()) {
            System.out.println("Khong co dich vu nao");
            return;
        }

        System.out.println("====================================================");
        System.out.println("ID | Ten dich vu                  | Gia");
        System.out.println("----------------------------------------------------");
        for (Service s : services) {
            System.out.printf("%-2d | %-27s | %,.0f%n", s.getId(), s.getName(), s.getPrice());
        }
        System.out.println("====================================================");
    }

    private void addServiceFlow() {
        String name = inputServiceName("Ten dich vu: ");
        double price = inputPositivePrice("Gia dich vu: ");

        System.out.printf("Xac nhan them dich vu '%s' voi gia %,.0f? (y/n): ", name, price);
        if (!confirmAction()) {
            System.out.println("Da huy thao tac");
            return;
        }

        try {
            if (serviceService.addService(name, price)) {
                System.out.println("Them dich vu thanh cong");
            } else {
                System.out.println("Them dich vu that bai");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Du lieu khong hop le: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Loi he thong khi them dich vu: " + e.getMessage());
        }
    }

    private void updateServiceFlow() {
        int id = InputUtil.inputInt("Nhap ID dich vu can sua: ");
        Service current = serviceService.getServiceById(id);
        if (current == null) {
            System.out.println("Dich vu khong ton tai");
            return;
        }

        String name = inputServiceName("Ten dich vu moi: ");
        double price = inputPositivePrice("Gia dich vu moi: ");

        System.out.printf("Xac nhan sua dich vu ID %d thanh '%s' - %,.0f? (y/n): ", id, name, price);
        if (!confirmAction()) {
            System.out.println("Da huy thao tac");
            return;
        }

        try {
            if (serviceService.updateService(id, name, price)) {
                System.out.println("Cap nhat dich vu thanh cong");
            } else {
                System.out.println("Cap nhat dich vu that bai");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Du lieu khong hop le: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Loi he thong khi cap nhat dich vu: " + e.getMessage());
        }
    }

    private void deleteServiceFlow() {
        int id = InputUtil.inputInt("Nhap ID dich vu can xoa: ");
        Service current = serviceService.getServiceById(id);
        if (current == null) {
            System.out.println("Dich vu khong ton tai");
            return;
        }

        System.out.printf("Xac nhan xoa dich vu '%s' (ID %d)? (y/n): ", current.getName(), current.getId());
        if (!confirmAction()) {
            System.out.println("Da huy thao tac");
            return;
        }

        try {
            if (serviceService.deleteService(id)) {
                System.out.println("Xoa dich vu thanh cong");
            } else {
                System.out.println("Xoa dich vu that bai");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Du lieu khong hop le: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Khong the xoa dich vu. Co the dang duoc su dung trong booking.");
        }
    }

    private String inputServiceName(String message) {
        while (true) {
            String name = InputUtil.inputString(message);
            if (Validator.isBlank(name)) {
                System.out.println("Ten dich vu khong duoc de trong");
            } else {
                return name.trim();
            }
        }
    }

    private double inputPositivePrice(String message) {
        while (true) {
            double price = InputUtil.inputDouble(message);
            if (price <= 0) {
                System.out.println("Gia dich vu phai lon hon 0");
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
            System.out.print("Vui long nhap y hoac n: ");
        }
    }
}

