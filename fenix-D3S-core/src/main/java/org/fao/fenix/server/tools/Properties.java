package org.fao.fenix.server.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Properties extends java.util.Properties {

	private static final long serialVersionUID = 1L;
	
	public Map<String,String> toMap() {
		Map<String,String> properties = new HashMap<String,String>();
		for (Entry<Object, Object> property : entrySet())
			properties.put(property.getKey().toString(),property.getValue().toString());
		return properties;
	}	

}
