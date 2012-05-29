package sdu.dsa.sensor;

/**
 * Define the functionalities that should be provided by a SensorConnector.
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
public interface ISensorConnector {
	
	/**
	 * Connect a ISensor to the monitor.
	 * @throws SensorConnectionException
	 */
	public void connect() throws SensorConnectionException;
	
	/**
	 * Disconnect the ISensor to the monitor.
	 */
	public void disconnect();

}
