package service;

import dao.RoomDAO;
import model.Room;
import model.RoomStatus;
import util.Validator;

import java.util.List;

public class RoomService {

    private RoomDAO roomDAO = new RoomDAO();

    public boolean addRoom(String name, int capacity, String location, RoomStatus roomStatus) {
        validateRoomInput(name, capacity, location);
        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setLocation(location);
        room.setStatus(roomStatus);

        return roomDAO.insert(room);
    }

    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    public Room getRoomById(int id) {
        return roomDAO.findById(id);
    }

    public boolean updateRoom(int id, String name, int capacity, String location) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID phòng không hợp lệ");
        }
        if (roomDAO.findById(id) == null) {
            return false;
        }
        validateRoomInput(name, capacity, location);

        Room room = new Room();
        room.setId(id);
        room.setName(name);
        room.setCapacity(capacity);
        room.setLocation(location);

        return roomDAO.updateRoomInfo(room);
    }

    public boolean updateRoomStatus(int id, RoomStatus status) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID phòng không hợp lệ");
        }
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái phòng không hợp lệ");
        }
        if (roomDAO.findById(id) == null) {
            return false;
        }
        return roomDAO.updateStatus(id, status);
    }

    public boolean deleteRoom(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID phòng không hợp lệ");
        }
        if (roomDAO.findById(id) == null) {
            return false;
        }
        return roomDAO.delete(id);
    }

    private void validateRoomInput(String name, int capacity, String location) {
        if (Validator.isBlank(name)) {
            throw new IllegalArgumentException("Tên phòng không được để trống");
        }
        if (!Validator.isPositive(capacity)) {
            throw new IllegalArgumentException("Sức chứa phòng phải lớn hơn 0");
        }
        if (Validator.isBlank(location)) {
            throw new IllegalArgumentException("Vị trí phòng không được để trống");
        }
    }
}
