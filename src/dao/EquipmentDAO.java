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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Equipment> findAll() {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipments";

        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Equipment equipment = new Equipment();
                equipment.setId(rs.getInt("id"));
                equipment.setName(rs.getString("name"));
                equipment.setTotalQuantity(rs.getInt("total_quantity"));
                equipment.setAvailableQuantity(rs.getInt("available_quantity"));
                equipment.setStatus(rs.getString("status"));

                list.add(equipment);
            }

        } catch (Exception e) {
            e.printStackTrace();
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

        } catch (Exception e) {
            e.printStackTrace();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM equipments WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAvailableQuantity(int id, int quantity) {
        String sql = "UPDATE equipments SET available_quantity=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
