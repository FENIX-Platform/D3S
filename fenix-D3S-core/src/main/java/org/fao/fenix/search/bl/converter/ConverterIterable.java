package org.fao.fenix.search.bl.converter;

import java.util.Map;

import org.fao.fenix.search.bl.dataFilter.IterableWrapper;

public class ConverterIterable extends IterableWrapper<Object[], Object[]>{
	private static final long serialVersionUID = 1L;
	
	int valueIndex;
	Map<String,Object> systemParameters;
	org.fao.fenix.cl.conversion.Converter converter;
	
	public ConverterIterable(Iterable<Object[]> data, int valueIndex, Map<String,Object> systemParameters, org.fao.fenix.cl.conversion.Converter converter) {
		super(data);
		this.valueIndex = valueIndex;
		this.systemParameters = systemParameters;
		this.converter = converter;
	}

	@Override
	protected Object[] apply(Object[] data) {
		data[valueIndex] = converter.doConversion(data[valueIndex], data, systemParameters);
		return data;
	}

}
