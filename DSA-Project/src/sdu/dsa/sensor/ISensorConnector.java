package sdu.dsa.sensor;

/**
 * Define the functionalities that should be provided by a SensorConnector
 * 
 */
public interface ISensorConnector {
	
	/**
	 * Connect a ISensor to the monitor
	 */
	public void connect() throws SensorConnectionException;
	
	/**
	 * Disconnect the ISensor to the monitor
	 */
	public void disconnect();

}
