package org.fao.fenix.msd.dto.dm.type;

import java.io.Serializable;

public enum DMDataKind implements Serializable {
	
	//d1("dataKind1"), d2("dataKind2");
	survey("survey"), census("census"), administrativeRecords("administrativeRecords"), automated("automated");
	private String code;
	private DMDataKind(String code) { this.code = code; }
	public String getCode() { return code; }

	//Utils
	public static DMDataKind getByCode(String code) {
		for (DMDataKind type : values())
			if (type.getCode().equals(code))
				return type;
		return null;
	}
}
