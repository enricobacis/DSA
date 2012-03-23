package sdu.dsa.monitor;

import java.util.Date;

/**
 * Wrapper to send the data through the application
 * @author gael
 * 
 */
public class DataTransfertObject {

	private int temperature;

	private int humidity;

	private Date timestamp;

	public DataTransfertObject(int temperature, int humidity, Date timestamp) {
		super();
		this.temperature = temperature;
		this.humidity = humidity;
		this.timestamp = timestamp;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
