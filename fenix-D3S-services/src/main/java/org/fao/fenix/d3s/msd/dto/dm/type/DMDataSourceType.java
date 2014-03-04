package org.fao.fenix.d3s.msd.dto.dm.type;

public enum DMDataSourceType {

    bullettin ("bullettins"), cd("CD-Rom"), yearbook("yearbook"), database("db"), rest("restService"), soap("soapService"), xml("xml"), pdf("pdf"), publication("publication"), other("other");
    private String code;
    private DMDataSourceType(String code) { this.code = code; }
    public String getCode() { return code; }

    //Utils
    public static DMDataSourceType getByCode(String code) {
        for (DMDataSourceType type : values())
            if (type.getCode().equals(code))
                return type;
        return null;
    }
}
