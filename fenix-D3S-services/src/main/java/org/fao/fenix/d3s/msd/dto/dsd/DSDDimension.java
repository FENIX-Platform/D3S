package org.fao.fenix.d3s.msd.dto.dsd;

import java.util.HashMap;
import java.util.Map;

public class DSDDimension {
	public static final DSDDimension VALUE_DIMENSION = new DSDDimension("VALUE");
	public static final DSDDimension ELEMENT_DIMENSION = new DSDDimension("ELEMENT");

	private String name;
	private Map<String,String> title;

	public DSDDimension() {}
	public DSDDimension(String name) { this.name = name; }
	
	//GET-SET
	public Map<String, String> getTitle() {
		return title;
	}
	public void setTitle(Map<String, String> title) {
		this.title = title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	//Utils
	public void addTitle(String language, String title) {
        if (this.title==null)
            this.title = new HashMap<String, String>();
		this.title.put(language, title);
	}
	
	@Override public boolean equals(Object obj) { return obj!=null && obj instanceof DSDDimension && name!=null && name.equals(((DSDDimension)obj).name); }
}
