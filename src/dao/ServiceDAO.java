package dao;

import model.Service;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

	public boolean insert(Service service) {
		String sql = "INSERT INTO services(name, price) VALUES (?, ?)";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, service.getName());
			ps.setDouble(2, service.getPrice());
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new RuntimeException("Lỗi thêm dịch vụ", e);
		}
	}

	public List<Service> findAll() {
		List<Service> list = new ArrayList<>();
		String sql = "SELECT * FROM services ORDER BY id";

		try (Connection conn = DBConnection.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				Service service = new Service();
				service.setId(rs.getInt("id"));
				service.setName(rs.getString("name"));
				service.setPrice(rs.getDouble("price"));
				list.add(service);
			}

		} catch (SQLException e) {
			throw new RuntimeException("Lỗi lấy danh sách dịch vụ", e);
		}

		return list;
	}

	public Service findById(int id) {
		String sql = "SELECT * FROM services WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Service service = new Service();
				service.setId(rs.getInt("id"));
				service.setName(rs.getString("name"));
				service.setPrice(rs.getDouble("price"));
				return service;
			}

		} catch (SQLException e) {
			throw new RuntimeException("Lỗi tìm dịch vụ", e);
		}

		return null;
	}

	public boolean update(Service service) {
		String sql = "UPDATE services SET name = ?, price = ? WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, service.getName());
			ps.setDouble(2, service.getPrice());
			ps.setInt(3, service.getId());
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new RuntimeException("Lỗi cập nhật dịch vụ", e);
		}
	}

	public boolean delete(int id) {
		String sql = "DELETE FROM services WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new RuntimeException("Lỗi xóa dịch vụ", e);
		}
	}

	public boolean existsByName(String name) {
		String sql = "SELECT 1 FROM services WHERE LOWER(name) = LOWER(?) LIMIT 1";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			return rs.next();

		} catch (SQLException e) {
			throw new RuntimeException("Lỗi kiểm tra tên dịch vụ", e);
		}
	}

	public boolean existsByNameExceptId(String name, int excludedId) {
		String sql = "SELECT 1 FROM services WHERE LOWER(name) = LOWER(?) AND id <> ? LIMIT 1";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, name);
			ps.setInt(2, excludedId);
			ResultSet rs = ps.executeQuery();
			return rs.next();

		} catch (SQLException e) {
			throw new RuntimeException("Lỗi kiểm tra trùng tên dịch vụ", e);
		}
	}
}
