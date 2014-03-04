package org.fao.fenix.d3s.msd.dao.dsd;

import java.util.*;

import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dao.common.CommonsStore;
import org.fao.fenix.d3s.msd.dao.dm.DMLoad;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.msd.dto.dsd.DSD;
import org.fao.fenix.d3s.msd.dto.dsd.DSDColumn;
import org.fao.fenix.d3s.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.d3s.msd.dto.dsd.DSDDatasource;
import org.fao.fenix.d3s.msd.dto.dsd.DSDDimension;
import org.fao.fenix.d3s.msd.dto.dsd.type.DSDDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class DSDStore extends OrientDao {
	@Autowired private CommonsStore commonsStoreDAO;
	@Autowired private CodeListLoad clLoadDAO;
	@Autowired private DSDLoad dsdLoadDAO;
	@Autowired private DMLoad dmLoadDAO;
	
	//UPDATE
	//dsd
	public int updateDSD(DSD dsd, ODocument dsdmain, OGraphDatabase database) throws Exception {
		if (dsd==null || dsdmain==null)
			return 0;

		dsdmain.field("startDate", dsd.getStartDate());
		dsdmain.field("endDate", dsd.getEndDate());
		dsdmain.field("supplemental", dsd.getSupplemental());

		//connected elements
        if (dsd.getAggregationRules()!=null)
            dsdmain.field("aggregationRules", commonsStoreDAO.storeValueOperator(dsd.getAggregationRules(), database));
        else
            dsdmain.field("aggregationRules", null, OType.LINKLIST);

        if (dsd.getDatasource()!=null)
			dsdmain.field("datasource", storeDatasource(dsd.getDatasource(), database));
		else
			dsdmain.field("datasource", null, OType.LINK);

		if (dsd.getContextSystem()!=null)
			dsdmain.field("contextSystem", storeContext(dsd.getContextSystem(), database));
		else
			dsdmain.field("contextSystem", null, OType.LINK);
		
		if (dsd.getColumns()!=null && dsd.getColumns().size()>0) {
			Collection<ODocument> columns = dsdmain.field("columns");
			for (ODocument column : columns)
				column.delete();
			columns = new ArrayList<ODocument>();
			for (DSDColumn column : dsd.getColumns())
				columns.add(storeColumn(column, database));
			dsdmain.field("columns", columns, OType.LINKLIST);
		} else
			dsdmain.field("columns", null, OType.LINKLIST);

		dsdmain.save();
		return 1;
	}
	//dsd append mode
	public int appendDSD(DSD dsd, ODocument dsdmain, OGraphDatabase database) throws Exception {
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
            dsdmain.field("aggregationRules", commonsStoreDAO.storeValueOperator(dsd.getAggregationRules(), database));

        if (dsd.getDatasource()!=null)
			dsdmain.field("datasource", storeDatasource(dsd.getDatasource(), database));

		if (dsd.getContextSystem()!=null)
			dsdmain.field("contextSystem", storeContext(dsd.getContextSystem(), database));
		
		if (dsd.getColumns()!=null && dsd.getColumns().size()>0) {
			Collection<ODocument> columns = dsdmain.field("columns");
			for (ODocument column : columns)
				column.delete();
			columns = new ArrayList<ODocument>();
			for (DSDColumn column : dsd.getColumns())
				columns.add(storeColumn(column, database));
			dsdmain.field("columns", columns, OType.LINKLIST);
		}
		
		dsdmain.save();
		return 1;
	}
	//column
	public int updateColumn(String datasetUid, DSDColumn column) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return updateColumn(datasetUid, column, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int updateColumn(String datasetUid, DSDColumn column, OGraphDatabase database) throws Exception {
		ODocument dsdO = ((ODocument)dmLoadDAO.loadDatasetMetadataO(datasetUid, database)).field("dsd");
		return updateColumn(column, dsdO, database);
	}
	public int updateColumn(DSDColumn column, ODocument dsdmain, OGraphDatabase database) throws Exception {
		if (column==null || dsdmain==null || column.getColumnId()==null || dsdmain.field("columns")==null)
			return 0;

		Collection<ODocument> columns = dsdmain.field("columns");
		ODocument columnO = null;
		for (Iterator<ODocument> columnIterator=columns.iterator(); columnIterator.hasNext(); )
			if (column.getColumnId().equals((columnO=columnIterator.next()).field("column"))) {
				columnIterator.remove();
				columns.add(storeColumn(column, database));
				dsdmain.field("columns", columns, OType.LINKLIST);
				dsdmain.save();
				columnO.delete();
				return 1;
			}
		
		return 0;
	}
	//dimension
	public int updateDimension(DSDDimension dimension) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return updateDimension(dimension, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int updateDimension(DSDDimension dimension, OGraphDatabase database) throws Exception {
		ODocument dsddatasource = dsdLoadDAO.loadDimensionO(dimension.getName(), database);
		if (dsddatasource==null)
			return 0;
		dsddatasource.field("title", dimension.getTitle());
		dsddatasource.save();
		return 1;
	}
	

	
	//DELETE
	//dimension
	public int deleteDimension(String name) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteDimension(name, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteDimension(String name, OGraphDatabase database) throws Exception {
		ODocument dsddimension = dsdLoadDAO.loadDimensionO(name, database);
		if (dsddimension==null)
			return 0;
		disconnectDimension(dsddimension, database);
		dsddimension.delete();
		return 1;
	}
	private void disconnectDimension(ODocument dimensionO, OGraphDatabase database) throws Exception {
		for (ODocument column : dsdLoadDAO.loadColumnsByDimension(dimensionO, database)) {
			column.field("dimension", null, OType.LINK);
			column.save();
		}
	}
	//context system
	public int deleteContext(String name) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteContext(name, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteContext(String name, OGraphDatabase database) throws Exception {
		ODocument dsdcontext = dsdLoadDAO.loadContextSystem(name, database);
		if (dsdcontext==null)
			return 0;
		disconnectContext(dsdcontext, database);
		dsdcontext.delete();
		return 1;
	}
	private void disconnectContext(ODocument contextO, OGraphDatabase database) throws Exception {
		for (ODocument dsd : dsdLoadDAO.loadDsdByContextSystem(contextO, database)) {
			dsd.field("contextSystem", null, OType.LINK);
			dsd.save();
		}
	}
	//codelist
	public void disconnectCodeList (ODocument systemO, OGraphDatabase database) throws Exception {
		for (ODocument column : dsdLoadDAO.loadColumnsBySystem(systemO, database)) {
			if (column.field("codeSystem")!=null) {
				column.field("codeSystem", null, OType.LINK);
				column.field("values", null, OType.EMBEDDEDLIST);
				column.save();
			}
		}
	}

	
	
	//STORE
	public int storeDatasource(DSDDatasource datasource) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		int count=0;
		try {
			storeDatasource(datasource, database);
		} finally {
			if (database!=null)
				database.close();
		}
		return count;
	}
	public int storeContext(DSDContextSystem context) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		int count=0;
		try {
			storeContext(context, database);
		} finally {
			if (database!=null)
				database.close();
		}
		return count;
	}
	public int storeDimension (DSDDimension dimension) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		int count=0;
		try {
			storeDimension(dimension, database);
		} finally {
			if (database!=null)
				database.close();
		}
		return count;
	}
	
	public ODocument storeDSD(DSD dsd, OGraphDatabase database) throws Exception {
		ODocument dsdmain = database.createVertex("DSDMain");

		dsdmain.field("startDate", dsd.getStartDate());
		dsdmain.field("endDate", dsd.getEndDate());
		dsdmain.field("supplemental", dsd.getSupplemental());

		//connected elements
		dsdmain.field("aggregationRules", commonsStoreDAO.storeValueOperator(dsd.getAggregationRules(), database));

        dsdmain.field("datasource", storeDatasource(dsd.getDatasource(), database));

		dsdmain.field("contextSystem", storeContext(dsd.getContextSystem(), database));
		
		Collection<ODocument> columns = new ArrayList<ODocument>();
		for (DSDColumn column : dsd.getColumns())
			columns.add(storeColumn(column, database));
		dsdmain.field("columns", columns.size()>0 ? columns : null, OType.LINKLIST);

		return dsdmain.save();
	}
	
	private ODocument storeColumn(DSDColumn column, OGraphDatabase database) throws Exception {
		ODocument dsdcolumn = database.createVertex("DSDColumn");
		dsdcolumn.field("column", column.getColumnId());
		dsdcolumn.field("title", column.getTitle());
		dsdcolumn.field("supplemental", column.getSupplemental());
		dsdcolumn.field("codesLevel", column.getCodesLevel());
		dsdcolumn.field("datatype", column.getDataType()!=null ? column.getDataType().getCode() : null);
		dsdcolumn.field("virtualColumn", column.getVirtualColumn());
		dsdcolumn.field("geoLayer", column.getGeoLyer());
		//Connected elements
		dsdcolumn.field("dimension", storeDimension(column.getDimension(), database));
		if (column.getCodeSystem()!=null)
			dsdcolumn.field("codeSystem", clLoadDAO.loadSystemO(column.getCodeSystem().getSystem(), column.getCodeSystem().getVersion(), database));
		//Values (and codes level)
		Collection<Object> originalValues = column.getValues();
		if (originalValues!=null && originalValues.size()>0) {
			 if (column.getDataType()==DSDDataType.code) {
                 Set<Integer> levels = new HashSet<Integer>();
				 Collection<ODocument> valuesConnection = new LinkedList<ODocument>();
				 CodeSystem cl = column.getCodeSystem();
				for (Object value : originalValues) {
					ODocument codeO = cl == null    ? clLoadDAO.loadCodeO((String)((Map<String,Object>) value).get("systemKey"), (String)((Map<String,Object>) value).get("systemVersion"), (String)((Map<String,Object>) value).get("code"), database)
                                                    : clLoadDAO.loadCodeO(cl.getSystem(), cl.getVersion(), value instanceof String ? (String) value : (String)((Map<String,Object>) value).get("code"), database);
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

	public ODocument storeDatasource(DSDDatasource datasource, OGraphDatabase database) throws Exception {
		ODocument dsddatasource = database.createVertex("DSDDatasource");
		dsddatasource.field("dao", datasource.getDao().getCode());
		dsddatasource.field("reference", datasource.getReference());
		return dsddatasource.save();
	}

	public ODocument storeContext(DSDContextSystem context, OGraphDatabase database) throws Exception {
		ODocument dsdcontext = dsdLoadDAO.loadContextSystem(context.getName(), database);
		return dsdcontext!=null ? dsdcontext : database.createVertex("DSDContextSystem").field("name", context.getName()).save();
	}
	
	public ODocument storeDimension (DSDDimension dimension, OGraphDatabase database) throws Exception {
		ODocument dsddimension = dsdLoadDAO.loadDimensionO(dimension.getName(), database);
		if (dsddimension==null) {
			dsddimension = database.createVertex("DSDDimension");
			dsddimension.field("name", dimension.getName());
			dsddimension.field("title", dimension.getTitle());
			dsddimension.save();
		}
		return dsddimension;
	}
	
}
