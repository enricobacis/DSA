package sdu.tek.dsa;

public interface ISensor {
	
	// return an unique identifier for this object
	public int getSensorId();
	
	// return the udp port for this object
	public int getPort();

	// return a value between -20.00C and 35.00C
	public String getTemperature();

	// return a value between 0.0% and 100.0%
	public String getHumidity();
}
