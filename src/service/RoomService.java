package service;

import dao.RoomDAO;
import model.Room;
import model.RoomStatus;

import java.util.List;

public class RoomService {

    private RoomDAO roomDAO = new RoomDAO();

    public boolean addRoom(String name, int capacity, String location, RoomStatus roomStatus) {
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
        if (roomDAO.findById(id) == null) {
            System.out.println("Phòng không tồn tại");
            return false;
        }
        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setLocation(location);

        return roomDAO.updateRoomInfo(room);
    }

    public boolean updateRoomStatus(int id, RoomStatus status) {
        if (roomDAO.findById(id) == null) {
            System.out.println("Phòng không tồn tại");
            return false;
        }
        return roomDAO.updateStatus(id, status);
    }

    public boolean deleteRoom(int id) {
        if (roomDAO.findById(id) == null) {
            System.out.println("Phòng không tồn tại");
            return false;
        }
        return roomDAO.delete(id);
    }
}
