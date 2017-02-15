package dbtLab3;

import java.sql.*;
import java.util.*;


/**
 * Database is a class that specifies the interface to the movie
 * database. Uses JDBC.
 */
public class Database {

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Create the database interface object. Connection to the
     * database is performed later.
     */
    public Database() {
        conn = null;
    }

    /**
     * Open a connection to the database, using the specified user
     * name and password.
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

    public boolean userExists(String userName) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT *\n" +
                "FROM   users\n" +
                "WHERE  username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            rs = ps.executeQuery();
            if (rs.next()){
            	return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return false;
    }
    
    public List<String> getMovies(){
    	Statement s = null;
    	ResultSet rs = null;
    	List<String> found = new LinkedList<>();
        try {
            String sql =
                "SELECT movie_name\n" +
                "FROM   movies\n";
            s = conn.createStatement();
            rs = s.executeQuery(sql);
            while (rs.next()) {
                found.add(rs.getString("movie_name"));
            }
            return found;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	try {
    			if (s != null){
    				s.close();
    			}
    			if (rs != null){
    				rs.close();
    			}
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        return found;
    }
    public List<String> getDates(String movieName){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	List<String> found = new LinkedList<>();
        try {
            String sql =
                "SELECT date\n" +
                "FROM   performances\n" +
                "WHERE  movie_name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, movieName);
            rs = ps.executeQuery();
            while (rs.next()) {
                found.add(rs.getString("date"));
            }
            return found;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return found;
    }
    
    public String getTheater(String movieName, String date){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		String sql =
    				"SELECT theater_name\n" +
    				"FROM performances\n" +
    				"WHERE movie_name = ?\n" +
    				"AND date = ?";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, movieName);
    		ps.setString(2, date);
    		rs = ps.executeQuery();
    		if (rs.next()){
    			return rs.getString("theater_name");
    		}
    	} catch (SQLException e){
    		e.printStackTrace();
    	} finally {
    		closePs(ps, rs);
    	}
    	return "";
    }
    
    public int getSeats(String theaterName){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		String sql =
    				"SELECT max_seats\n" +
    				"FROM theaters\n" +
    				"WHERE theater_name = ?";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, theaterName);
    		rs = ps.executeQuery();
    		if (rs.next()){
    			return rs.getInt("max_seats");
    		}
    	} catch (SQLException e){
    		e.printStackTrace();
    	} finally {
    		closePs(ps, rs);
    	}
    	return 0;
    }
    
    public int getNbrOfBookings(String movieName, String date){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		String sql =
    				"SELECT count(*)\n" +
    				"FROM bookings\n" +
    				"WHERE movie_name = ?\n" +
    				"AND date = ?";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, movieName);
    		ps.setString(2, date);
    		rs = ps.executeQuery();
    		if (rs.next()){
    			return rs.getInt(1);
    		}
    	} catch (SQLException e){
    		e.printStackTrace();
    	} finally {
    		closePs(ps, rs);
    	}
    	return 0;
    }
    
    public int createBooking(String username, String movieName, String date, String theaterName){
    	try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		String sql =
    				"INSERT INTO bookings(username, movie_name, date)\n" +
    				"VALUES(?, ?, ?)";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, username);
    		ps.setString(2, movieName);
    		ps.setString(3, date);
    		if (ps.executeUpdate() == 1){
    			rs = ps.getGeneratedKeys();
    			if (rs.next()){
    				conn.commit();
    				conn.setAutoCommit(true);
    				return rs.getInt(1);
    			}
    		}
    	} catch (SQLException e){
    		e.printStackTrace();
    	} finally {
    		closePs(ps, rs);
    	}

    	try {
        	conn.rollback();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    private void closePs(PreparedStatement ps, ResultSet rs){
    	try {
			if (ps != null){
				ps.close();
			}
			if (rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
