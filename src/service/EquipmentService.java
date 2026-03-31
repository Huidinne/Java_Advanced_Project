package service;

import dao.EquipmentDAO;
import model.Equipment;
import model.EquipmentStatus;
import util.Validator;

import java.util.List;

public class EquipmentService {

    private EquipmentDAO equipmentDAO = new EquipmentDAO();

    public boolean addEquipment(String name, int totalQuantity, String status) {
        validateEquipmentInput(name, totalQuantity, status);

        Equipment equipment = new Equipment();
        equipment.setName(name.trim());
        equipment.setTotalQuantity(totalQuantity);
        equipment.setAvailableQuantity(totalQuantity);
        equipment.setStatus(normalizeStatus(status));

        return equipmentDAO.insert(equipment);
    }

    public List<Equipment> getAllEquipment() {
        return equipmentDAO.findAll();
    }

    public Equipment getEquipmentById(int id) {
        return equipmentDAO.findById(id);
    }

    public boolean updateEquipment(int id, String name, int totalQuantity, String status) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID thiết bị không hợp lệ");
        }
        if (equipmentDAO.findById(id) == null) {
            return false;
        }
        validateEquipmentInput(name, totalQuantity, status);

        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setName(name.trim());
        equipment.setTotalQuantity(totalQuantity);
        equipment.setAvailableQuantity(totalQuantity);
        equipment.setStatus(normalizeStatus(status));

        return equipmentDAO.update(equipment);
    }

    public boolean updateAvailableQuantity(int id, int availableQuantity) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID thiết bị không hợp lệ");
        }
        Equipment equipment = equipmentDAO.findById(id);

        if (equipment == null) {
            System.out.println("Thiết bị không tồn tại");
            return false;
        }

        if (availableQuantity > equipment.getTotalQuantity()) {
            System.out.println("Số lượng khả dụng không được vượt quá tổng số lượng");
            return false;
        }

        if (!Validator.isNonNegative(availableQuantity)) {
            System.out.println("Số lượng không được âm");
            return false;
        }

        return equipmentDAO.updateAvailableQuantity(id, availableQuantity);
    }

    public boolean deleteEquipment(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID thiết bị không hợp lệ");
        }
        return equipmentDAO.delete(id);
    }

    private void validateEquipmentInput(String name, int totalQuantity, String status) {
        if (Validator.isBlank(name)) {
            throw new IllegalArgumentException("Tên thiết bị không được để trống");
        }
        if (!Validator.isPositive(totalQuantity)) {
            throw new IllegalArgumentException("Số lượng thiết bị phải lớn hơn 0");
        }
        normalizeStatus(status);
    }

    private String normalizeStatus(String status) {
        if (Validator.isBlank(status)) {
            throw new IllegalArgumentException("Trạng thái thiết bị không được để trống");
        }
        try {
            return EquipmentStatus.valueOf(status.trim().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái chỉ nhận AVAILABLE/MAINTENANCE/BROKEN");
        }
    }
}
