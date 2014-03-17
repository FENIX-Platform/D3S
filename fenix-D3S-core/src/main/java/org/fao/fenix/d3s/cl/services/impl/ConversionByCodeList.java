package org.fao.fenix.d3s.cl.services.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;

import org.fao.fenix.d3s.cl.conversion.Converter;
import org.fao.fenix.d3s.cl.conversion.ConverterFactory;
import org.fao.fenix.d3s.cl.dto.ConverterParameters;
import org.fao.fenix.d3s.cl.dto.Value;
import org.fao.fenix.d3s.msd.dao.cl.CodeListConverter;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLinkLoad;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.dsd.type.DSDDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class ConversionByCodeList extends OrientDao {
	
	@Autowired
    CodeListLinkLoad clLinkDao;
	@Autowired
    CodeListLoad clDao;
	@Autowired
    CodeListConverter clConverter;
	
	public Value applyConversion(Value valueToConvert, Code unitOfMeasure) throws Exception {
		Converter converter = getConverter(valueToConvert.getUnitOfMeasure(), unitOfMeasure, valueToConvert.getDataType(), null);
		Object convertedValue = converter.doConversion(valueToConvert.getValue(), null, null);
		return new Value(
				convertedValue,
				valueToConvert.getDataType(),
				unitOfMeasure
			);
	}
	public Value applyConversion(Value valueToConvert, CodeSystem unitOfMeasureCL) throws Exception {
		Converter converter = getConverter(valueToConvert.getUnitOfMeasure(), unitOfMeasureCL, valueToConvert.getDataType(), null);
		Object convertedValue = converter.doConversion(valueToConvert.getValue(), null, null);
		return new Value(
				convertedValue,
				valueToConvert.getDataType(),
				converter.getUnitOfMeasureTo()
			);
	}
	
	
	public Converter getConverter(Code unitOfMeasureFrom, Code unitOfMeasureTo, DSDDataType dataType, OGraphDatabase database) throws Exception {
		database = database!=null ? database : getDatabase(OrientDatabase.msd);
		
		ODocument umFromO = clDao.loadCodeO(unitOfMeasureFrom.getSystemKey(), unitOfMeasureFrom.getSystemVersion(), unitOfMeasureFrom.getCode(), database);
		ODocument umToO = clDao.loadCodeO(unitOfMeasureTo.getSystemKey(), unitOfMeasureTo.getSystemVersion(), unitOfMeasureTo.getCode(), database);
		
		Collection<ODocument> conversionListO = clLinkDao.loadConversionsFromCodeToCodeO(umFromO, umToO, database);
		return conversionListO.size()<=0 ? null : ConverterFactory.getInstance(getConverterInfo(conversionListO.iterator().next(), dataType, unitOfMeasureFrom, unitOfMeasureTo, database));
	}
	
	public Converter getConverter(Code unitOfMeasureFrom, CodeSystem unitOfMeasureCLTo, DSDDataType dataType, OGraphDatabase database) throws Exception {
		database = database!=null ? database : getDatabase(OrientDatabase.msd);
		
		ODocument umFromO = clDao.loadCodeO(unitOfMeasureFrom.getSystemKey(), unitOfMeasureFrom.getSystemVersion(), unitOfMeasureFrom.getCode(), database);
		ODocument clToO = clDao.loadSystemO(unitOfMeasureCLTo.getSystem(), unitOfMeasureCLTo.getVersion(), database);

		Collection<ODocument> conversionListO = clLinkDao.loadConversionsFromCodeToCLO(umFromO, clToO, database);
		if (conversionListO.size()>0) {
			ODocument conversionO = conversionListO.iterator().next();
			Code unitOfMeasureTo = clConverter.toCode((ODocument)conversionO.field("in"), false, 0);
			return ConverterFactory.getInstance(getConverterInfo(conversionListO.iterator().next(), dataType, unitOfMeasureFrom, unitOfMeasureTo, database));
		} else
			return null;
	}
	
	
	//Utils
	@SuppressWarnings("unchecked")
	private ConverterParameters getConverterInfo(ODocument conversionO, DSDDataType dataType, Code unitOfMeasureFrom, Code unitOfMeasureTo, OGraphDatabase database) {
		ConverterParameters parameters = new ConverterParameters();
		
		parameters.setDataType(dataType);
		parameters.setUnitOfMeasureFrom(unitOfMeasureFrom);
		parameters.setUnitOfMeasureTo(unitOfMeasureTo);
		
		parameters.setConverterClassName((String)conversionO.field("converter"));
		parameters.setConversionRule((String)conversionO.field("rule"));
		parameters.setDynamicParameters((SortedMap<String,String>)conversionO.field("dynamicParameters")); //TODO aggiungere il field e aggiornate DTO, DAO e TEST
		parameters.setFixedParameters((Map<String,Object>)conversionO.field("fixedParameters")); //TODO aggiungere il field e aggiornate DTO, DAO e TEST
		
		Collection<String> orderedRowDimensions = new LinkedList<String>();
//		if (rowStructure!=null)
//			for (ResponseColumnStructure columnStructure : rowStructure)
//				orderedRowDimensions.add(columnStructure.getDimension());
		parameters.setOrderedRowDimensions(orderedRowDimensions);
		
		return parameters;
	}
	
	

}
