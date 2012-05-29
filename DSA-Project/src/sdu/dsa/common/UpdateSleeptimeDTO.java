package sdu.dsa.common;

import java.io.Serializable;
import java.util.Map;

/** Data Transfer Object from the server to the monitor to reflect
 * some sleeptime change in the database (when a user change the
 * sleeptime from the website).
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
public class UpdateSleeptimeDTO implements Serializable {

	private static final long serialVersionUID = 5568097353655081959L;
	
	Map<Integer, Integer> updates;

	/**
	 * Constructor for UpdateSleeptimeDTO.
	 * @param updates the updates.
	 */
	public UpdateSleeptimeDTO(Map<Integer, Integer> updates) {
		this.updates = updates;
	}

	/**
	 * Method getUpdates.
	 * @return the updates' map.
	 */
	public Map<Integer, Integer> getUpdates() {
		return updates;
	}

	/**
	 * Method setUpdates.
	 * @param updates the updates' map.
	 */
	public void setUpdates(Map<Integer, Integer> updates) {
		this.updates = updates;
	}
	
}
