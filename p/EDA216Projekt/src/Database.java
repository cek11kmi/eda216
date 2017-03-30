import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

		String sql = "INSERT INTO pallets(cookie_name)\n" + "VALUES(?)";
		ps = conn.prepareStatement(sql);
		ps.setString(1, cookieName);
		if (ps.executeUpdate() == 1) {
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
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

	public boolean setProductionDate(String barCode) throws SQLException {
		conn.setAutoCommit(false);
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "UPDATE pallets\n" + "SET production_date = ?\n" + "WHERE pallet_id = ?\n"
				+ "AND production_date IS null";
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		ps = conn.prepareStatement(sql);
		ps.setString(1, timeStamp);
		ps.setString(2, barCode);
		if (ps.executeUpdate() == 1) {
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				conn.commit();
				conn.setAutoCommit(true);
				System.out.println("Added production date " + timeStamp + " to database with barcode " + barCode);
				closePs(ps, rs);
				return true;
			}
		}
		conn.rollback();
		conn.setAutoCommit(true);
		closePs(ps, rs);
		return false;
	}

	public List<String> getPalletsContaining(String product) throws SQLException {
		List<String> palletList = new LinkedList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT pallet_id\n" + "FROM pallets\n" + "WHERE cookie_name = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, product);
		rs = ps.executeQuery();
		while (rs.next()) {
			palletList.add(String.valueOf(rs.getInt("pallet_id")));
		}
		closePs(ps, rs);
		return palletList;
	}

	public List<String> getBlocked() throws SQLException {
		List<String> palletList = new LinkedList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT distinct coockie_name\n" + "FROM pallets\n" + "WHERE blocked = 1";
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		while (rs.next()) {
			palletList.add(String.valueOf(rs.getInt("pallet_id")));
		}
		closePs(ps, rs);
		return palletList;
	}

	public List<String> getBlockedPalletsContaining(String product) throws SQLException {
		List<String> palletList = new LinkedList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT pallet_id\n" + "FROM pallets\n" + "WHERE cookie_name = ? AND\n" + "blocked = 1";
		ps = conn.prepareStatement(sql);
		ps.setString(1, product);
		rs = ps.executeQuery();
		while (rs.next()) {
			palletList.add(String.valueOf(rs.getInt("pallet_id")));
		}
		closePs(ps, rs);
		return palletList;
	}

	public List<DeliveredPallets> getDeliveredPalletsByCustomer(String customer) throws SQLException {
		List<DeliveredPallets> palletList = new LinkedList<DeliveredPallets>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT pallet_id, delivered\n" + "FROM deliveries\n" + "JOIN orders\n" + "USING order_id\n"
				+ "WHERE customer_name = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, customer);
		rs = ps.executeQuery();
		while (rs.next()) {
			palletList.add(new DeliveredPallets(String.valueOf(rs.getInt("pallet_id")), rs.getString("delivered")));
		}
		closePs(ps, rs);
		return palletList;
	}

	public List<Pallet> getPalletsByProduct(String product) throws SQLException {
		List<Pallet> palletList = new LinkedList<Pallet>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT pallet_id, delivery_date, customer_name, production_date, blocked\n"
				+ "FROM pallets NATURAL JOIN deliveries NATURAL JOIN orders\n" 
				+ "WHERE cookie_name = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, product);
		rs = ps.executeQuery();
		while (rs.next()) {
			String palletId = String.valueOf(rs.getInt(1));
			String deliveryDate = "";
			if (!rs.getString(2).equals("0000-00-00")){
				deliveryDate = rs.getString(2);
			}
			String customerName = "";
			if (!rs.getString(3).equals("none")){
				customerName = rs.getString(3);
			}
			String productionDate = rs.getString(4);
			palletList.add(new Pallet(palletId, product, deliveryDate, customerName,
					productionDate, (rs.getInt(5) == 1)));
		}
		closePs(ps, rs);
		return palletList;
	}
	
	public Pallet getPalletByID(int barCode) throws SQLException{
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT cookie_name, delivery_date, customer_name, production_date, blocked\n"
				+ "FROM pallets NATURAL JOIN deliveries NATURAL JOIN orders NATURAL JOIN order_details\n" 
				+ "WHERE pallet_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, barCode);
		rs = ps.executeQuery();
		while (rs.next()) {
			String cookieName = rs.getString(1);
			String deliveryDate = "";
			if (!rs.getString(2).equals("0000-00-00")){
				deliveryDate = rs.getString(2);
			}
			String customerName = "";
			if (!rs.getString(3).equals("none")){
				customerName = rs.getString(3);
			}
			String productionDate = rs.getString(4);
			
			return new Pallet(String.valueOf(barCode), cookieName, deliveryDate, customerName,
					productionDate, (rs.getInt(5) == 1));
		}
		closePs(ps, rs);
		return null;
	}

	public List<Pallet> getPalletsByCustomer(String customer) throws SQLException {
		List<Pallet> palletList = new LinkedList<Pallet>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT pallet_id, cookie_name, delivery_date, production_date, blocked\n"
				+ "FROM pallets NATURAL JOIN deliveries NATURAL JOIN orders\n" 
				+ "WHERE customer_name = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, customer);
		rs = ps.executeQuery();
		while (rs.next()) {
			String palletId = String.valueOf(rs.getInt(1));
			String cookieName = rs.getString(2);
			String deliveryDate = "";
			if (!rs.getString(3).equals("0000-00-00")){
				deliveryDate = rs.getString(3);
			}
			String productionDate = rs.getString(4);
			palletList.add(new Pallet(palletId, cookieName, deliveryDate, customer,
					productionDate, (rs.getInt(5) == 1)));
		}
		closePs(ps, rs);
		return palletList;
	}
	
	public List<String> getCustomers() throws SQLException {
		List<String> customerList = new LinkedList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "SELECT customer_name\n" + 
				"FROM customers";
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		
		while (rs.next()){
			customerList.add(rs.getString(1));
		}
		closePs(ps, rs);
		return customerList;
	}
	
	public List<Pallet> getPalletsByTime(String start, String end) throws SQLException {
		List<Pallet> palletList = new LinkedList<Pallet>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT pallet_id, cookie_name, delivery_date, customer_name, production_date, blocked\n"
				+ "FROM pallets NATURAL JOIN deliveries NATURAL JOIN orders\n" 
				+ "WHERE production_date BETWEEN ? AND ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, start);
		ps.setString(2, end);
		rs = ps.executeQuery();
		while (rs.next()) {
			String palletId = String.valueOf(rs.getInt(1));
			String cookieName = rs.getString(2);
			String customerName = "";
			if (!rs.getString(3).equals("none")){
				customerName = rs.getString(3);
			}
			String deliveryDate = "";
			if (!rs.getString(4).equals("0000-00-00")){
				deliveryDate = rs.getString(4);
			}
			String productionDate = rs.getString(5);
			palletList.add(new Pallet(palletId, cookieName, deliveryDate, customerName,
					productionDate, (rs.getInt(6) == 1)));
		}
		closePs(ps, rs);
		return palletList;
	}
	
	public List<Pallet> getAllBlockedPallets() throws SQLException {
		List<Pallet> palletList = new LinkedList<Pallet>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT pallet_id, cookie_name, production_date, blocked\n"
				+ "FROM pallets\n" 
				+ "WHERE blocked = 1";
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		while (rs.next()) {
			String palletId = String.valueOf(rs.getInt(1));
			String cookieName = rs.getString(2);
			
			String productionDate = rs.getString(3);
			palletList.add(new Pallet(palletId, cookieName, null, null,
					productionDate, (rs.getInt(4) == 1)));
		}
		closePs(ps, rs);
		return palletList;
	}
	
	public boolean blockPalletsByTimeAndCookie(String start, String end, String cookieName) throws SQLException {
		PreparedStatement ps = null;

		String sql = "UPDATE pallets\n" +
					"SET blocked = 1\n" + 
					"WHERE cookie_name = ? AND production_date BETWEEN ? AND ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, cookieName);
		ps.setString(2, start);
		ps.setString(3, end);
		if (ps.executeUpdate() > 0){
			ps.close();
			return true;
		} else {
			ps.close();
			return false;
		}
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
