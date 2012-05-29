package sdu.dsa.demo;

import java.io.IOException;

/** Demonstration Environment. Run this to start a fully working
 * system environment.
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
public class EnvironmentDemo {

	/**
	 * Method main.
	 * @param args String[]
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DeviceSimulator.setDirectory(System.getProperty("user.dir") + "/bin");
		
		DeviceSimulator server  = new DeviceSimulator("java", "sdu.dsa.server.Server", "127.0.0.1", "5000", "6001");
		DeviceSimulator monitor = new DeviceSimulator("java", "sdu.dsa.monitor.Monitor", "127.0.0.1", "6001");
		DeviceSimulator sensor1 = new DeviceSimulator("java", "sdu.dsa.sensor.SensorLauncher", "1", "127.0.0.1");
		DeviceSimulator sensor2 = new DeviceSimulator("java", "sdu.dsa.sensor.SensorLauncher", "2", "127.0.0.1");
		DeviceSimulator sensor3 = new DeviceSimulator("java", "sdu.dsa.sensor.SensorLauncher", "3", "127.0.0.1");
		
		System.in.read();
		sensor1.stopDevice();
		sensor2.stopDevice();
		sensor3.stopDevice();
		monitor.stopDevice();
		server.stopDevice();
	}
}
