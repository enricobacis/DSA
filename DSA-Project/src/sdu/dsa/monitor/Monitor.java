package sdu.dsa.monitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.Timer;

import sdu.dsa.common.MonitorDTO;

public class Monitor {

	private ArrayList<SensorClient> sensors;
	private List<MonitorDTO> dtoBuffer;
	private int port;
	private Timer sendingTimer;
	private Socket sendingSocket;
	private ObjectOutputStream oos;
	private InetAddress serverIp;
	private int serverPort;
	
	private static final int SENDING_TIMEOUT = 5000; 

	public Monitor(InetAddress _serverIp, int _serverPort, int _port) {
		this.serverIp = _serverIp;
		this.serverPort = _serverPort;
		this.port = _port;
		sensors = new ArrayList<SensorClient>();
		dtoBuffer = Collections.synchronizedList(new ArrayList<MonitorDTO>());

		ListeningThread listeningThread = new ListeningThread();
		listeningThread.start();
		
		sendingTimer = new Timer(SENDING_TIMEOUT, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!dtoBuffer.isEmpty()) {
					List<MonitorDTO> temp = dtoBuffer;
					dtoBuffer = Collections.synchronizedList(new ArrayList<MonitorDTO>());
					try {
						sendingSocket = new Socket(serverIp, serverPort);
						oos = new ObjectOutputStream(sendingSocket.getOutputStream());
						oos.writeObject(new ArrayList<MonitorDTO>(temp));
						oos.close();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		sendingTimer.start();
	}
	
	protected void finalize() throws Throwable {
		sendingSocket.close();
		super.finalize();
	}

	public void bindSensor(int ID, InetAddress address, int port, int sleeptime) {
		SensorClient sensor = new SensorClient(ID, address, port, sleeptime);
		sensors.add(sensor);
		sensor.start();
	}

	private static void printError() {
		System.out.println("Usage: Monitor server_ip server_port [listening_port = 5000]");
	}
	
	private HashMap<String,String> unwrapStringPacket(byte[] bytes) throws IOException {
		String data = new String(bytes);
		data = data.substring(data.indexOf("#") + 1);
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		for (String attribute : data.split(",")) {
			String[] token = attribute.split("=");
			map.put(token[0], token[1]);
		}
		
		return map;
	}

	public static void main(String[] args) {		
		if (args.length < 2) {
			printError();
		} else if (args.length > 3) {
			printError();
		} else {
			try {
				InetAddress serverIp = InetAddress.getByName(args[0]);
				int serverPort = Integer.parseInt(args[1]);
				
				int port = 5000;
				if (args.length == 3)
					port = Integer.parseInt(args[2]);	
				
				new Monitor(serverIp, serverPort, port);
				
			} catch (Exception e) {
				printError();
			}
		}
	}

	class SensorClient extends Thread {
		private int ID;
		private InetAddress address;
		private int port;
		private int sleeptime;
		private boolean running;

		public SensorClient(int ID, InetAddress address, int port, int sleeptime) {
			this.ID = ID;
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
					HashMap<String,String> map = unwrapStringPacket(receivingPacket.getData());
					
					dtoBuffer.add(new MonitorDTO(ID,
							Long.parseLong(map.get("timestamp").trim()),
							Float.parseFloat(map.get("temperature").trim()),
							Float.parseFloat(map.get("humidity").trim())));
					
					sleep(sleeptime);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
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
					bindSensor(sensorID, sensorAddress, sensorPort, 1000);

				} catch (IOException e) {
					System.out.println("Communication Error: " + sensorID);
					e.printStackTrace();
				}
			}
		}
	}
}
