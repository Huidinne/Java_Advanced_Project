package dao;

import model.Booking;
import model.BookingStatus;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

	public int insert(Booking booking, Connection conn) throws SQLException {
		String sql = "INSERT INTO bookings(user_id, room_id, start_time, end_time, status, support_staff_id) VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, booking.getUserId());
			ps.setInt(2, booking.getRoomId());
			ps.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
			ps.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
			ps.setString(5, booking.getStatus().name());

			if (booking.getSupportStaffId() == null) {
				ps.setNull(6, Types.INTEGER);
			} else {
				ps.setInt(6, booking.getSupportStaffId());
			}

			int affected = ps.executeUpdate();
			if (affected == 0) {
				throw new SQLException("Không thể tạo booking");
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		}

		throw new SQLException("Không lấy được booking_id vừa tạo");
	}

	public boolean existsConflict(int roomId, LocalDateTime startTime, LocalDateTime endTime) {
		try (Connection conn = DBConnection.getConnection()) {
			return existsConflict(roomId, startTime, endTime, conn);
		} catch (SQLException e) {
			throw new RuntimeException("Lỗi kiểm tra xung đột booking", e);
		}
	}

	public boolean existsConflict(int roomId, LocalDateTime startTime, LocalDateTime endTime, Connection conn) throws SQLException {
		String sql = """
				SELECT 1
				FROM bookings
				WHERE room_id = ?
				  AND status IN ('PENDING', 'APPROVED')
				  AND start_time < ?
				  AND end_time > ?
				LIMIT 1
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, roomId);
			ps.setTimestamp(2, Timestamp.valueOf(endTime));
			ps.setTimestamp(3, Timestamp.valueOf(startTime));

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	public List<Booking> findByUserId(int userId) {
		List<Booking> list = new ArrayList<>();
		String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY start_time DESC";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Booking booking = new Booking();
				booking.setId(rs.getInt("id"));
				booking.setUserId(rs.getInt("user_id"));
				booking.setRoomId(rs.getInt("room_id"));
				booking.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
				booking.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
				booking.setStatus(BookingStatus.valueOf(rs.getString("status")));

				int supportStaffId = rs.getInt("support_staff_id");
				booking.setSupportStaffId(rs.wasNull() ? null : supportStaffId);
				list.add(booking);
			}

		} catch (SQLException e) {
			throw new RuntimeException("Lỗi lấy lịch sử booking", e);
		}

		return list;
	}

	public Booking findByIdAndUserId(int bookingId, int userId, Connection conn) throws SQLException {
		String sql = "SELECT * FROM bookings WHERE id = ? AND user_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, bookingId);
			ps.setInt(2, userId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Booking booking = new Booking();
				booking.setId(rs.getInt("id"));
				booking.setUserId(rs.getInt("user_id"));
				booking.setRoomId(rs.getInt("room_id"));
				booking.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
				booking.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
				booking.setStatus(BookingStatus.valueOf(rs.getString("status")));

				int supportStaffId = rs.getInt("support_staff_id");
				booking.setSupportStaffId(rs.wasNull() ? null : supportStaffId);
				return booking;
			}
		}

		return null;
	}

	public boolean updateStatusIfCurrent(int bookingId, int userId, BookingStatus currentStatus,
									BookingStatus newStatus, Connection conn) throws SQLException {
		String sql = "UPDATE bookings SET status = ? WHERE id = ? AND user_id = ? AND status = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, newStatus.name());
			ps.setInt(2, bookingId);
			ps.setInt(3, userId);
			ps.setString(4, currentStatus.name());
			return ps.executeUpdate() > 0;
		}
	}
}
