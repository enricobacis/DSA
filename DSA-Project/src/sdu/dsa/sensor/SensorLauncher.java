package sdu.dsa.sensor;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Launches a Sensor communicating with a monitor
 */
public class SensorLauncher {

	/**
	 * Start a Sensor with the specified arguments
	 * 
	 * @param args
	 *            the parameters sensor_id monitor_ip_or_host monitor_port
	 */
	public static void main(String[] args) {
		args = new String[] {"1","127.0.0.1","5000"};
		try {
			// Get parameters in the right format
			int sensorID = Integer.parseInt(args[0]);
			InetAddress monitorAddress = InetAddress.getByName(args[1]);
			int monitorPort = Integer.parseInt(args[2]);
			// Basic verifications
			if (sensorID < 0) {
				throw new IllegalArgumentException(
						"Error: sensor_id must be a positive number");
			}
			if (monitorPort < 0 || monitorPort > 65536) {
				throw new IllegalArgumentException(
						"Error: sensor_id must be a number between 0 and 65536");
			}
			// Creation of a sensor with the specified parameters
			new SensorUDPConnector(new SimpleSensor(sensorID), monitorAddress,
					monitorPort).connect();
		} catch (NumberFormatException ex) {
			System.out
					.println("Error: sensor_id and monitor_port must be valid numbers");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (UnknownHostException ex) {
			System.out
					.println("Error: monitor_ip_or_host couldn't be resolved");
		} catch (SensorConnectionException ex) {
			System.out.println("Error: " + ex.getMessage());
		}
		catch (Exception e) {
			System.out
					.println("Usage: Sensor sensor_id monitor_ip_or_host monitor_port");
		}
	}

}
