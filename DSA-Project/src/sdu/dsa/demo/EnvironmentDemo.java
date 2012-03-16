package sdu.dsa.demo;

import java.io.IOException;

public class EnvironmentDemo {

	public static void main(String[] args) throws IOException {
		new ProcessBuilder("java", "./bin/sdu/dsa/monitor/Monitor").start();
		
		new ProcessBuilder("java", "./bin/sdu/dsa/sensor/Sensor", "1 127.0.0.1 5000").start();
		new ProcessBuilder("java", "./bin/sdu/dsa/sensor/Sensor", "2 127.0.0.1 5000").start();
		new ProcessBuilder("java", "./bin/sdu/dsa/sensor/Sensor", "3 127.0.0.1 5000").start();
	}
}
