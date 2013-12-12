package org.fao.fenix.cl.dto;

import org.fao.fenix.msd.dto.cl.Code;

public class ConversionParameters {
	
	private Code unitOfMeasure;
	private Value value;
	
	
	public Code getUnitOfMeasure() {
		return unitOfMeasure;
	}
	public void setUnitOfMeasure(Code unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}
	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	
	

}
