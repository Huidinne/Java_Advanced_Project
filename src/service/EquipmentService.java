package service;

import dao.EquipmentDAO;
import model.Equipment;

import java.util.List;

public class EquipmentService {

    private EquipmentDAO equipmentDAO = new EquipmentDAO();

    public boolean addEquipment(String name, int totalQuantity, String status) {
        Equipment equipment = new Equipment();
        equipment.setName(name);
        equipment.setTotalQuantity(totalQuantity);
        equipment.setAvailableQuantity(totalQuantity);
        equipment.setStatus(status);

        return equipmentDAO.insert(equipment);
    }

    public List<Equipment> getAllEquipment() {
        return equipmentDAO.findAll();
    }

    public Equipment getEquipmentById(int id) {
        return equipmentDAO.findById(id);
    }

    public boolean updateEquipment(int id, String name, int totalQuantity, String status) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setName(name);
        equipment.setTotalQuantity(totalQuantity);
        equipment.setAvailableQuantity(totalQuantity);
        equipment.setStatus(status);

        return equipmentDAO.update(equipment);
    }

    public boolean updateAvailableQuantity(int id, int availableQuantity) {
        Equipment equipment = equipmentDAO.findById(id);

        if (equipment == null) {
            System.out.println("Thiết bị không tồn tại");
            return false;
        }

        if (availableQuantity > equipment.getTotalQuantity()) {
            System.out.println("Số lượng khả dụng không được vượt quá tổng số lượng");
            return false;
        }

        if (availableQuantity < 0) {
            System.out.println("Số lượng không được âm");
            return false;
        }

        return equipmentDAO.updateAvailableQuantity(id, availableQuantity);
    }

    public boolean deleteEquipment(int id) {
        return equipmentDAO.delete(id);
    }
}
