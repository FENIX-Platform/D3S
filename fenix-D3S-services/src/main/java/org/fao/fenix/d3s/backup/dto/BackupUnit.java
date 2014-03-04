package org.fao.fenix.d3s.backup.dto;

import org.fao.fenix.d3s.server.utils.JSONUtils;

import java.util.Map;

public class BackupUnit <T> {
    private T unit;
    private Class<T> unitType;
    private Map<String,Object> unitMap;

    public BackupUnit() {}
    public BackupUnit(T unit) {
        this.unit = unit;
        this.unitType = (Class<T>)unit.getClass();
    }

    public T getUnit() {
        try {
            return unit!=null ? unit: JSONUtils.toObject(JSONUtils.toJSON(unitMap), unitType);
        } catch (Exception e) {
            return null;
        }
    }
    public void setUnit(Map<String,Object> unitMap) {
        this.unitMap = unitMap;
    }
    public String getUnitType() {
        return unitType.getName();
    }
    public void setUnitType(String unitType) {
        try {
            this.unitType = (Class<T>)Class.forName(unitType);
        } catch (ClassNotFoundException e) {
            this.unitType = null;
        }
    }
}
