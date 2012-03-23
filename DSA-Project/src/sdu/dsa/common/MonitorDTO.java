package sdu.dsa.common;

public class MonitorDTO {
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
}
