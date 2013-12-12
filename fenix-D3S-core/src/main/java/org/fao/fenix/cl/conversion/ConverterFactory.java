package org.fao.fenix.cl.conversion;

import java.util.HashMap;
import java.util.Map;

import org.fao.fenix.cl.dto.ConverterParameters;

public class ConverterFactory {
	
	private static Map<ConverterParameters,Converter> converters = new HashMap<ConverterParameters, Converter>();
	
	
	public static Converter getInstance(ConverterParameters converterInfo) throws Exception {
		Converter converter = converters.get(converterInfo);
		if (converter==null) {
			try { converters.put(converterInfo, converter = (Converter) Class.forName(converterInfo.getConverterClassName()).newInstance());
			} catch (ClassNotFoundException ex) { throw new Exception("Wrong converter class name", ex);
			} catch (Exception ex) { throw ex; }
			converter.init(converterInfo.getConversionRule(), converterInfo.getDataType(), converterInfo.getUnitOfMeasureFrom(), converterInfo.getUnitOfMeasureTo(), converterInfo.getFixedParameters(), converterInfo.getDynamicParameters(), converterInfo.getOrderedRowDimensions());
		}
		return converter;
	}

}
