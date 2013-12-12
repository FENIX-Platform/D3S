package org.fao.fenix.cl.conversion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dsd.type.DSDDataType;

public abstract class Converter {
	
	private Code unitOfMeasureFrom;
	private Code unitOfMeasureTo;
	private DSDDataType dataType;
	
	//Values dimension names includes element name
	public final void init(String rule, DSDDataType dataType, Code unitOfMeasureFrom, Code unitOfMeasureTo, Map<String,Object> fixedParameters, Map<String,String> dynamicParameters, Collection<String> orderedRowDimensions) {
		Map<String,Integer> dynamicParametersIndex = new HashMap<String, Integer>();
		if (dynamicParameters!=null && orderedRowDimensions!=null) {
			Map<String,String> dynamicParametersInversion = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : dynamicParameters.entrySet())
				dynamicParametersInversion.put(entry.getValue(), entry.getKey());
			
			int index = 0;
			for (String dimension : orderedRowDimensions) {
				if (dynamicParametersInversion.containsKey(dimension))
					dynamicParametersIndex.put(dynamicParametersInversion.get(dimension), index);
				index++;
			}
		}
		
		init(rule, dataType, unitOfMeasureFrom, unitOfMeasureTo, fixedParameters, dynamicParametersIndex);
	}
	
	
	protected abstract void init(String rule, DSDDataType dataType, Code unitOfMeasureFrom, Code unitOfMeasureTo, Map<String,Object> fixedParameters, Map<String,Integer> dynamicParametersIndex);
	
	public abstract Object doConversion(Object value, Object[] row, Map<String,Object> systemParameters);


	
	//Utils
	public Code getUnitOfMeasureFrom() {
		return unitOfMeasureFrom;
	}
	public void setUnitOfMeasureFrom(Code unitOfMeasureFrom) {
		this.unitOfMeasureFrom = unitOfMeasureFrom;
	}
	public Code getUnitOfMeasureTo() {
		return unitOfMeasureTo;
	}
	public void setUnitOfMeasureTo(Code unitOfMeasureTo) {
		this.unitOfMeasureTo = unitOfMeasureTo;
	}
	public DSDDataType getDataType() {
		return dataType;
	}
	public void setDataType(DSDDataType dataType) {
		this.dataType = dataType;
	}
}
