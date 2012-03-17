package sdu.dsa.sensor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import javax.swing.Timer;

/**
 * Implement the connection service for a sensor
 * @author gael
 *
 */
public class SensorConnector implements ISensorConnector {

	/**
	 * Contains the timeout used before re-sending a handshake packet to the monitor
	 */
	public static final int HANDSHAKE_TIMEOUT = 1000;

	/**
	 * Contains the udpSocket used to communicate with the monitor
	 */
	private DatagramSocket udpSocket;

	/**
	 * Contains the timer used to send handshake until the monitor asks something
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
	 * Construct a SensorConnector which provide communication services with a monitor
	 * @param sensor the sensor which provide the data
	 * @param monitorAdress the monitor initial address for the handshake
	 * @param monitorPort the monitor initial port for the handshake
	 */
	public SensorConnector(ISensor sensor, InetAddress monitorAdress,
			int monitorPort) {
		this.sensor = sensor;
		this.monitorAddress = monitorAdress;
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
	 * Make the handshake with the monitor and makes available
	 * the data provider service for the monitor
	 */
	@Override
	public void connect() throws SensorConnectionException {
		// Creation of a UDP Socket with a random port
		try {
			udpSocket = new DatagramSocket();
		} catch (SocketException ex) {
			throw new SensorConnectionException(
					"The connector was unable to get a UDP Socket", ex);
		}
		// Creation of the DataProvider before the connection handshake to be
		// sure not to loose the response
		new ConnectorDataProvider().start();
		// Connection handshake loop
		handshakeTimer.start();

	}

	/**
	 * Disconnect the sensor
	 */
	@Override
	public void disconnect() {
		//TODO: forge a disconnect packet, send it
		handshakeTimer.stop();
		udpSocket.close();
		udpSocket = null;
	}

	/**
	 * Forge a handshake packet and send it to the monitor to initiate communication
	 */
	private void sendConnectPacket() {
		try {
			// Creation and wrapping of the connection command in a packet
			byte[] connectPacketBuffer = wrapPacketString("connect#"
					+ sensor.getId());
			DatagramPacket connectPacket = new DatagramPacket(
					connectPacketBuffer, connectPacketBuffer.length,
					monitorAddress, monitorPort);
			// Sending the packet
			udpSocket.send(connectPacket);
		} catch (IOException ex) {
			ex.printStackTrace();// TODO: handle this in a better way
		}
	}

	/**
	 * Forge a data packet and send it back to the asking address and port (from the monitor)
	 * @param monitorAdress the monitor address asking for the data
	 * @param monitorPort the monitor port asking for the data
	 */
	private void sendDataPacket(InetAddress monitorAdress, int monitorPort) {
		try {
			//Creation and wrapping of the data command in a packet
			byte[] dataPacketBuffer = wrapPacketString("data#" + "Temperature:"
					+ sensor.getTemperature() + ",Humidity:"
					+ sensor.getHumidity() + ",Timestamp:"
					+ new Date().getTime());
			DatagramPacket dataPacket = new DatagramPacket(dataPacketBuffer,
					dataPacketBuffer.length, monitorAdress, monitorPort);
			//Sending the packet
			udpSocket.send(dataPacket);
		} catch (IOException ex) {
			ex.printStackTrace();// TODO: handle this in a better way
		}

	}

	/**
	 * Wraps a String properly in a byte array keeping the default charset
	 * @param str the string to wrap
	 * @return the byte array reprensentation of the string
	 * @throws IOException if there's a stream error
	 */
	private byte[] wrapPacketString(String str) throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		OutputStreamWriter strOutputWriter = new OutputStreamWriter(
				byteOutputStream);
		strOutputWriter.write(str);
		strOutputWriter.flush();
		strOutputWriter.close();
		return byteOutputStream.toByteArray();
	}

	/**
	 * Listener providing data to the monitor whenever it asks for it
	 * @author gael
	 *
	 */
	private class ConnectorDataProvider extends Thread {

		/**
		 * Listens for the monitor until the udpSocket is deleted
		 */
		@Override
		public void run() {
			byte[] receiveBuffer = new byte[1024];//TODO: reduce the buffer to the max size of possible commands
			while (udpSocket != null) {
				try {
					DatagramPacket datagramPacket = new DatagramPacket(
							receiveBuffer, receiveBuffer.length);
					udpSocket.receive(datagramPacket);
					handshakeTimer.stop();
					//TODO: analyze the command and trigger the action matching in order 
					// for example to stop the sensor from the monitor
					// get data from the sensor, etc ....
					sendDataPacket(datagramPacket.getAddress(),
							datagramPacket.getPort());
				} catch (IOException ex) {
					ex.printStackTrace();// TODO: handle this in a better way
				}
			}
		}
	}

}
