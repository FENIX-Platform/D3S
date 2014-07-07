package org.fao.fenix.d3s.msd.dao.canc.dsd;

import java.util.*;

import org.fao.fenix.commons.msd.dto.templates.canc.common.ValueOperator;
import org.fao.fenix.d3s.msd.dao.canc.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dao.canc.common.CommonsStore;
import org.fao.fenix.d3s.msd.dao.canc.dm.DMLoad;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSD;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDColumn;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDDatasource;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDDimension;
import org.fao.fenix.commons.msd.dto.type.dsd.DSDDataType;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;

public class DSDStore extends OrientDao {
	@Inject private CommonsStore commonsStoreDAO;
	@Inject private CodeListLoad clLoadDAO;
	@Inject private DSDLoad dsdLoadDAO;
	@Inject private DMLoad dmLoadDAO;
	
	//UPDATE
	//dsd
	public int updateDSD(DSD dsd, ODocument dsdmain) throws Exception {
		if (dsd==null || dsdmain==null)
			return 0;

		dsdmain.field("startDate", dsd.getStartDate());
		dsdmain.field("endDate", dsd.getEndDate());
		dsdmain.field("supplemental", dsd.getSupplemental());

		//connected elements
        if (dsd.getAggregationRules()!=null)
            dsdmain.field("aggregationRules", storeValueOperator(dsd.getAggregationRules()));
        else
            dsdmain.field("aggregationRules", null, OType.LINKLIST);

        if (dsd.getDatasource()!=null)
			dsdmain.field("datasource", storeDatasource(dsd.getDatasource()));
		else
			dsdmain.field("datasource", null, OType.LINK);

		if (dsd.getContextSystem()!=null)
			dsdmain.field("contextSystem", commonsStoreDAO.storeContext(dsd.getContextSystem()));
		else
			dsdmain.field("contextSystem", null, OType.LINK);
		
		if (dsd.getColumns()!=null && dsd.getColumns().size()>0) {
			Collection<ODocument> columns = dsdmain.field("columns");
			for (ODocument column : columns)
				column.delete();
			columns = new ArrayList<ODocument>();
			for (DSDColumn column : dsd.getColumns())
				columns.add(storeColumn(column));
			dsdmain.field("columns", columns, OType.LINKLIST);
		} else
			dsdmain.field("columns", null, OType.LINKLIST);

		dsdmain.save();
		return 1;
	}
	//dsd append mode
	public int appendDSD(DSD dsd, ODocument dsdmain) throws Exception {
		if (dsd==null || dsdmain==null)
			return 0;

		if (dsd.getStartDate()!=null)
			dsdmain.field("startDate", dsd.getStartDate());
		if (dsd.getEndDate()!=null)
			dsdmain.field("endDate", dsd.getEndDate());
		if (dsd.getSupplemental()!=null)
			dsdmain.field("supplemental", dsd.getSupplemental());

		//connected elements
        if (dsd.getAggregationRules()!=null)
            dsdmain.field("aggregationRules", storeValueOperator(dsd.getAggregationRules()));

        if (dsd.getDatasource()!=null)
			dsdmain.field("datasource", storeDatasource(dsd.getDatasource()));

		if (dsd.getContextSystem()!=null)
			dsdmain.field("contextSystem", commonsStoreDAO.storeContext(dsd.getContextSystem()));
		
		if (dsd.getColumns()!=null && dsd.getColumns().size()>0) {
			Collection<ODocument> columns = dsdmain.field("columns");
			for (ODocument column : columns)
				column.delete();
			columns = new ArrayList<ODocument>();
			for (DSDColumn column : dsd.getColumns())
				columns.add(storeColumn(column));
			dsdmain.field("columns", columns, OType.LINKLIST);
		}
		
		dsdmain.save();
		return 1;
	}
	//column
	public int updateColumn(String datasetUid, DSDColumn column) throws Exception {
		ODocument dsdO = ((ODocument)dmLoadDAO.loadDatasetMetadataO(datasetUid)).field("dsd");
		return updateColumn(column, dsdO);
	}
	public int updateColumn(DSDColumn column, ODocument dsdmain) throws Exception {
		if (column==null || dsdmain==null || column.getColumnId()==null || dsdmain.field("columns")==null)
			return 0;

		Collection<ODocument> columns = dsdmain.field("columns");
		ODocument columnO = null;
		for (Iterator<ODocument> columnIterator=columns.iterator(); columnIterator.hasNext(); )
			if (column.getColumnId().equals((columnO=columnIterator.next()).field("column"))) {
				columnIterator.remove();
				columns.add(storeColumn(column));
				dsdmain.field("columns", columns, OType.LINKLIST);
				dsdmain.save();
				columnO.delete();
				return 1;
			}
		
		return 0;
	}
	//dimension
	public int updateDimension(DSDDimension dimension) throws Exception {
		ODocument dsddatasource = dsdLoadDAO.loadDimensionO(dimension.getName());
		if (dsddatasource==null)
			return 0;
		dsddatasource.field("title", dimension.getTitle());
		dsddatasource.save();
		return 1;
	}
	

	
	//DELETE
	//dimension
	public int deleteDimension(String name) throws Exception {
		ODocument dsddimension = dsdLoadDAO.loadDimensionO(name);
		if (dsddimension==null)
			return 0;
		disconnectDimension(dsddimension);
		dsddimension.delete();
		return 1;
	}
	private void disconnectDimension(ODocument dimensionO) throws Exception {
		for (ODocument column : dsdLoadDAO.loadColumnsByDimension(dimensionO)) {
			column.field("dimension", null, OType.LINK);
			column.save();
		}
	}
	//codelist
	public void disconnectCodeList (ODocument systemO) throws Exception {
		for (ODocument column : dsdLoadDAO.loadColumnsBySystem(systemO)) {
			if (column.field("codeSystem")!=null) {
				column.field("codeSystem", null, OType.LINK);
				column.field("values", null, OType.EMBEDDEDLIST);
				column.save();
			}
		}
	}

	
	
	//STORE

	public ODocument storeDSD(DSD dsd) throws Exception {
		ODocument dsdmain = getConnection().createVertex("DSDMain");

		dsdmain.field("startDate", dsd.getStartDate());
		dsdmain.field("endDate", dsd.getEndDate());
		dsdmain.field("supplemental", dsd.getSupplemental());

		//connected elements
		dsdmain.field("aggregationRules", storeValueOperator(dsd.getAggregationRules()));

        dsdmain.field("datasource", storeDatasource(dsd.getDatasource()));

		dsdmain.field("contextSystem", commonsStoreDAO.storeContext(dsd.getContextSystem()));
		
		Collection<ODocument> columns = new ArrayList<ODocument>();
		for (DSDColumn column : dsd.getColumns())
			columns.add(storeColumn(column));
		dsdmain.field("columns", columns.size()>0 ? columns : null, OType.LINKLIST);

		return dsdmain.save();
	}
	
	private ODocument storeColumn(DSDColumn column) throws Exception {
		ODocument dsdcolumn = getConnection().createVertex("DSDColumn");
		dsdcolumn.field("column", column.getColumnId());
		dsdcolumn.field("title", column.getTitle());
		dsdcolumn.field("supplemental", column.getSupplemental());
		dsdcolumn.field("codesLevel", column.getCodesLevel());
		dsdcolumn.field("datatype", column.getDataType()!=null ? column.getDataType().getCode() : null);
		dsdcolumn.field("virtualColumn", column.getVirtualColumn());
		dsdcolumn.field("geoLayer", column.getGeoLyer());
		//Connected elements
		dsdcolumn.field("dimension", storeDimension(column.getDimension()));
		if (column.getCodeSystem()!=null)
			dsdcolumn.field("codeSystem", clLoadDAO.loadSystemO(column.getCodeSystem().getSystem(), column.getCodeSystem().getVersion()));
		//Values (and codes level)
		Collection<Object> originalValues = column.getValues();
		if (originalValues!=null && originalValues.size()>0) {
			 if (column.getDataType()==DSDDataType.code) {
                 Set<Integer> levels = new HashSet<Integer>();
				 Collection<ODocument> valuesConnection = new LinkedList<ODocument>();
				 CodeSystem cl = column.getCodeSystem();
				for (Object value : originalValues) {
					ODocument codeO = cl == null    ? clLoadDAO.loadCodeO((String)((Map<String,Object>) value).get("systemKey"), (String)((Map<String,Object>) value).get("systemVersion"), (String)((Map<String,Object>) value).get("code"))
                                                    : clLoadDAO.loadCodeO(cl.getSystem(), cl.getVersion(), value instanceof String ? (String) value : (String)((Map<String,Object>) value).get("code"));
					if (codeO!=null) {
						valuesConnection.add(codeO);
                        levels.add((Integer)codeO.field("level"));
                    } else
						throw new Exception("Code value '"+value+"' not found for column: "+column.getColumnId());
				}
				 dsdcolumn.field("values", valuesConnection);
                 if (column.getCodesLevel()==null && levels.size()==1)
                     dsdcolumn.field("codesLevel", levels.iterator().next());
			 } else if (column.getDataType()==DSDDataType.document) {
                 OGraphDatabase database = getConnection();
				 Collection<ODocument> valuesConnection = new LinkedList<ODocument>();
				 for (Object value : originalValues)
					 valuesConnection.add((ODocument)database.load(toRID((String)value)));
				 dsdcolumn.field("values", valuesConnection);
			 } else {
				 dsdcolumn.field("values", originalValues);
			 }
		}
		//return column
		return dsdcolumn.save();
	}

	public ODocument storeDatasource(DSDDatasource datasource) throws Exception {
		ODocument dsddatasource = getConnection().createVertex("DSDDatasource");
		dsddatasource.field("dao", datasource.getDao().getCode());
		dsddatasource.field("reference", datasource.getReference());
		return dsddatasource.save();
	}

	public ODocument storeDimension (DSDDimension dimension) throws Exception {
		ODocument dsddimension = dsdLoadDAO.loadDimensionO(dimension.getName());
		if (dsddimension==null) {
			dsddimension = getConnection().createVertex("DSDDimension");
			dsddimension.field("name", dimension.getName());
			dsddimension.field("title", dimension.getTitle());
			dsddimension.save();
		}
		return dsddimension;
	}


    //Value operator
    public ODocument storeValueOperator(ValueOperator operator) throws Exception {
        ODocument operatorO = getConnection().createVertex("CMValueOperator");
        operatorO.field("implementation", operator.getImplementation());
        operatorO.field("rule", operator.getRule());
        operatorO.field("fixedParameters", operator.getFixedParameters());
        operatorO.field("dimension", storeDimension(operator.getDimension()!=null ? operator.getDimension() : new DSDDimension("VALUE")));
        return operatorO.save();
    }

    public Collection<ODocument> storeValueOperator(Collection<ValueOperator> operators) throws Exception {
        if (operators!=null) {
            Collection<ODocument> operatorsO = new LinkedList<ODocument>();
            for (ValueOperator operator : operators)
                operatorsO.add(storeValueOperator(operator));
            return operatorsO;
        }
        return null;
    }


}
