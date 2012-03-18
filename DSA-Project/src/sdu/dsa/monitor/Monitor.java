package sdu.dsa.monitor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.Data;

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
					String message = new String(receivingPacket.getData());
					System.out.println("port:" + port + " = " + message);
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

	private List<String> unwrapStringPacket(byte[] bytes) throws IOException {
		List<String> lst = new ArrayList<String>();
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
		InputStreamReader strInputReader = new InputStreamReader(
				byteInputStream);
		BufferedReader bufferedReader = new BufferedReader(strInputReader);
		String fullCommand = bufferedReader.readLine();
		// TODO: secure the indexOf
		lst.add(fullCommand.substring(0, fullCommand.indexOf("#") - 1));
		fullCommand = fullCommand.substring(fullCommand.indexOf("#") + 1);
		for (String parameter : fullCommand.split(":")) {
			lst.add(parameter);
		}
		return lst;
	}
}
