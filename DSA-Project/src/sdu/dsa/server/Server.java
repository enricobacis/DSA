package sdu.dsa.server;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import sdu.dsa.common.MonitorDTO;

public class Server {
	
	private ServerSocket serverSocket;
    private Socket clientSocket;
	
	public Server(int port) {		
		try {
			serverSocket = new ServerSocket(port);
			clientSocket = serverSocket.accept();
			ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
			
			while (!clientSocket.isClosed()) {
				Object receivedObject = ois.readObject();
				@SuppressWarnings("unchecked")
				ArrayList<MonitorDTO> receivedData = (ArrayList<MonitorDTO>) receivedObject;
				for (MonitorDTO dto : receivedData) {
					System.out.println("##### " + dto.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
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
