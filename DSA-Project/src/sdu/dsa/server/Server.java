package sdu.dsa.server;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import sdu.dsa.common.MonitorDTO;
import sdu.dsa.database.DBManager;

public class Server {
	
	private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream ois;
    private boolean running;
	
	public Server(int port) {
		try {
			running = true;
			serverSocket = new ServerSocket(port);
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
	
	@Override
	protected void finalize() throws Throwable {
		running = false;
		if (!serverSocket.isClosed())
			serverSocket.close();
		super.finalize();
	}
	
	public static void main(String[] args) {
		if (args.length > 1) {
			printError();
		} else {
			try {				
				int port = 6000;
				if (args.length == 1)
					port = Integer.parseInt(args[0]);	
				
				new Server(port);
				
			} catch (Exception e) {
				printError();
			}
		}
	}

	private static void printError() {
		System.out.println("Usage: Server [port = 6000]");
	}
}
