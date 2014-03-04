package org.fao.fenix.d3s.msd.dto.dm.type;


public enum DMStatus {
	incomplete("incomplete"), //Dataset available for normal usage
	available("available"), //Dataset available for normal usage
	toUpload("upload"), //Dataset have to be uploaded by using D3S
	toStore("store"); //Dataset have to be stored
	
	private String code;
	private DMStatus(String code) { this.code = code; }
	public String getCode() { return code; }

	//Utils
	public static DMStatus getByCode(String code) {
		for (DMStatus type : values())
			if (type.getCode().equals(code))
				return type;
		return null;
	}


}
