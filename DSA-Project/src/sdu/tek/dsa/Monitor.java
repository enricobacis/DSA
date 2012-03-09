package sdu.tek.dsa;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Monitor extends Thread {
	
	ArrayList<SensorClient> sensors;
	
	public Monitor() {
		sensors = new ArrayList<SensorClient>();
	}
	
	public void bindSensor(InetAddress address, int port, int sleeptime) {
		SensorClient sensor = new SensorClient(address, port, sleeptime);
		sensors.add(sensor);
		sensor.start();
	}
	
	class SensorClient extends Thread {
		private InetAddress address;
		private int port;
		private int sleeptime;
		
		public SensorClient(InetAddress address, int port, int sleeptime) {
			this.address = address;
			this.port = port;
			this.sleeptime = sleeptime;
		}
		
		@Override
		public void run() {
			byte[] packet = new byte[1024];
			DatagramSocket datagramSocket;
			try {
				datagramSocket = new DatagramSocket();
				DatagramPacket datagramPacket = new DatagramPacket(packet, packet.length, address, port);
				while (true) {
					datagramSocket.send(datagramPacket);
					datagramSocket.receive(datagramPacket);
					String message = new String(datagramPacket.getData());
					System.out.println("port:" + port + " = " + message);
					sleep(sleeptime);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
