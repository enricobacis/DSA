package sdu.dsa.common;

import java.io.Serializable;

/**
 * The Data Transfer Object that the sensors send to the monitor
 * to pass data.
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
public class MonitorDTO implements Serializable{

	private static final long serialVersionUID = -7670395793370067766L;
	
	int sensorID;
	long timestamp;
	float temperature;
	float humidity;

	/**
	 * Constructor for MonitorDTO.
	 * @param sensorID the sensor ID.
	 * @param timestamp the timestamp.
	 * @param temperature the temperature.
	 * @param humidity the humidity.
	 */
	public MonitorDTO(int sensorID, long timestamp, float temperature, float humidity) {
		this.sensorID = sensorID;
		this.timestamp = timestamp;
		this.temperature = temperature;
		this.humidity = humidity;
	}
	
	/**
	 * Method getSensorID.
	 * @return the ID of the sensor.
	 */
	public int getSensorID() {
		return sensorID;
	}
	
	/**
	 * Method getTimestamp.
	 * @return the timestamp of the data.
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Method getTemperature.
	 * @return the temperature of the data.
	 */
	public float getTemperature() {
		return temperature;
	}
	
	/**
	 * Method getHumidity.
	 * @return the humidity of the data.
	 */
	public float getHumidity() {
		return humidity;
	}
	
	/**
	 * Method toString.
	 * @return a string representing the object.
	 */
	@Override
	public String toString() {
		return "SensorID: " + sensorID + " temp: " + temperature + " hum: " + humidity + " [" + timestamp + "]";
	}
}
