package org.fao.fenix.d3s.msd.dao.dsd;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.fao.fenix.commons.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.commons.msd.dto.dsd.DSDDatasource;
import org.fao.fenix.commons.msd.dto.dsd.DSDDimension;
import org.fao.fenix.d3s.msd.dao.common.CommonsConverter;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import javax.inject.Inject;

public class DSDLoad extends OrientDao {
	@Inject private DSDConverter dsdConverter;
	@Inject private CommonsConverter cmConverter;

	private static OSQLSynchQuery<ODocument> queryLoadContextSystem = new OSQLSynchQuery<ODocument>("select from DSDContextSystem where name = ?");
	private static OSQLSynchQuery<ODocument> queryLoadDatasource = new OSQLSynchQuery<ODocument>("select from DSDDatasource where dao = ? and reference = ?");
	private static OSQLSynchQuery<ODocument> queryLoadDimension = new OSQLSynchQuery<ODocument>("select from DSDDimension where name = ?");
	private static OSQLSynchQuery<ODocument> queryLoadColumnsBySystem = new OSQLSynchQuery<ODocument>("select from DSDColumn where codeSystem = ?");
	private static OSQLSynchQuery<ODocument> queryLoadColumnsByDimension = new OSQLSynchQuery<ODocument>("select from DSDColumn where dimension = ?");
	private static OSQLSynchQuery<ODocument> queryLoadDsdByDatasource = new OSQLSynchQuery<ODocument>("select from DSDMain where datasource = ?");
	private static OSQLSynchQuery<ODocument> queryLoadDsdByContextSystem = new OSQLSynchQuery<ODocument>("select from DSDMain where ? in context");
	private static OSQLSynchQuery<ODocument> queryLoadAllDatasource = new OSQLSynchQuery<ODocument>("select from DSDDatasource");
	private static OSQLSynchQuery<ODocument> queryLoadAllDimension = new OSQLSynchQuery<ODocument>("select from DSDDimension");
	private static OSQLSynchQuery<ODocument> queryLoadAllContextSystem = new OSQLSynchQuery<ODocument>("select from DSDContextSystem");
	
	//Context system
	public Collection<DSDContextSystem> loadContextSystem() throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadContextSystem(database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<DSDContextSystem> loadContextSystem(OGraphDatabase database) throws Exception {
		Collection<DSDContextSystem> result = new LinkedList<DSDContextSystem>();
		for (ODocument contextO : loadContextSystemO(database))
			result.add(cmConverter.toContext(contextO));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadContextSystemO(OGraphDatabase database) throws Exception {
		queryLoadAllContextSystem.reset();
		queryLoadAllContextSystem.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadAllContextSystem);
	}
	public synchronized ODocument loadContextSystem(String contextSystem, OGraphDatabase database) throws Exception {
		queryLoadContextSystem.reset();
		queryLoadContextSystem.resetPagination();
		List<ODocument> result = database.query(queryLoadContextSystem,contextSystem);
		return result.size()==1 ? result.get(0) : null;
	}
	//dimension
	public Collection<DSDDimension> loadDimension() throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadDimension(database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<DSDDimension> loadDimension(OGraphDatabase database) throws Exception {
		Collection<DSDDimension> result = new LinkedList<DSDDimension>();
		for (ODocument dimensionO : loadDimensionO(database))
			result.add(dsdConverter.toDimension(dimensionO));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDimensionO(OGraphDatabase database) throws Exception {
		queryLoadAllDimension.reset();
		queryLoadAllDimension.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadAllDimension);
	}
    public DSDDimension loadDimension(String name, OGraphDatabase database) throws Exception {
        return dsdConverter.toDimension(loadDimensionO(name,database));
    }
	public synchronized ODocument loadDimensionO(String name, OGraphDatabase database) throws Exception {
		queryLoadDimension.reset();
		queryLoadDimension.resetPagination();
		List<ODocument> result = database.query(queryLoadDimension,name);
		return result.size()==1 ? result.get(0) : null;
	}
	
	//datasource
	public Collection<DSDDatasource> loadDatasource() throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadDatasource(database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<DSDDatasource> loadDatasource(OGraphDatabase database) throws Exception {
		Collection<DSDDatasource> result = new LinkedList<DSDDatasource>();
		for (ODocument datasourceO : loadDatasourceO(database))
			result.add(dsdConverter.toDatasource(datasourceO));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasourceO(OGraphDatabase database) throws Exception {
		queryLoadAllDatasource.reset();
		queryLoadAllDatasource.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadAllDatasource);
	}
	public synchronized ODocument loadDatasource(String dao, String reference, OGraphDatabase database) throws Exception {
		queryLoadDatasource.reset();
		queryLoadDatasource.resetPagination();
		List<ODocument> result = database.query(queryLoadDatasource,dao,reference);
		return result.size()==1 ? result.get(0) : null;
	}
	
	
	//DSD
	@SuppressWarnings("unchecked")
	protected synchronized Collection<ODocument> loadDsdByDatasource(ODocument datasourceO, OGraphDatabase database) throws Exception {
		queryLoadDsdByDatasource.reset();
		queryLoadDsdByDatasource.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadDsdByDatasource,datasourceO);
	}
	@SuppressWarnings("unchecked")
    public synchronized Collection<ODocument> loadDsdByContextSystem(ODocument contextO, OGraphDatabase database) throws Exception {
		queryLoadDsdByContextSystem.reset();
		queryLoadDsdByContextSystem.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadDsdByContextSystem,contextO);
	}
	
	
	//columns
	@SuppressWarnings("unchecked")
	protected synchronized Collection<ODocument> loadColumnsBySystem(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadColumnsBySystem.reset();
		queryLoadColumnsBySystem.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadColumnsBySystem,systemO);
	}
	@SuppressWarnings("unchecked")
	protected synchronized Collection<ODocument> loadColumnsByDimension(ODocument dimensionO, OGraphDatabase database) throws Exception {
		queryLoadColumnsByDimension.reset();
		queryLoadColumnsByDimension.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadColumnsByDimension,dimensionO);
	}


	
	

}
