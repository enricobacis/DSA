package sdu.dsa.monitor;

/**
 * Manage the connection with a sensor, poll the data, send command....
 * and provide the data to the monitor
 * @author gael
 *
 */
public interface ISensorHandler {
	
	public void startPolling();
	
	public void disconnect();
	
	public void addMonitorListener(IMonitor monitor);
	
	public void removeMonitorListener(IMonitor monitor);
	
	public void setPollingDelay(int delay);

}
