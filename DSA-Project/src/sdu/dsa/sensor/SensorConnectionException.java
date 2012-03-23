package sdu.dsa.sensor;

/**
 * Exception raised when there is a connection error with the sensor
 * 
 * @author gael
 * 
 */
public class SensorConnectionException extends Exception {

	private static final long serialVersionUID = 1906560868511962949L;

	public SensorConnectionException() {
		super();
	}

	public SensorConnectionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SensorConnectionException(String msg) {
		super(msg);
	}

	public SensorConnectionException(Throwable cause) {
		super(cause);
	}

}
