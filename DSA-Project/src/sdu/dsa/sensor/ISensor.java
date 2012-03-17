package sdu.dsa.sensor;

/**
 * Define the functionalities that should be provided by a Sensor
 */
public interface ISensor {
	
	/**
	 * @return the unique identifier of the ISensor
	 */
	public int getId();

	/**
	 * @return the textual representation (format 0.00) of the current temperature recorded by the ISensor
	 */
	public String getTemperature();

	/**
	 * @return the textual representation (format 0.00) of the humidity percentage recorded by the ISensor
	 */
	public String getHumidity();
}
