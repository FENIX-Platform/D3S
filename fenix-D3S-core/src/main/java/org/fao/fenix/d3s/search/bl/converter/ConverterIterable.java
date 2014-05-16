package org.fao.fenix.d3s.search.bl.converter;

import java.util.Map;

//import org.fao.fenix.d3s.cl.conversion.Converter;
import org.fao.fenix.d3s.search.bl.dataFilter.IterableWrapper;

public class ConverterIterable extends IterableWrapper<Object[], Object[]>{
	private static final long serialVersionUID = 1L;
	
	int valueIndex;
	Map<String,Object> systemParameters;
//	org.fao.fenix.d3s.cl.conversion.Converter converter;
	
	public ConverterIterable(Iterable<Object[]> data, int valueIndex, Map<String,Object> systemParameters, Converter converter) {
		super(data);
		this.valueIndex = valueIndex;
		this.systemParameters = systemParameters;
//		this.converter = converter;
	}

	@Override
	protected Object[] apply(Object[] data) {
//		data[valueIndex] = converter.doConversion(data[valueIndex], data, systemParameters);
		return data;
	}

}
