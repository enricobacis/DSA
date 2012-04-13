package sdu.dsa.monitor;

import sdu.dsa.common.MonitorDTO;

/**
 * Handle the 
 * @author gael
 *
 */
public interface IServerConnector {
	
	public void sendData(MonitorDTO data);
	
	public void addManagerListener(IMonitorManager manager);
	
	public void removeManagerListener(IMonitorManager manager);

}
