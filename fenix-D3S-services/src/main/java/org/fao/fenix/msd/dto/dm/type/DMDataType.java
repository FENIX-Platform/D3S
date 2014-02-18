package org.fao.fenix.msd.dto.dm.type;

import java.io.Serializable;

public enum DMDataType implements Serializable {

	dataset("dataset"), layer("layer");
/*	production("production"), trade("trade"), foodSupply("foodSupply"), prices(
			"prices"), landAndIrrigation("landAndIrrigation"), population(
			"population"), labor("labor"), fertilizers("fertilizers"), pesticides(
			"pesticides"), machinery("machinery"), forestry("forestry"), fisheries(
			"fisheries"), water("water"), valueAdded("valueAdded"), commodityBalances(
			"commodityBalances"), foodBalanceSheets("foodBalanceSheets"), resources(
			"resources"), investment("investment"), emissionsAgriculture(
			"emissionsAgriculture"), emissionsLandUse("emissionsLandUse");
*/
    private String code;

	private DMDataType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	// Utils
	public static DMDataType getByCode(String code) {
		for (DMDataType type : values())
			if (type.getCode().equals(code))
				return type;
		return null;
	}
}
