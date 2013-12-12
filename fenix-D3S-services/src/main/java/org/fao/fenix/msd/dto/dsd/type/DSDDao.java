package org.fao.fenix.msd.dto.dsd.type;

import java.io.Serializable;

public enum DSDDao implements Serializable{
	cstat("CountrySTAT"), faostat("FAOSTAT");
	
	private String dbCode;
	private DSDDao(String code) { this.dbCode = code; }
	public String getCode() { return dbCode; }

    //Utils
	public static DSDDao getByCode(String code) {
		for (DSDDao type : values())
			if (type.getCode().equals(code))
				return type;
		return null;
	}
}
