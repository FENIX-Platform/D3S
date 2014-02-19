package org.fao.fenix.msd.dto.dm.type;

import java.io.Serializable;

public enum DMLayerType implements Serializable {

	vector("vector"), raster("raster");

    private String code;

	private DMLayerType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	// Utils
	public static DMLayerType getByCode(String code) {
		for (DMLayerType type : values())
			if (type.getCode().equals(code))
				return type;
		return null;
	}
}
