package dao;

import model.Room;
import model.RoomStatus;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public boolean insert(Room room) {
        String sql = "INSERT INTO rooms(name, capacity, location, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getLocation());
            ps.setString(4, RoomStatus.AVAILABLE.toString());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insert room", e);
        }
    }


    public List<Room> findAll() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setName(rs.getString("name"));
                room.setCapacity(rs.getInt("capacity"));
                room.setLocation(rs.getString("location"));
                room.setStatus(RoomStatus.valueOf(rs.getString("status")));
                list.add(room);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi get rooms", e);
        }

        return list;
    }


    public boolean updateRoomInfo(Room room) {
        String sql = "UPDATE rooms SET name=?, capacity=?, location=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getLocation());
            ps.setInt(4, room.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi update room", e);
        }
    }

    public boolean updateStatus(int id, RoomStatus status) {
        String sql = "UPDATE rooms SET status=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi update room status", e);
        }
    }


    public boolean delete(int id) {
        String sql = "DELETE FROM rooms WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi delete room", e);
        }
    }

    public Room findById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setName(rs.getString("name"));
                room.setCapacity(rs.getInt("capacity"));
                room.setLocation(rs.getString("location"));
                room.setStatus(RoomStatus.valueOf(rs.getString("status").toUpperCase()));

                return room;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phòng", e);
        }

        return null;
    }

    public List<Room> findAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
        List<Room> list = new ArrayList<>();
        String sql = """
                SELECT r.*
                FROM rooms r
                WHERE r.status = 'AVAILABLE'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM bookings b
                      WHERE b.room_id = r.id
                        AND b.status IN ('PENDING', 'APPROVED')
                        AND b.start_time < ?
                        AND b.end_time > ?
                  )
                ORDER BY r.id
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(endTime));
            ps.setTimestamp(2, Timestamp.valueOf(startTime));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setName(rs.getString("name"));
                room.setCapacity(rs.getInt("capacity"));
                room.setLocation(rs.getString("location"));
                room.setStatus(RoomStatus.valueOf(rs.getString("status").toUpperCase()));
                list.add(room);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách phòng trống", e);
        }

        return list;
    }

}
