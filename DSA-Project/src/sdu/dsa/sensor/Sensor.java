package sdu.dsa.sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;


public class Sensor extends Thread implements ISensor {

	public static final int    BASE_PORT = 5000;
	public static final double MIN_TEMP  = -20.00;
	public static final double MAX_TEMP  = 35;
	public static int lastID = 0;
	
	private int sensorID;
	private int port;
	private double lastTemperature;
	private double lastHumidity;
	private DatagramSocket datagramSocket;
	
	
	public Sensor() {
		sensorID = ++lastID;
		port = BASE_PORT + sensorID;
		lastTemperature = MIN_TEMP + (Math.random() * (MAX_TEMP - MIN_TEMP));
		lastHumidity = Math.random() * 100;
	}
	
	@Override
	public void run() {
		startServer();
	}
	
	public void startServer() {
		try {
			datagramSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.out.println("Cannot create a datagram socket for the sensor " + port);
			e.printStackTrace();
		}
		
		byte[] receiveBuffer = new byte[1024];
		
		while(true) {
			DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			try {
				datagramSocket.receive(datagramPacket);
				InetAddress clientAddress = datagramPacket.getAddress();
				int clientPort = datagramPacket.getPort();
				SendInfo(clientAddress, clientPort);
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				System.out.println("Communication Error: " + sensorID);
				e.printStackTrace();
			}
		}
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
	public int getPort() {
		return port;
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
	
	public static void main(String[] args) {
		Sensor sensor = new Sensor();
		sensor.startServer();
	}
}
