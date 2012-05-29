package sdu.dsa.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sdu.dsa.common.MonitorDTO;

/** Database Manager
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
public class DBManager {
	
	// Change your connection data here
	private static final String connectionString = "jdbc:mysql://localhost:3306/DSA";
	private static final String username = "root";
	private static final String password = "root";

	/**
	 * jdbc Connection
	 */
	private static Connection connection;
	
	/**
	 * Private Constructor.
	 */
	private DBManager() {}

	/**
	 * Method getConnection.
	 * @return the existing open connection to the database or a new one if no open connections are present. 
	 */
	public static Connection getConnection() {
		if (connection == null) {
			try	{
				Class.forName("com.mysql.jdbc.Driver");

				//Get a connection
				connection = DriverManager.getConnection(connectionString, username, password);
			} catch (Exception except) {
				except.printStackTrace();
			}
		}
		return connection;
	}
	
	/**
	 * Method closeConnection.
	 */
	public static void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Store data in a List of MonitorDTO in the database.
	 * @param data the list of MonitorDTO
	 */
	public static void storeData(ArrayList<MonitorDTO> data)
	{
		try {
			String sql = "INSERT INTO record (sensorID, timestamp, temperature, humidity) VALUES (?,?,?,?)";
			PreparedStatement statement = getConnection().prepareStatement(sql);
			for (MonitorDTO dto : data) {
				statement.setInt(1, dto.getSensorID());
				statement.setLong(2, dto.getTimestamp());
				statement.setFloat(3, dto.getTemperature());
				statement.setFloat(4, dto.getHumidity());
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check all the changes in the sleeptime and flush the changes.
	 * @return A map with the changes.
	 */
	public static Map<Integer, Integer> flushSleeptimeUpdates() {
		HashMap<Integer,Integer> result = new HashMap<Integer, Integer>();
		
		String sqlGetUpdates = "SELECT id, sleeptime " +
							   "FROM sensor " +
							   "WHERE changed<>0";
		
		String sqlFlushUpdates = "UPDATE sensor " +
							     "SET changed=0";
		
		Connection connection = getConnection();
		
		try {
			connection.setAutoCommit(false);
			ResultSet updates = connection.createStatement().executeQuery(sqlGetUpdates);
			connection.createStatement().executeUpdate(sqlFlushUpdates);
			connection.commit();
			connection.setAutoCommit(true);
			
			int id, sleeptime;
			
			while (updates.next()) {
				id = updates.getInt("id");
				sleeptime = updates.getInt("sleeptime");
				result.put(id, sleeptime);
			}
			
					
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Method used when the server is starting to inform the server to send all the sleeptimes
	 * to the monitor.
	 */
	public static void initializeSleeptimeUpdates() {
		String sql = "UPDATE sensor " +
			     	 "SET changed=1";
		try {
			getConnection().createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
