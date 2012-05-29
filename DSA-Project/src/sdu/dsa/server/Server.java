package sdu.dsa.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.Timer;

import sdu.dsa.common.MonitorDTO;
import sdu.dsa.common.UpdateSleeptimeDTO;
import sdu.dsa.database.DBManager;

/**
 * The server that is used to store the monitor's data in the database and inform the
 * monitor about changes in the sleeptime.
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0 
 */
public class Server {
	
	private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream ois;
    private boolean running;
    
    private static final int SLEEPTIME_UPDATES_TIMEOUT = 5000;
	
	/**
	 * Constructor for Server.
	 * @param port server port
	 * @param monitorIp monitor ip address
	 * @param monitorPort monitor port
	 */
	public Server(int port, final InetAddress monitorIp, final int monitorPort) {
		try {
			running = true;
			serverSocket = new ServerSocket(port);			
			
			// Inform the database to send all the sleeptimes to the monitor
			DBManager.initializeSleeptimeUpdates();
			
			Timer sleeptimeUpdatesTimer = new Timer(SLEEPTIME_UPDATES_TIMEOUT, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent event) {
					Map<Integer, Integer> updates = DBManager.flushSleeptimeUpdates();

					if (!updates.isEmpty()) {
						System.out.println("## " + updates.size() + " Sleeptime Updates Found ##");
						UpdateSleeptimeDTO dto = new UpdateSleeptimeDTO(updates);
						try {
							Socket sendingSocket = new Socket(monitorIp, monitorPort);
							ObjectOutputStream oos = new ObjectOutputStream(sendingSocket.getOutputStream());
							oos.writeObject(dto);
							sendingSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			
			sleeptimeUpdatesTimer.start();
			
			System.out.println("Server Started");
			
			while (running) {
				clientSocket = serverSocket.accept();
				ois = new ObjectInputStream(clientSocket.getInputStream());
				Object receivedObject = ois.readObject();
				
				@SuppressWarnings("unchecked")
				ArrayList<MonitorDTO> receivedData = (ArrayList<MonitorDTO>) receivedObject;
				System.out.println("## DB Store: " + receivedData.size() + " records received ##");
				DBManager.storeData(receivedData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method finalize.
	 * @throws Throwable
	 */
	@Override
	protected void finalize() throws Throwable {
		running = false;
		if (!serverSocket.isClosed())
			serverSocket.close();
		super.finalize();
	}
	
	/**
	 * Method main.
	 * @param args the starting arguments in the format: monitor_ip monitor_port [port = 6000]
	 */
	public static void main(String[] args) {
		if (args.length > 3) {
			printError();
		} else {
			try {
				InetAddress monitorIp = InetAddress.getByName(args[0]);
				int monitorPort = Integer.parseInt(args[1]);
				
				int port = 6000;
				if (args.length == 3)
					port = Integer.parseInt(args[2]);
				
				System.out.println("Starting Server...");
				new Server(port, monitorIp, monitorPort);
				
			} catch (Exception e) {
				printError();
			}
		}
	}

	/**
	 * Print the errors if the main method is called with wrong parameters.
	 */
	private static void printError() {
		System.out.println("Usage: Server monitor_ip monitor_port [port = 6000]");
	}
}
