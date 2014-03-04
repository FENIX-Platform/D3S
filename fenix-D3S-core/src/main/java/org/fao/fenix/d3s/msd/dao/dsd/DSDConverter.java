package org.fao.fenix.d3s.msd.dao.dsd;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.fao.fenix.d3s.msd.dao.cl.CodeListConverter;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;
import org.fao.fenix.d3s.msd.dao.common.CommonsConverter;
import org.fao.fenix.d3s.msd.dto.dsd.DSD;
import org.fao.fenix.d3s.msd.dto.dsd.DSDColumn;
import org.fao.fenix.d3s.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.d3s.msd.dto.dsd.DSDDatasource;
import org.fao.fenix.d3s.msd.dto.dsd.DSDDimension;
import org.fao.fenix.d3s.msd.dto.dsd.type.DSDDao;
import org.fao.fenix.d3s.msd.dto.dsd.type.DSDDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class DSDConverter {
	
	@Autowired private CodeListConverter clConverter;
	@Autowired private CommonsConverter commonsConverter;

	//Code system conversion
	@SuppressWarnings("unchecked")
	public DSD toDSD (ODocument dsdO, boolean all) {
		DSD dsd = new DSD();
		dsd.setSupplemental((Map<String,String>)dsdO.field("supplemental",Map.class));
		dsd.setStartDate((Date)dsdO.field("startDate"));
		dsd.setEndDate((Date)dsdO.field("endDate"));
        dsd.setAggregationRules(commonsConverter.toOperator((Collection<ODocument>)dsdO.field("aggregationRules")));
		
		//connected elements
		dsd.setDatasource(toDatasource((ODocument)dsdO.field("datasource")));
		dsd.setContextSystem(toContext((ODocument)dsdO.field("contextSystem")));
		Collection<ODocument> columns = dsdO.field("columns");
		if (columns!=null)
			for (ODocument columnO : columns)
				dsd.addColumn(toColumn(columnO, all));
		
		return dsd;
	}
	
	public DSDContextSystem toContext (ODocument contextO) {
        if (contextO==null)
            return null;
		DSDContextSystem context = new DSDContextSystem();
		context.setName((String)contextO.field("name"));
		return context;
	}
	@SuppressWarnings("unchecked")
	public DSDDimension toDimension (ODocument dimensionO) {
        if (dimensionO==null)
            return null;
		DSDDimension dimension = new DSDDimension();
		dimension.setName((String)dimensionO.field("name"));
		dimension.setTitle((Map<String,String>)dimensionO.field("title",Map.class));
		return dimension;
	}
	@SuppressWarnings("unchecked")
	public DSDDatasource toDatasource (ODocument datasourceO) {
        if (datasourceO==null)
            return null;
		DSDDatasource datasource = new DSDDatasource();
		datasource.setDao(DSDDao.getByCode((String)datasourceO.field("dao")));
		datasource.setReference((Map<String,String>)datasourceO.field("reference",Map.class));
		return datasource;
	}
	@SuppressWarnings("unchecked")
	public DSDColumn toColumn (ODocument columnO, boolean all) {
        if (columnO==null)
            return null;
		DSDColumn column = new DSDColumn();
		column.setColumnId((String)columnO.field("column"));
		column.setTitle((Map<String,String>)columnO.field("title",Map.class));
		column.setSupplemental((Map<String,String>)columnO.field("supplemental",Map.class));
		column.setCodesLevel((Integer)columnO.field("codesLevel"));
		column.setDataType(DSDDataType.getByCode((String)columnO.field("datatype")));
		column.setVirtualColumn((String)columnO.field("virtualColumn"));
		column.setGeoLyer((String)columnO.field("geoLayer"));
		//Values
		Collection<Object> valuesBuffer = columnO.field("values");
		if (valuesBuffer!=null && valuesBuffer.size()>0 && valuesBuffer.iterator().next() instanceof ORID && DSDDataType.code==column.getDataType()) {
			OGraphDatabase database = OrientServer.getMsdDatabase();
			try {
			Collection<ODocument> values = new LinkedList<ODocument>();
			for (Object value : valuesBuffer)
				values.add((ODocument)database.load((ORID)value));
			valuesBuffer.clear();
			valuesBuffer.addAll(clConverter.toCode(values, all));
			} finally {
				if (database!=null)
					database.close();
			}
		}
		
//TODO convertire i document in mappe se nn sono codici
		
		column.setValues(valuesBuffer);
		//Connected elements
		column.setDimension(toDimension((ODocument)columnO.field("dimension")));
		if (columnO.field("codeSystem")!=null)
			column.setCodeSystem(clConverter.toSystem((ODocument)columnO.field("codeSystem"), false));
		//Return column
		return column;
	}
	

}
