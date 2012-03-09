package sdu.tek.dsa;

import java.io.IOException;

public class EnvironmentDemo {

	public static void main(String[] args) throws IOException {
		Sensor s1 = new Sensor();
		s1.start();
		
		Sensor s2 = new Sensor();
		s2.start();

		Monitor monitor = new Monitor();
		monitor.bindSensor(s1.getAddress(), s1.getPort(), 5000);
		monitor.bindSensor(s2.getAddress(), s2.getPort(), 2000);

	}

}
