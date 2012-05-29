package sdu.dsa.sensor;

/**
 * Define the functionalities that should be provided by a Sensor.
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
public interface ISensor {
	
	/**
	 * Method getId.
	 * @return the unique identifier of the ISensor
	 */
	public int getId();

	/**
	 * Method getTemperature.
	 * @return the textual representation (format 0.00) of the current temperature recorded by the ISensor.
	 */
	public String getTemperature();

	/**
	 * Method getHumidity.
	 * @return the textual representation (format 0.00) of the humidity percentage recorded by the ISensor.
	 */
	public String getHumidity();
}
