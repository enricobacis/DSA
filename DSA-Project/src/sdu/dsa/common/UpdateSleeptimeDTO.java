package sdu.dsa.common;

import java.io.Serializable;
import java.util.Map;

public class UpdateSleeptimeDTO implements Serializable {

	private static final long serialVersionUID = 5568097353655081959L;
	
	Map<Integer, Integer> updates;

	public UpdateSleeptimeDTO(Map<Integer, Integer> updates) {
		this.updates = updates;
	}

	public Map<Integer, Integer> getUpdates() {
		return updates;
	}

	public void setUpdates(Map<Integer, Integer> updates) {
		this.updates = updates;
	}
	
}
