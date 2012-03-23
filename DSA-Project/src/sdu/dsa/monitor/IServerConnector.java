package sdu.dsa.monitor;

/**
 * Handle the 
 * @author gael
 *
 */
public interface IServerConnector {
	
	public void sendData(DataTransfertObject data);
	
	public void addManagerListener(IMonitorManager manager);
	
	public void removeManagerListener(IMonitorManager manager);

}
