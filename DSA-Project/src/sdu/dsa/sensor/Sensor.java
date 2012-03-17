package sdu.dsa.sensor;

import java.text.DecimalFormat;

public class Sensor implements ISensor {

	/**
	 * Mininum possible temperature recorded with this sensor
	 */
	public static final double MIN_TEMP = -20.00;
	/**
	 * Maximum possible temperature recorded with this sensor
	 */
	public static final double MAX_TEMP = 35;
	/**
	 * Contains the sensor unique identifier
	 */
	private int sensorId;
	/**
	 * Contains the last temperature provided
	 */
	private double lastTemperature;
	/**
	 * Contains the last humidity percentage provided
	 */
	private double lastHumidity;

	/**
	 * Creates a Sensor with it's unique identifier
	 * 
	 * @param sensorId
	 *            the unique identifier of the sensor
	 */
	public Sensor(int sensorId) {
		this.sensorId = sensorId;
		lastTemperature = MIN_TEMP + (Math.random() * (MAX_TEMP - MIN_TEMP));
		lastHumidity = Math.random() * 100;
	}

	/**
	 * @return the unique identifier of the sensor
	 */
	@Override
	public int getId() {
		return sensorId;
	}

	/**
	 * @return the textual representation (format 0.00) of the current
	 *         temperature recorded by the sensor
	 */
	@Override
	public String getTemperature() {
		// We evolve of max +0.5 or -0.5 degree
		double nextTemperature = lastTemperature + Math.random() - 0.5;
		// We stay in the defined bounds
		nextTemperature = nextTemperature < MIN_TEMP
				|| nextTemperature > MAX_TEMP ? lastTemperature
				: nextTemperature;

		lastTemperature = nextTemperature;
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(nextTemperature);
	}

	/**
	 * @return the textual representation (format 0.00) of the humidity
	 *         percentage recorded by the sensor
	 */
	@Override
	public String getHumidity() {
		// We evolve of max +0.5 or -0.5 %
		double nextHumidity = lastHumidity + Math.random() - 0.5;
		// We stay in the defined bounds
		nextHumidity = nextHumidity < 0 || nextHumidity > 100 ? lastHumidity
				: nextHumidity;

		lastHumidity = nextHumidity;
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(nextHumidity);
	}

}
