package sdu.dsa.monitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import sdu.dsa.common.MonitorDTO;

public class Monitor {

	ArrayList<SensorClient> sensors;
	private int port;

	public Monitor(int port) {
		this.port = port;
		sensors = new ArrayList<SensorClient>();

		ListeningThread listeningThread = new ListeningThread();
		listeningThread.start();
	}

	public void bindSensor(InetAddress address, int port, int sleeptime) {
		SensorClient sensor = new SensorClient(address, port, sleeptime);
		sensors.add(sensor);
		sensor.start();
	}

	private static void printError() {
		System.out.println("Usage: Monitor [port = 5000]");
	}
	
	private HashMap<String,Float> unwrapStringPacket(byte[] bytes) throws IOException {
		String data = new String(bytes);
		data = data.substring(data.indexOf("#") + 1);
		
		HashMap<String, Float> map = new HashMap<String, Float>();
		
		for (String attribute : data.split(",")) {
			String[] token = attribute.split("=");
			map.put(token[0], Float.parseFloat(token[1]));
		}
		
		return map;
	}

	public static void main(String[] args) {
		int port = 5000;
		if (args.length == 1) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				printError();
			}
		} else if (args.length > 1) {
			printError();
		}

		new Monitor(port);
	}

	class SensorClient extends Thread {
		private int ID;
		private InetAddress address;
		private int port;
		private int sleeptime;
		private boolean running;

		public SensorClient(InetAddress address, int port, int sleeptime) {
			this.address = address;
			this.port = port;
			this.sleeptime = sleeptime;
		}

		@Override
		public void run() {
			startClient();
		}

		private void startClient() {
			running = true;
			byte[] packet = new byte[1024];
			DatagramSocket datagramSocket;
			try {
				datagramSocket = new DatagramSocket();
				byte[] dataCommand = "data".getBytes();
				DatagramPacket sendingPacket = new DatagramPacket(dataCommand,
						dataCommand.length, address, port);
				DatagramPacket receivingPacket = new DatagramPacket(packet,
						packet.length);
				while (running) {
					datagramSocket.send(sendingPacket);
					datagramSocket.receive(receivingPacket);
					System.out.println("port:" + port + " = " + new String(receivingPacket.getData()));
					HashMap<String,Float> map = unwrapStringPacket(receivingPacket.getData());
					MonitorDTO dto = new MonitorDTO(ID, map.get("timestamp").longValue(), map.get("temperature"), map.get("humidity"));
					sleep(sleeptime);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void stopClient() {
			running = false;
		}

		public int getID() {
			return ID;
		}

		public InetAddress getAddress() {
			return address;
		}

		public int getPort() {
			return port;
		}

		public int getSleeptime() {
			return sleeptime;
		}

		public void setSleeptime(int sleeptime) {
			this.sleeptime = sleeptime;
		}
	}

	private class ListeningThread extends Thread {

		private DatagramSocket datagramSocket;

		public ListeningThread() {
			try {
				datagramSocket = new DatagramSocket(port);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			byte[] receiveBuffer = new byte[1024];
			InetAddress sensorAddress;
			int sensorPort;
			int sensorID = 0;
			while (true) {
				DatagramPacket datagramPacket = new DatagramPacket(
						receiveBuffer, receiveBuffer.length);
				try {
					datagramSocket.receive(datagramPacket);
					sensorAddress = datagramPacket.getAddress();
					sensorPort = datagramPacket.getPort();
					String[] command = new String(datagramPacket.getData(),0, datagramPacket.getLength()).split("#");
					sensorID = Integer.parseInt(command[1]);
					for (SensorClient sensor : sensors) {
						if (sensor.getID() == sensorID) {
							sensor.stopClient();
							sensors.remove(sensor);
							break;
						}
					}

					// TODO: get the sleeptime from the database
					SensorClient sensorClient = new SensorClient(sensorAddress,
							sensorPort, 1000);
					sensorClient.start();
					sensors.add(sensorClient);

				} catch (IOException e) {
					System.out.println("Communication Error: " + sensorID);
					e.printStackTrace();
				}
			}
		}
	}
}
