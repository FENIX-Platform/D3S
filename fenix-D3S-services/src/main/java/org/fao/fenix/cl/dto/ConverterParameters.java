package org.fao.fenix.cl.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dsd.type.DSDDataType;

public class ConverterParameters implements Comparable<ConverterParameters> {
	
	private String key;
	private String converterClassName;
	private String conversionRule;
	private Map<String,Object> fixedParameters = new HashMap<String, Object>();
	private SortedMap<String,String> dynamicParameters = new TreeMap<String, String>();
	Collection<String> orderedRowDimensions = new LinkedList<String>();
	
	private Code unitOfMeasureFrom;
	private Code unitOfMeasureTo;
	private DSDDataType dataType;
	
	
	
	public String getConverterClassName() {
		return converterClassName;
	}
	public void setConverterClassName(String converterClassName) {
		this.key = null;
		this.converterClassName = converterClassName;
	}
	public Map<String, Object> getFixedParameters() {
		return fixedParameters;
	}
	public void setFixedParameters(Map<String, Object> fixedParameters) {
		this.key = null;
		this.fixedParameters = fixedParameters;
	}
	public SortedMap<String, String> getDynamicParameters() {
		return dynamicParameters;
	}
	public void setDynamicParameters(SortedMap<String, String> dynamicParameters) {
		this.key = null;
		this.dynamicParameters = dynamicParameters;
	}
	public Collection<String> getOrderedRowDimensions() {
		return orderedRowDimensions;
	}
	public void setOrderedRowDimensions(Collection<String> orderedRowDimensions) {
		this.orderedRowDimensions = orderedRowDimensions;
	}
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
	public String getConversionRule() {
		return conversionRule;
	}
	public void setConversionRule(String conversionRule) {
		this.conversionRule = conversionRule;
	}

	
	//Utils
	public void addFixedParameter (String key, Object value) {
		this.key = null;
		fixedParameters.put(key, value);
	}
	public void addDynamicParameter (String key, String value) {
		this.key = null;
		dynamicParameters.put(key, value);
	}
	public void addDimensionName (String dimension) {
		this.key = null;
		orderedRowDimensions.add(key);
	}

	
	//Compare
	private void toString(Map<String,?> map, StringBuilder buffer) {
		for (Map.Entry<String,?> entry : map.entrySet())
			buffer.append(entry.getKey().hashCode()).append(entry.getValue().hashCode());
	}
	private void toString(Collection<String> collection, StringBuilder buffer) {
		for (String element : collection)
			buffer.append(element.hashCode());
	}
	
	@JsonIgnore
	private String getKey() {
		if (key!=null) return key;
		
		StringBuilder buffer = new StringBuilder();
		if (converterClassName!=null)
			buffer.append(converterClassName);
		buffer.append('|');
		if (conversionRule!=null)
			buffer.append(conversionRule);
		buffer.append('|');
		if (fixedParameters!=null)
			toString(fixedParameters, buffer);
		buffer.append('|');
		if (dynamicParameters!=null)
			toString(dynamicParameters, buffer);
		buffer.append('|');
		if (orderedRowDimensions!=null)
			toString(orderedRowDimensions, buffer);
		return key = buffer.toString();
	}
	
	@Override public String toString() { return getKey(); }
	
	@Override public boolean equals(Object obj) { return obj instanceof ConverterParameters && ((ConverterParameters)obj).getKey().equals(getKey()); }
	
	@Override public int compareTo(ConverterParameters o) { return getKey().compareTo(o.getKey()); }
	
}
