package presentation;

import model.*;
import service.BookingService;
import service.EquipmentService;
import service.ServiceService;
import util.InputUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeMenu {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final BookingService bookingService = new BookingService();
    private final EquipmentService equipmentService = new EquipmentService();
    private final ServiceService serviceService = new ServiceService();
    private final User currentUser;

    public EmployeeMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ================================
                        MENU NHÂN VIÊN
                    ================================
                    | 1 | Đặt phòng & yêu cầu DV   |
                    | 2 | Xem lịch họp của tôi     |
                    | 3 | Hủy booking PENDING     |
                    | 0 | Đăng xuất                |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> bookRoom();
                case 2 -> viewBookingHistory();
                case 3 -> cancelPendingBooking();
                case 0 -> {
                    System.out.println("Đã đăng xuất");
                    return;
                }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }

    private void bookRoom() {
        try {
            LocalDateTime startTime = inputDateTime("Thời gian bắt đầu (yyyy-MM-dd HH:mm): ");
            LocalDateTime endTime = inputDateTime("Thời gian kết thúc (yyyy-MM-dd HH:mm): ");

            List<Room> availableRooms = bookingService.getAvailableRooms(startTime, endTime);
            if (availableRooms.isEmpty()) {
                System.out.println("Không có phòng trống trong khung giờ đã chọn");
                return;
            }

            printAvailableRooms(availableRooms);
            int roomId = InputUtil.inputInt("Nhập ID phòng muốn đặt: ");
            if (!containsRoomId(availableRooms, roomId)) {
                System.out.println("ID phòng không nằm trong danh sách phòng trống");
                return;
            }

            int attendeeCount = inputPositiveInt("Số người tham gia: ");

            List<BookingDetail> requestDetails = new ArrayList<>();
            requestDetails.addAll(inputEquipmentDetails());
            requestDetails.addAll(inputServiceDetails());
            boolean success = bookingService.createPendingBooking(
                    currentUser.getId(),
                    roomId,
                    startTime,
                    endTime,
                    attendeeCount,
                    requestDetails
            );

            if (success) {
                System.out.println("Đặt phòng thành công. Trạng thái: PENDING");
            } else {
                System.out.println("Đặt phòng thất bại do xung đột thời gian");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi dữ liệu: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Có lỗi khi tạo booking: " + e.getMessage());
        }
    }

    private void viewBookingHistory() {
        List<Booking> list = bookingService.getBookingHistoryByUser(currentUser.getId());
        if (list.isEmpty()) {
            System.out.println("Bạn chưa có booking nào");
            return;
        }

        System.out.println("============================================================================================================");
        System.out.println("ID | Room ID | Start Time         | End Time           | Trạng thái duyệt | Chuẩn bị");
        System.out.println("------------------------------------------------------------------------------------------------------------");
        for (Booking b : list) {
            String preparation = bookingService.getPreparationSummary(b.getId());
            System.out.printf("%-2d | %-7d | %-18s | %-18s | %-16s | %s%n",
                    b.getId(),
                    b.getRoomId(),
                    b.getStartTime().format(DATE_TIME_FORMATTER),
                    b.getEndTime().format(DATE_TIME_FORMATTER),
                    b.getStatus().name(),
                    preparation);
        }
        System.out.println("============================================================================================================");
    }

    private void cancelPendingBooking() {
        int bookingId = InputUtil.inputInt("Nhập ID booking cần hủy: ");
        System.out.print("Xác nhận hủy booking này? (y/n): ");
        if (!confirmYesNo()) {
            System.out.println("Đã hủy thao tác");
            return;
        }

        try {
            boolean success = bookingService.cancelPendingBooking(currentUser.getId(), bookingId);
            if (success) {
                System.out.println("Hủy booking thành công");
            } else {
                System.out.println("Không thể hủy. Chỉ được hủy booking của bạn khi đang PENDING");
            }
        } catch (RuntimeException e) {
            System.out.println("Có lỗi khi hủy booking: " + e.getMessage());
        }
    }

    private LocalDateTime inputDateTime(String message) {
        while (true) {
            String raw = InputUtil.inputString(message);
            try {
                return LocalDateTime.parse(raw, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Định dạng không hợp lệ. Ví dụ: 2026-03-30 08:00");
            }
        }
    }

    private void printAvailableRooms(List<Room> rooms) {
        System.out.println("==============================================================");
        System.out.println("ID | Tên phòng        | Sức chứa | Vị trí        | Trạng thái");
        System.out.println("--------------------------------------------------------------");
        for (Room r : rooms) {
            System.out.printf("%-2d | %-15s | %-8d | %-12s | %-10s%n",
                    r.getId(), r.getName(), r.getCapacity(), r.getLocation(), r.getStatus().name());
        }
        System.out.println("==============================================================");
    }

    private List<BookingDetail> inputEquipmentDetails() {
        List<BookingDetail> details = new ArrayList<>();
        String answer = InputUtil.inputString("Bạn có muốn mượn thêm thiết bị? (y/n): ");
        if (!"y".equalsIgnoreCase(answer)) {
            return details;
        }

        printEquipmentList();
        while (true) {
            int equipmentId = InputUtil.inputInt("Nhập ID thiết bị (0 để kết thúc): ");
            if (equipmentId == 0) {
                break;
            }

            int quantity = InputUtil.inputInt("Số lượng: ");
            BookingDetail detail = new BookingDetail();
            detail.setType(DetailType.EQUIPMENT);
            detail.setRefId(equipmentId);
            detail.setQuantity(quantity);
            details.add(detail);
        }

        return details;
    }

    private List<BookingDetail> inputServiceDetails() {
        List<BookingDetail> details = new ArrayList<>();
        String answer = InputUtil.inputString("Bạn có muốn yêu cầu thêm dịch vụ? (y/n): ");
        if (!"y".equalsIgnoreCase(answer)) {
            return details;
        }

        printServiceList();
        while (true) {
            int serviceId = InputUtil.inputInt("Nhập ID dịch vụ (0 để kết thúc): ");
            if (serviceId == 0) {
                break;
            }

            int quantity = inputPositiveInt("Số lượng dịch vụ: ");
            BookingDetail detail = new BookingDetail();
            detail.setType(DetailType.SERVICE);
            detail.setRefId(serviceId);
            detail.setQuantity(quantity);
            details.add(detail);
        }

        return details;
    }

    private void printEquipmentList() {
        List<Equipment> equipments = equipmentService.getAllEquipment();
        if (equipments.isEmpty()) {
            System.out.println("Không có thiết bị để mượn thêm");
            return;
        }

        System.out.println("============================================================");
        System.out.println("ID | Tên thiết bị      | Khả dụng | Trạng thái");
        System.out.println("------------------------------------------------------------");
        for (Equipment e : equipments) {
            System.out.printf("%-2d | %-15s | %-8d | %-12s%n",
                    e.getId(), e.getName(), e.getAvailableQuantity(), e.getStatus());
        }
        System.out.println("============================================================");
    }

    private void printServiceList() {
        List<model.Service> services = serviceService.getAllServices();
        if (services.isEmpty()) {
            System.out.println("Không có dịch vụ nào để yêu cầu thêm");
            return;
        }

        System.out.println("============================================================");
        System.out.println("ID | Tên dịch vụ       | Giá");
        System.out.println("------------------------------------------------------------");
        for (model.Service s : services) {
            System.out.printf("%-2d | %-15s | %,.0f%n", s.getId(), s.getName(), s.getPrice());
        }
        System.out.println("============================================================");
    }

    private int inputPositiveInt(String message) {
        while (true) {
            int value = InputUtil.inputInt(message);
            if (value > 0) {
                return value;
            }
            System.out.println("Giá trị phải lớn hơn 0");
        }
    }

    private boolean containsRoomId(List<Room> rooms, int roomId) {
        for (Room room : rooms) {
            if (room.getId() == roomId) {
                return true;
            }
        }
        return false;
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
