package dao;

import model.Equipment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    public boolean insert(Equipment equipment) {
        String sql = "INSERT INTO equipments(name, total_quantity, available_quantity, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipment.getName());
            ps.setInt(2, equipment.getTotalQuantity());
            ps.setInt(3, equipment.getAvailableQuantity());
            ps.setString(4, equipment.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm thiết bị", e);
        }
    }

    public List<Equipment> findAll() {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipments";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipment equipment = new Equipment();
                equipment.setId(rs.getInt("id"));
                equipment.setName(rs.getString("name"));
                equipment.setTotalQuantity(rs.getInt("total_quantity"));
                equipment.setAvailableQuantity(rs.getInt("available_quantity"));
                equipment.setStatus(rs.getString("status"));

                list.add(equipment);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách thiết bị", e);
        }

        return list;
    }

    public Equipment findById(int id) {
        String sql = "SELECT * FROM equipments WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Equipment equipment = new Equipment();
                equipment.setId(rs.getInt("id"));
                equipment.setName(rs.getString("name"));
                equipment.setTotalQuantity(rs.getInt("total_quantity"));
                equipment.setAvailableQuantity(rs.getInt("available_quantity"));
                equipment.setStatus(rs.getString("status"));

                return equipment;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm thiết bị", e);
        }
        return null;
    }

    public Equipment findById(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM equipments WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Equipment equipment = new Equipment();
                equipment.setId(rs.getInt("id"));
                equipment.setName(rs.getString("name"));
                equipment.setTotalQuantity(rs.getInt("total_quantity"));
                equipment.setAvailableQuantity(rs.getInt("available_quantity"));
                equipment.setStatus(rs.getString("status"));

                return equipment;
            }
        }
        return null;
    }

    public boolean update(Equipment equipment) {
        String sql = "UPDATE equipments SET name=?, total_quantity=?, available_quantity=?, status=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipment.getName());
            ps.setInt(2, equipment.getTotalQuantity());
            ps.setInt(3, equipment.getAvailableQuantity());
            ps.setString(4, equipment.getStatus());
            ps.setInt(5, equipment.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật thiết bị", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM equipments WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa thiết bị", e);
        }
    }

    public boolean updateAvailableQuantity(int id, int quantity) {
        String sql = "UPDATE equipments SET available_quantity=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật số lượng khả dụng", e);
        }
    }

    public boolean reserveAvailableQuantity(int id, int quantity, Connection conn) throws SQLException {
        String sql = """
                UPDATE equipments
                SET available_quantity = available_quantity - ?
                WHERE id = ?
                  AND status = 'AVAILABLE'
                  AND available_quantity >= ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, id);
            ps.setInt(3, quantity);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean increaseAvailableQuantity(int id, int quantity, Connection conn) throws SQLException {
        String sql = """
                UPDATE equipments
                SET available_quantity = available_quantity + ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }
}
