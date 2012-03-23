package sdu.dsa.monitor;

/**
 * Contains the differents monitors, receive the data from them and
 * use the server connectors to send the data to the servers registered
 * or receive command from them.
 * @author gael
 *
 */
public interface IMonitorManager {
	
	public void start();
	
	public void stop();
	
	public void addMonitor(IMonitor monitor);
	
	public void removeMonitor(IMonitor monitor);
	
	public void addServerConnector(IServerConnector connector);
	
	public void removeServerConnector(IServerConnector connector);
	
	public void receiveData(DataTransfertObject data);
	
	public void receiveCommand(CommandTransfertObject command);
	
	public void setPollingDelay(int delay);

}
