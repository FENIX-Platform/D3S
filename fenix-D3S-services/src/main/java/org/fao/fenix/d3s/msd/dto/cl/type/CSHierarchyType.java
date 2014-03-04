package org.fao.fenix.d3s.msd.dto.cl.type;


public enum CSHierarchyType {

	child("child"), parent("parent");

	private String code;
	private CSHierarchyType(String code) { this.code = code; }
	public String getCode() { return code; }
	
	public static CSHierarchyType getByCode(String code) {
		for (CSHierarchyType type : values())
			if (type.getCode().equals(code))
				return type;
		return null;
	}

}
