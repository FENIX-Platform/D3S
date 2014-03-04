package org.fao.fenix.d3s.msd.dto.dm.type;

import java.io.Serializable;

public enum DMPeriodicityType implements Serializable {
	
	annually("annually"), monthly("monthly"), dayly("dayly"), weekly("weekly"), unknown("unknown");
	private String code;
	private DMPeriodicityType(String code) { this.code = code; }
	public String getCode() { return code; }

	//Utils
	public static DMPeriodicityType getByCode(String code) {
		for (DMPeriodicityType type : values())
			if (type.getCode().equals(code))
				return type;
		return null;
	}
}
