package sdu.tek.dsa;

import java.text.DecimalFormat;

public class Sensor implements ISensor {

	public static final int    BASE_PORT = 4000;
	public static final double MIN_TEMP  = -20.00;
	public static final double MAX_TEMP  = 35;
	public static int lastID = 0;
	
	private int sensorID;
	private int port;
	private double lastTemperature;
	private double lastHumidity;
	
	public Sensor() {
		sensorID = ++lastID;
		port = BASE_PORT + sensorID;
		lastTemperature = MIN_TEMP + (Math.random() * (MAX_TEMP - MIN_TEMP));
		lastHumidity = Math.random() * 100;
	}
	
	@Override
	public int getSensorId() {
		return sensorID;
	}
	
	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public String getTemperature() {
		double temperature = lastTemperature + Math.random() - 0.5;
		
		if (temperature < MIN_TEMP)
			temperature = MIN_TEMP;
		else if (temperature > MAX_TEMP)
			temperature = MAX_TEMP;
		
		lastTemperature = temperature;
		
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(temperature);
	}
	
	@Override
	public String getHumidity() {
		double humidity = lastHumidity + Math.random() - 0.5;
		
		if (humidity < MIN_TEMP)
			humidity = MIN_TEMP;
		else if (humidity > MAX_TEMP)
			humidity = MAX_TEMP;
		
		lastHumidity = humidity;
		
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(humidity); 
	}
}
