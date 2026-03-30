package dao;

import model.BookingDetail;
import model.DetailType;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDetailDAO {

	public boolean insertBatch(List<BookingDetail> details, Connection conn) throws SQLException {
		if (details == null || details.isEmpty()) {
			return true;
		}

		String sql = "INSERT INTO booking_details(booking_id, type, ref_id, quantity) VALUES (?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (BookingDetail detail : details) {
				ps.setInt(1, detail.getBookingId());
				ps.setString(2, detail.getType().name());
				ps.setInt(3, detail.getRefId());
				ps.setInt(4, detail.getQuantity());
				ps.addBatch();
			}

			int[] results = ps.executeBatch();
			for (int result : results) {
				if (result == PreparedStatement.EXECUTE_FAILED) {
					return false;
				}
			}
			return true;
		}
	}

	public List<BookingDetail> findByBookingId(int bookingId) {
		try (Connection conn = DBConnection.getConnection()) {
			return findByBookingId(bookingId, conn);
		} catch (SQLException e) {
			throw new RuntimeException("Lỗi lấy chi tiết booking", e);
		}
	}

	public List<BookingDetail> findByBookingId(int bookingId, Connection conn) throws SQLException {
		List<BookingDetail> list = new ArrayList<>();
		String sql = "SELECT * FROM booking_details WHERE booking_id = ? ORDER BY id";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, bookingId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				BookingDetail detail = new BookingDetail();
				detail.setId(rs.getInt("id"));
				detail.setBookingId(rs.getInt("booking_id"));
				detail.setType(DetailType.valueOf(rs.getString("type")));
				detail.setRefId(rs.getInt("ref_id"));
				detail.setQuantity(rs.getInt("quantity"));
				list.add(detail);
			}
		}

		return list;
	}
}
