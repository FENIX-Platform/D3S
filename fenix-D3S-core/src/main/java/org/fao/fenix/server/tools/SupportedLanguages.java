package org.fao.fenix.server.tools;

public enum SupportedLanguages {
	
	english("EN"), spanish("ES"), france("FR"), detush("DE");
	
	private String code;
	private SupportedLanguages(String code) { this.code = code; }
	
	public String getCode() { return code; }
	

}
