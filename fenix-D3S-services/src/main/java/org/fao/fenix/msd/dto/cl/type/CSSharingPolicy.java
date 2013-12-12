package org.fao.fenix.msd.dto.cl.type;


public enum CSSharingPolicy {
	
	publicPolicy("public"), privatePolicy("private"), sharedPolicy("shared");
	
	private String code;
	private CSSharingPolicy(String code) { this.code = code; }
	public String getCode() { return code; }
	
	public static CSSharingPolicy getByCode(String code) {
		for (CSSharingPolicy type : values())
			if (type.getCode().equals(code))
				return type;
		return null;
	}

}
