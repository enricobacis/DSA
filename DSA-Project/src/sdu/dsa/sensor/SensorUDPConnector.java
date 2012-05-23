package sdu.dsa.sensor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import javax.swing.Timer;
import sdu.dsa.common.SensorCommand;

/**
 * Implement the connection service for a sensor. When the connect method is
 * triggered, the connector sends a handshake packet to the monitor specified in
 * the constructor to warn it that it's available. If the monitor don't send any
 * command after the {@link #HANDSHAKE_TIMEOUT} it will continue sending
 * handshake packet until the first command is received. When a command is
 * received, it's executed in a separated thread to continue listening to the
 * monitor sending commands. If no command is received after the
 * {@link #SILENCE_TIMEOUT} it consider that the monitor may have lost the
 * sensor so the handshake process starts again. When the connector disconnect,
 * it sends a disconnect packet to the monitor. The disconnection of the
 * connector can also be triggered by the monitor.
 * 
 * @author gael
 * 
 */
public class SensorUDPConnector implements ISensorConnector {

	/**
	 * Contains the timeout used before re-sending a handshake packet to the
	 * monitor
	 */
	public static final int HANDSHAKE_TIMEOUT = 1000;
	/**
	 * Contains the timeout used before re-starting the handshake process if no
	 * data is asked by any monitor.
	 */
	public static final int SILENCE_TIMEOUT = 60000;

	/**
	 * Contains the udpSocket used to communicate with the monitor
	 */
	private DatagramSocket udpSocket;

	/**
	 * Contains the timer used to send handshake until the monitor asks
	 * something
	 */
	private Timer handshakeTimer;

	/**
	 * Contains the sensor which provide the data
	 */
	private ISensor sensor;

	/**
	 * Contains the monitor initial address for the handshake
	 */
	private InetAddress monitorAddress;

	/**
	 * Contains the monitor initial port for the handshake
	 */
	private int monitorPort;

	/**
	 * Construct a SensorConnector which provide communication services with a
	 * monitor
	 * 
	 * @param sensor
	 *            the sensor which provide the data
	 * @param monitorAddress
	 *            the monitor initial address for the handshake
	 * @param monitorPort
	 *            the monitor initial port for the handshake
	 */
	public SensorUDPConnector(ISensor sensor, InetAddress monitorAddress,
			int monitorPort) {
		this.sensor = sensor;
		this.monitorAddress = monitorAddress;
		this.monitorPort = monitorPort;
		// Timer construction for use when performing a connection handshake
		handshakeTimer = new Timer(HANDSHAKE_TIMEOUT, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendConnectPacket();
			}
		});
		handshakeTimer.setInitialDelay(0);
	}

	/**
	 * Make the handshake with the monitor and makes available the data provider
	 * service for the monitor
	 */
	@Override
	public void connect() throws SensorConnectionException {
		if (udpSocket == null) {
			// Creation of a UDP Socket with a random port
			try {
				udpSocket = new DatagramSocket();
				udpSocket.setSoTimeout(SILENCE_TIMEOUT);
			} catch (SocketException ex) {
				throw new SensorConnectionException(
						"The connector was unable to get a UDP Socket", ex);
			}
			// Creation of the DataProvider before the connection handshake to
			// be
			// sure not to loose the response
			new ConnectorListener().start();
			// Connection handshake loop
			handshakeTimer.start();
		}

	}

	/**
	 * Disconnect the sensor
	 */
	@Override
	public void disconnect() {
		if (udpSocket != null) {
			handshakeTimer.stop();
			sendDisconnectPacket();
			udpSocket.close();
			udpSocket = null;
		}
	}

	/**
	 * Forge a handshake packet and send it to the monitor to end communication
	 */
	private void sendDisconnectPacket() {
		String strPacket = "disconnect#" + sensor.getId();
		sendStringPacket(strPacket, monitorAddress, monitorPort);
	}

	/**
	 * Forge a handshake packet and send it to the monitor to initiate
	 * communication
	 */
	private void sendConnectPacket() {
		String strPacket = "connect#" + sensor.getId();
		sendStringPacket(strPacket, monitorAddress, monitorPort);
	}

	/**
	 * Forge a data packet and send it back to the asking address and port (from
	 * the monitor)
	 * 
	 * @param monitorAddress
	 *            the monitor address asking for the data
	 * @param monitorPort
	 *            the monitor port asking for the data
	 */
	private void sendDataPacket(InetAddress monitorAddress, int monitorPort) {
		String strPacket = "data#" + "temperature=" + sensor.getTemperature()
				+ ",humidity=" + sensor.getHumidity() + ",timestamp="
				+ new Date().getTime() + "#";
		sendStringPacket(strPacket, monitorAddress, monitorPort);
	}

	/**
	 * Send a string packet at the specified address and port
	 * 
	 * @param strToSend
	 *            the string that the packet will contain
	 * @param address
	 *            the address to send the packet to
	 * @param port
	 *            the port to send the packet to
	 */
	private void sendStringPacket(String strToSend, InetAddress address,
			int port) {
		try {
			// Creation and wrapping of the string in a packet
			byte[] packetBuffer = strToSend.getBytes();
			DatagramPacket dataPacket = new DatagramPacket(packetBuffer,
					packetBuffer.length, address, port);
			// Sending the packet
			udpSocket.send(dataPacket);
		} catch (IOException ex) {
			ex.printStackTrace();// TODO: handle this in a better way
		}

	}

	/**
	 * Called by the garbage collector to perform disconnection before the
	 * connector is erased from memory
	 */
	@Override
	protected void finalize() throws Throwable {
		disconnect();
		super.finalize();
	}

	/**
	 * Listener providing data to the monitor whenever it asks for it
	 * 
	 * @author gael
	 * 
	 */
	private class ConnectorListener extends Thread {

		/**
		 * Listens for the monitor until the udpSocket is deleted
		 */
		@Override
		public void run() {
			byte[] receiveBuffer = new byte[65507];
			while (udpSocket != null) {
				try {
					// We wait to receive a packet within the SILENCE_TIMEOUT
					DatagramPacket datagramPacket = new DatagramPacket(
							receiveBuffer, receiveBuffer.length);
					udpSocket.receive(datagramPacket);
					// If the handshake process was running, it is stopped
					handshakeTimer.stop();
					// We retrieve the command from the packet and build it's
					// enum equivalence
					SensorCommand command = SensorCommand.valueOf(new String(datagramPacket
							.getData(), 0, datagramPacket.getLength())
							.toUpperCase());
					// The command will be handled by another thread so we can
					// immediately listen to other commands
					new ConnectorCommandExecutor(command,
							datagramPacket.getAddress(),
							datagramPacket.getPort()).start();
				} catch (IllegalArgumentException ex) {
					// The command is not recognized, we don't do anything
				} catch (SocketTimeoutException ex) {
					// If we don't receive any demand of data for a long time
					// Maybe the monitor lost us so we restart the handshake
					// process to reconnect to it
					handshakeTimer.start();
				} catch (IOException ex) {
					ex.printStackTrace();// TODO: handle this in a better way
				}
			}
		}
	}

	/**
	 * Execute a command in the list of recognized commands for this sensor
	 * 
	 * @author gael
	 * 
	 */
	private class ConnectorCommandExecutor extends Thread {

		/**
		 * The command name
		 */
		SensorCommand command;

		/**
		 * The address of the monitor asking for the command to be executed
		 */
		InetAddress monitorAddress;

		/**
		 * The port of the monitor asking for the command to be executed
		 */
		int monitorPort;

		/**
		 * Construct a ConnectorCommandExecutor ready to execute the command
		 * passed to it
		 * 
		 * @param command
		 *            the command name
		 * @param monitorAdress
		 *            the address of the monitor asking for the command to be
		 *            executed
		 * @param monitorPort
		 *            the port of the monitor asking for the command to be
		 *            executed
		 */
		public ConnectorCommandExecutor(SensorCommand command,
				InetAddress monitorAdress, int monitorPort) {
			this.command = command;
			this.monitorAddress = monitorAdress;
			this.monitorPort = monitorPort;
		}

		/**
		 * Run the command
		 */
		@Override
		public void run() {
			switch (command) {
			case DATA:
				sendDataPacket(monitorAddress, monitorPort);
				break;
			case DISCONNECT:
				disconnect();
				break;
			}
		}

	}

}
