package org.fao.fenix.server.tools;

import java.util.HashMap;
import java.util.Map;

public class D3SIdentity {
		
	private String userName;
	private String applicationName;
	
	
	public D3SIdentity() {}
	public D3SIdentity(String userName, String applicationName) {
		this.userName = userName;
		this.applicationName = applicationName;
	}
	
	//GET-SET
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	
	//Utils
	public Map<String,Object> hasMap() {
		Map<String,Object> result = new HashMap<String,Object>();
		if (userName!=null)
			result.put("user", userName);
		if (applicationName!=null)
			result.put("application", applicationName);
		return result;
	}
	
	public static D3SIdentity fromMap(Map<String,Object> identityMap) {
		D3SIdentity identity = new D3SIdentity();
		if (identityMap != null) {
			identity.setUserName((String)identityMap.get("user"));
			identity.setApplicationName((String)identityMap.get("application"));
		}
		return identity;
	}
	

}
