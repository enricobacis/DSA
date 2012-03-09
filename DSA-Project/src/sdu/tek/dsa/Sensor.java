package sdu.tek.dsa;

import java.text.DecimalFormat;

public class Sensor implements ISensor {

	public static final double MIN_TEMP = -20.00;
	public static final double MAX_TEMP = 35;
	
	private int sensorID;
	public Sensor(int sensorID) {
		
		this.sensorID = sensorID;
	}
	
	public int getSensorId() {
		
		return sensorID;
	}

	public String getTemperature() {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(MIN_TEMP + (Math.random() * ((MAX_TEMP - MIN_TEMP) + 1))); 
	}

	public String getHumidity() {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format((0 + (Math.random() * ((100 - 0) + 1)))); 
	}

}
