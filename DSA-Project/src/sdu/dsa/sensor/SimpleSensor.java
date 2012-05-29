package sdu.dsa.sensor;

import java.text.DecimalFormat;

/**
 * Simulation of a sensor providing weather information. The minimum temperature
 * generated is {@value #MIN_TEMP} and the maximum temperature is
 * {@value #MAX_TEMP} The initial temperature is a random temperature between
 * the {@link #MIN_TEMP} and {@link #MAX_TEMP} The temperature and humidity
 * percentage both evolve of maximum plus or minus 0.5 between to access to one
 * of those values.
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
public class SimpleSensor implements ISensor {

	/**
	 * Minimum possible temperature recordable with this sensor.
	 */
	public static final double MIN_TEMP = -20.00;
	
	/**
	 * Maximum possible temperature recordable with this sensor.
	 */
	public static final double MAX_TEMP = 35;
	
	/**
	 * Contains the sensor unique identifier.
	 */
	private int sensorId;
	
	/**
	 * Contains the last temperature provided.
	 */
	private double lastTemperature;
	
	/**
	 * Contains the last humidity percentage provided.
	 */
	private double lastHumidity;

	/**
	 * Creates a Sensor with it's unique identifier.
	 * @param sensorId the unique identifier of the sensor
	 */
	public SimpleSensor(int sensorId) {
		this.sensorId = sensorId;
		lastTemperature = MIN_TEMP + (Math.random() * (MAX_TEMP - MIN_TEMP));
		lastHumidity = Math.random() * 100;
	}

	/**
	 * @return the unique identifier of the sensor.
	 * @see sdu.dsa.sensor.ISensor#getId()
	 */
	@Override
	public int getId() {
		return sensorId;
	}

	/**
	 * @return the textual representation (format 0.00) of the current
	 *         temperature recorded by the sensor.
	 * @see sdu.dsa.sensor.ISensor#getTemperature()
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
	 *         percentage recorded by the sensor.
	 * @see sdu.dsa.sensor.ISensor#getHumidity()
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
