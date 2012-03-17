package sdu.dsa.demo;

import java.io.IOException;

public class EnvironmentDemo {
	public static void main(String[] args) throws IOException {
		
		DeviceSimulator.setDirectory(System.getProperty("user.dir") + "/bin");

		new DeviceSimulator("java", "sdu.dsa.monitor.Monitor", "7000");
		new DeviceSimulator("java", "sdu.dsa.sensor.Sensor", "1", "127.0.0.1", "7000");
		new DeviceSimulator("java", "sdu.dsa.sensor.Sensor", "2", "127.0.0.1", "7000");
		new DeviceSimulator("java", "sdu.dsa.sensor.Sensor", "3", "127.0.0.1", "7000");
	}
}
