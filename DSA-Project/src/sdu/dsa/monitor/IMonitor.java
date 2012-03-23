package sdu.dsa.monitor;

/**
 * Manage the sensors connected with a certain technology, retrieve the data
 * from them and send it to the MonitorManager so it can process the data
 * 
 * @author gael
 * 
 */
public interface IMonitor {

	public void start();

	public void stop();

	public void addManagerListener(IMonitorManager manager);

	public void removeManagerListener(IMonitorManager manager);

	public void receiveData(DataTransfertObject data);

	public void setPollingDelay(int delay);

}
