package org.fao.fenix.server.dto;

import java.util.HashSet;
import java.util.Set;

public class OrientStatus {
	
	private boolean initialized;
	private boolean active;
	
	
	public boolean isInitialized() {
		return initialized;
	}
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

}
