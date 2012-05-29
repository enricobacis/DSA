package sdu.dsa.sensor;

/**
 * Exception raised when there is a connection error with the sensor.
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
public class SensorConnectionException extends Exception {

	private static final long serialVersionUID = 1906560868511962949L;

	public SensorConnectionException() {
		super();
	}

	/**
	 * Constructor for SensorConnectionException.
	 * @param msg the exception message
	 * @param cause the Throwable object
	 */
	public SensorConnectionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Constructor for SensorConnectionException.
	 * @param msg the exception message
	 */
	public SensorConnectionException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for SensorConnectionException.
	 * @param cause the Throwable object
	 */
	public SensorConnectionException(Throwable cause) {
		super(cause);
	}

}
