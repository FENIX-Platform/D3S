package org.fao.fenix.msd.dto.dsd.type;

import java.io.Serializable;

public enum DSDDataType implements Serializable{
    enumeration("enumeration"), number("number"), text("text"), iText("iText"), code("code"), customCode("customCode"), document("document"), date("date"), month("month"), year("year"), period("period");
	private String dbCode;
	private DSDDataType(String code) { this.dbCode = code; }
	public String getCode() { return dbCode; }

	//Utils
	public static DSDDataType getByCode(String code) {
        if (code!=null)
            for (DSDDataType type : values())
                if (type.getCode().equals(code))
                    return type;
		return null;
	}
}
