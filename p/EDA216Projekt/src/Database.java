import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class Database {

	/**
	 * The database connection.
	 */
	private Connection conn;

	/**
	 * Create the database interface object. Connection to the database is
	 * performed later.
	 */
	public Database() {
		conn = null;
	}

	/**
	 * Open a connection to the database, using the specified user name and
	 * password.
	 */
	public boolean openConnection(String filename) {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Close the connection to the database.
	 */
	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if the connection to the database has been established
	 * 
	 * @return true if the connection has been established
	 */
	public boolean isConnected() {
		return conn != null;
	}

	public List<String> getCookies() throws SQLException {
		List<String> cookieList = new LinkedList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT *\n" + "FROM cookies";
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		while (rs.next()) {
			cookieList.add(rs.getString("cookie_name"));
		}

		closePs(ps, rs);
		return cookieList;
	}
	
	public int addPallet(String cookieName) throws SQLException {
		conn.setAutoCommit(false);
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO pallets(cookie_name)\n" +
		"VALUES(?)";
		ps = conn.prepareStatement(sql);
		ps.setString(1, cookieName);
		if (ps.executeUpdate() == 1){
			rs = ps.getGeneratedKeys();
			if (rs.next()){
				conn.commit();		
				conn.setAutoCommit(true);
				int palletId = rs.getInt(1);
				System.out.println("Added " + cookieName + " to database with palletId " + palletId);
				closePs(ps, rs);
				return palletId;
			}
		}
        conn.rollback();
		conn.setAutoCommit(true);
		closePs(ps, rs);
		return 0;
	}
	
	public boolean setProductionDate(String barCode) {
		conn.setAutoCommit(false);
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "UPDATE pallets\n" +
		"SET production_date = ?\n" +
		"WHERE pallet_id = ?";
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		ps = conn.prepareStatement(sql);
		ps.setString(1, timestamp.toString());
		ps.setString(2, barCode);
		
		if (ps.executeUpdate() == 1){
			rs = ps.getGeneratedKeys();
			if (rs.next()){
				conn.commit();		
				conn.setAutoCommit(true);
				int palletId = rs.getInt(1);
				System.out.println("Added " + barCode + " to database with palletId " + palletId);
				closePs(ps, rs);
				return true;
			}
		}
        conn.rollback();
		conn.setAutoCommit(true);
		closePs(ps, rs);
		return false;
	}

	public void foreignKey() throws SQLException {
		PreparedStatement ps = null;
		try {
			String sql = "PRAGMA foreign_keys = on";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}

	private void closePs(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
