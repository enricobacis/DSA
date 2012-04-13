package sdu.dsa.common;

import java.io.Serializable;

public class MonitorDTO implements Serializable{

	private static final long serialVersionUID = -7670395793370067766L;
	
	int sensorID;
	long timestamp;
	float temperature;
	float humidity;

	public MonitorDTO(int sensorID, long timestamp, float temperature, float humidity) {
		this.sensorID = sensorID;
		this.timestamp = timestamp;
		this.temperature = temperature;
		this.humidity = humidity;
	}
	
	public int getSensorID() {
		return sensorID;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public float getTemperature() {
		return temperature;
	}
	
	public float getHumidity() {
		return humidity;
	}
	
	@Override
	public String toString() {
		return "SensorID: " + sensorID + " temp: " + temperature + " hum: " + humidity + " [" + timestamp + "]";
	}
}
