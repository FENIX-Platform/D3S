package org.fao.fenix.cl.dto;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dsd.type.DSDDataType;

public class Value {
	
	private Object value;
	private DSDDataType dataType;
	private Code unitOfMeasure;
	
	public Value() {}
	public Value(Object value, DSDDataType dataType, Code unitOfMeasure) {
		this.value = value;
		this.dataType = dataType;
		this.unitOfMeasure = unitOfMeasure;
	}



	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public DSDDataType getDataType() {
		return dataType;
	}
	public void setDataType(DSDDataType dataType) {
		this.dataType = dataType;
	}
	public Code getUnitOfMeasure() {
		return unitOfMeasure;
	}
	public void setUnitOfMeasure(Code unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}
	
	
	

}
