package sdu.dsa.sensor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import javax.swing.Timer;


public class Sensor implements ISensor {
	
	public static final double MIN_TEMP  = -20.00;
	public static final double MAX_TEMP  = 35;
	
	private int sensorID;
	private double lastTemperature;
	private double lastHumidity;
	private DatagramSocket datagramSocket;
	private InetAddress monitorAddress;
	private int monitorPort;
	private ListeningThread listeningThread;
	private Timer handshakeTimer;
	
	public Sensor(int sensorID, InetAddress monitorAddress, int monitorPort) {
		this.sensorID = sensorID;
		this.monitorAddress = monitorAddress;
		this.monitorPort = monitorPort;
		
		lastTemperature = MIN_TEMP + (Math.random() * (MAX_TEMP - MIN_TEMP));
		lastHumidity = Math.random() * 100;
		
		startServer();
	}
	
	public void startServer() {
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("Cannot create a datagram socket for the sensor " + sensorID);
			e.printStackTrace();
		}
		
		listeningThread = new ListeningThread();
		listeningThread.start();
		
		// Handshake
		final byte[] sensorInitialization = Integer.toString(sensorID).getBytes();
		handshakeTimer = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DatagramPacket datagramPacket = new DatagramPacket(sensorInitialization,
						sensorInitialization.length, monitorAddress, monitorPort);
				try {
					datagramSocket.send(datagramPacket);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		handshakeTimer.setInitialDelay(0);
		handshakeTimer.start();
	}
	
	@Override
	public int getSensorId() {
		return sensorID;
	}
	
	@Override
	public InetAddress getAddress() {
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	@Override
	public String getTemperature() {
		double temperature = lastTemperature + Math.random() - 0.5;
		
		if (temperature < MIN_TEMP)
			temperature = MIN_TEMP;
		else if (temperature > MAX_TEMP)
			temperature = MAX_TEMP;
		
		lastTemperature = temperature;
		
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(temperature);
	}
	
	@Override
	public String getHumidity() {
		double humidity = lastHumidity + Math.random() - 0.5;
		
		if (humidity < MIN_TEMP)
			humidity = MIN_TEMP;
		else if (humidity > MAX_TEMP)
			humidity = MAX_TEMP;
		
		lastHumidity = humidity;
		
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(humidity); 
	}
	
	public void SendInfo(InetAddress clientAddress, int clientPort) throws IOException
	{
		String info = "Temperature: " + getTemperature() + " Humidity: " + getHumidity();
		DatagramPacket infoPacket = new DatagramPacket(info.getBytes(), info.getBytes().length, clientAddress, clientPort);
		datagramSocket.send(infoPacket);
	}
	
	private static void printError() {
		System.out.println("Usage: Sensor sensor_id monitor_ip monitor_port");
	}
	
	public static void main(String[] args) {
		if (args.length != 3) {
			printError();
			return;
		}
		
		int sensorID;
		InetAddress monitorAddress;
		int monitorPort;
		
		try {
			sensorID = Integer.parseInt(args[0]);
			monitorAddress = InetAddress.getByName(args[1]);
			monitorPort = Integer.parseInt(args[2]);
		} catch (Exception e) {
			printError();
			return;
		}
		
		new Sensor(sensorID, monitorAddress, monitorPort);
	}
	
	private class ListeningThread extends Thread {
		
		@Override
		public void run() {
			byte[] receiveBuffer = new byte[1024];
			while(true) {
				DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				try {
					datagramSocket.receive(datagramPacket);
					handshakeTimer.stop();
					InetAddress clientAddress = datagramPacket.getAddress();
					int clientPort = datagramPacket.getPort();
					SendInfo(clientAddress, clientPort);
				} catch (IOException e) {
					System.out.println("Communication Error: " + sensorID);
					e.printStackTrace();
				}
			}
		}
	}
}
