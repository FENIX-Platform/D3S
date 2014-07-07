package org.fao.fenix.d3s.msd.dao.canc.dsd;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import org.fao.fenix.commons.msd.dto.templates.canc.common.ValueOperator;
import org.fao.fenix.d3s.msd.dao.canc.cl.CodeListConverter;
import org.fao.fenix.d3s.server.tools.orient.DatabaseStandards;
import org.fao.fenix.d3s.msd.dao.canc.common.CommonsConverter;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSD;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDColumn;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDDatasource;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDDimension;
import org.fao.fenix.commons.msd.dto.type.dsd.DSDDao;
import org.fao.fenix.commons.msd.dto.type.dsd.DSDDataType;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;

public class DSDConverter {
	
	@Inject private CodeListConverter clConverter;
	@Inject private CommonsConverter commonsConverter;
	@Inject private DatabaseStandards database;

	//Code system conversion
	@SuppressWarnings("unchecked")
	public DSD toDSD (ODocument dsdO, boolean all) {
		DSD dsd = new DSD();
		dsd.setSupplemental((Map<String,String>)dsdO.field("supplemental",Map.class));
		dsd.setStartDate((Date)dsdO.field("startDate"));
		dsd.setEndDate((Date)dsdO.field("endDate"));
        dsd.setAggregationRules(toOperator((Collection<ODocument>)dsdO.field("aggregationRules")));
		
		//connected elements
		dsd.setDatasource(toDatasource((ODocument)dsdO.field("datasource")));
		dsd.setContextSystem(commonsConverter.toContext((ODocument) dsdO.field("contextSystem")));
		Collection<ODocument> columns = dsdO.field("columns");
		if (columns!=null)
			for (ODocument columnO : columns)
				dsd.addColumn(toColumn(columnO, all));
		
		return dsd;
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
        OGraphDatabase connection = database.getConnection();
		Collection<Object> valuesBuffer = columnO.field("values");
		if (valuesBuffer!=null && valuesBuffer.size()>0 && valuesBuffer.iterator().next() instanceof ORID && DSDDataType.code==column.getDataType()) {
			Collection<ODocument> values = new LinkedList<>();
			for (Object value : valuesBuffer)
				values.add((ODocument)connection.load((ORID)value));
			valuesBuffer.clear();
			valuesBuffer.addAll(clConverter.toCode(values, all));
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



    //Value operator conversion
    public Collection<ValueOperator> toOperator (Collection<ODocument> operatorsO) {
        if (operatorsO!=null) {
            Collection<ValueOperator> operators = new LinkedList<ValueOperator>();
            for (ODocument operatorO : operatorsO)
                operators.add(toOperator(operatorO));
            return operators;
        }
        return null;
    }

    public ValueOperator toOperator (ODocument operatorO) {
        if (operatorO==null)
            return null;
        ValueOperator operator = new ValueOperator();
        operator.setImplementation((String)operatorO.field("implementation"));
        operator.setRule((String)operatorO.field("rule"));
        operator.setFixedParameters((Map<String,Object>)operatorO.field("fixedParameters",Map.class));
        operator.setDimension(toDimension((ODocument)operatorO.field("dimension")));
        return operator;
    }



}
