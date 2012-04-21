package sdu.dsa.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import sdu.dsa.common.MonitorDTO;

public class DBManager {

	private static String dbURL = "jdbc:derby://localhost:1527/DSA;user=DSA;password=DSA";

	// jdbc Connection
	private static Connection connection;

	private DBManager() {}

	public static Connection getConnection() {
		if (connection == null) {
			try	{
				Class.forName("org.apache.derby.jdbc.ClientDriver").getInterfaces();
				//Get a connection
				connection = DriverManager.getConnection(dbURL); 
			} catch (Exception except) {
				except.printStackTrace();
			}
		}
		return connection;
	}

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

}
