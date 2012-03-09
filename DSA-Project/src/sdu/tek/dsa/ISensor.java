package sdu.tek.dsa;

import java.net.InetAddress;

public interface ISensor {
	
	// start the startserver
	public void startServer();
	
	// return an unique identifier for this object
	public int getSensorId();
	
	// return the address for this object
	public InetAddress getAddress();
	
	// return the udp port for this object
	public int getPort();

	// return a value between -20.00C and 35.00C
	public String getTemperature();

	// return a value between 0.0% and 100.0%
	public String getHumidity();
}
