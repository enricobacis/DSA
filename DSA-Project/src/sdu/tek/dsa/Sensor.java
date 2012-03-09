package sdu.tek.dsa;

public interface Sensor {
	  // return an unique identifier for this object
    public int getSensorId();

    // return a value between -20.00°C and 35.00°C
    public String getTemperature();
    
    // return a value between 0.0% and 100.0%
    public String getHumidity();
    }
