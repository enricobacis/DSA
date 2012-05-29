package sdu.dsa.monitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import sdu.dsa.common.MonitorDTO;
import sdu.dsa.common.UpdateSleeptimeDTO;

/**
 * Monitor that gathers all the data from the sensors in order
 * to send them to the server.
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
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

	/**
	 * Constructor for Monitor.
	 * @param _serverIp server ip
	 * @param _serverPort server port
	 * @param _port port for incoming HELLO messages from the sensors
	 */
	public Monitor(InetAddress _serverIp, int _serverPort, int _port) {
		this.serverIp = _serverIp;
		this.serverPort = _serverPort;
		this.port = _port;
		sensors = new ArrayList<SensorClient>();
		dtoBuffer = Collections.synchronizedList(new ArrayList<MonitorDTO>());
		
		System.out.println("Monitor Started");	
		
		ListeningThread listeningThread = new ListeningThread();
		listeningThread.start();
		
		UpdateSleeptimeThread updateSleeptimeThread = new UpdateSleeptimeThread();
		updateSleeptimeThread.start();
		
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
	
	/**
	 * Method finalize.
	 * @throws Throwable
	 */
	protected void finalize() throws Throwable {
		sendingSocket.close();
		super.finalize();
	}

	/**
	 * Bind a sensor to the server.
	 * @param ID sensor's ID
	 * @param address sensor's ip address
	 * @param port sensor's port
	 * @param sleeptime sensor's sleeptime
	 */
	public void bindSensor(int ID, InetAddress address, int port, int sleeptime) {
		SensorClient sensor = new SensorClient(ID, address, port, sleeptime);
		sensors.add(sensor);
		sensor.start();
	}

	/**
	 * Print the errors if the main method is called with wrong parameters
	 */
	private static void printError() {
		System.out.println("Usage: Monitor server_ip server_port [listening_port = 5000]");
	}
	
	/**
	 * Unwrap the string data passed from the sensor to the monitor
	 * @param bytes the incoming data
	 * @return A map with the data unwrapped
	 * @throws IOException
	 */
	private HashMap<String,String> unwrapStringPacket(byte[] bytes) throws IOException {
		String data = new String(bytes);
		data = data.substring(data.indexOf("#") + 1);
		data = data.substring(0, data.indexOf("#"));
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		for (String attribute : data.split(",")) {
			String[] token = attribute.split("=");
			map.put(token[0], token[1]);
		}
		
		return map;
	}

	/**
	 * Method main.
	 * @param args The arguments in the form: server_ip server_port [listening_port = 5000]
	 */
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
				
				System.out.println("Starting Monitor...");	
				new Monitor(serverIp, serverPort, port);
				
			} catch (Exception e) {
				printError();
			}
		}
	}

	/**
	 * Thread that wrap the connection to a sensor
 	 *
 	 * @author DSA-Project Group [Spring 2012]
 	 * @version 1.0
	 */
	class SensorClient extends Thread {
		private int ID;
		private InetAddress address;
		private int port;
		private int sleeptime;
		private boolean running;

		/**
		 * Constructor for SensorClient.
		 * @param ID sensor's ID
		 * @param address sensor's ip address 
		 * @param port sensor's port
		 * @param sleeptime sensor's sleeptime
		 */
		public SensorClient(int ID, InetAddress address, int port, int sleeptime) {
			this.ID = ID;
			this.address = address;
			this.port = port;
			this.sleeptime = sleeptime;
		}

		/**
		 * Method run.
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			startClient();
		}
		
		/**
		 * Start the client connection to the sensor.
		 */
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
		
		/**
		 * Stop the client connection to the sensor. 
		 */
		public void stopClient() {
			running = false;
		}

		/**
		 * Method getID.
		 * @return the sensor's ID
		 */
		public int getID() {
			return ID;
		}

		/**
		 * Method getAddress.
		 * @return the sensor's ip address
		 */
		public InetAddress getAddress() {
			return address;
		}

		/**
		 * Method getPort.
		 * @return the sensor's port
		 */
		public int getPort() {
			return port;
		}

		/**
		 * Method getSleeptime.
		 * @return the sensor's sleeptime
		 */
		public int getSleeptime() {
			return sleeptime;
		}

		/**
		 * Method setSleeptime.
		 * @param sleeptime the sensor's sleeptime
		 */
		public void setSleeptime(int sleeptime) {
			this.sleeptime = sleeptime;
		}
	}

	/** Thread that listen from incoming HELLO messages from the sensors
	 * that want to connect to the monitor.
 	 *
 	 * @author DSA-Project Group [Spring 2012]
 	 * @version 1.0
	 */
	private class ListeningThread extends Thread {

		private DatagramSocket datagramSocket;

		/**
		 * Constructor for ListeningThread.
		 */
		public ListeningThread() {
			try {
				datagramSocket = new DatagramSocket(port);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Method run.
		 * @see java.lang.Runnable#run()
		 */
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
					String[] command = new String(datagramPacket.getData(), 0, datagramPacket.getLength()).split("#");
					sensorID = Integer.parseInt(command[1]);
					for (SensorClient sensor : sensors) {
						if (sensor.getID() == sensorID) {
							sensor.stopClient();
							sensors.remove(sensor);
							break;
						}
					}

					// start the sensors with the default timer (10 seconds)
					bindSensor(sensorID, sensorAddress, sensorPort, 10000);

				} catch (IOException e) {
					System.out.println("Communication Error: " + sensorID);
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Thread that listens to incoming DTO from the server which are
	 * asking to change the sleeptime for a sensor.
 	 *
 	 * @author DSA-Project Group [Spring 2012]
 	 * @version 1.0
	 */
	private class UpdateSleeptimeThread extends Thread {

		private ServerSocket serverSocket;

		/**
		 * Constructor for UpdateSleeptimeThread.
		 */
		public UpdateSleeptimeThread() {
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Method run.
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			ObjectInputStream ois;
			Socket socket;
			Map<Integer, Integer> updates;
			Integer sleeptime;
			
			while (true) {
				try {
					socket = serverSocket.accept();
					ois = new ObjectInputStream(socket.getInputStream());
					updates = ((UpdateSleeptimeDTO) ois.readObject()).getUpdates();
					
					if (updates != null && !updates.isEmpty()) {
						for (SensorClient sensor : sensors) {
							int id = sensor.getID();
							sleeptime = updates.get(id);
							if (sleeptime != null) {
								sensor.setSleeptime(sleeptime);
								System.out.println("@ Sleeptime of sensor ID " + id + " changed to " + sleeptime + " ms");
							}
						}
					}
					
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}
	}
}
