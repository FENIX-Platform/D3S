package org.fao.fenix.d3s.msd.dao.dm;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.fao.fenix.commons.msd.dto.dm.DM;
import org.fao.fenix.commons.msd.dto.dm.DMMeta;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.commons.utils.CompletenessIterator;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import javax.inject.Inject;

public class DMLoad extends OrientDao {
	
	@Inject private DMConverter converter;
	
	private static OSQLSynchQuery<ODocument> queryLoadMM = new OSQLSynchQuery<ODocument>("select from DMMeta where metadataUID = ?");
	private static OSQLSynchQuery<ODocument> queryLoadDM = new OSQLSynchQuery<ODocument>("select from DMMain where index_uid = ?");
	private static OSQLSynchQuery<ODocument> queryLoadAllDM = new OSQLSynchQuery<ODocument>("select from DMMain");
	private static OSQLSynchQuery<ODocument> queryLoadDMbyRegionCL = new OSQLSynchQuery<ODocument>("select from DMMain where region.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadDMbyCategoryCL = new OSQLSynchQuery<ODocument>("select from DMMain where category.system = ?");
	private static OSQLSynchQuery<ODocument> queryLoadDMbyDomainCL = new OSQLSynchQuery<ODocument>("select from DMMain where domain.system = ?");
	//Temp query to use "Like" wildcards
	private static OSQLSynchQuery<ODocument> queryLoadDMWithLike = new OSQLSynchQuery<ODocument>("select from DMMain where uid like ?");

	//Load metadata structure
	public DMMeta loadMetadataStructure(String uid, boolean all) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadMetadataStructure(uid, database, all);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public DMMeta loadMetadataStructure(String uid, OGraphDatabase database, boolean all) throws Exception {
		ODocument dmO = loadMetadataStructureO(uid, database);
		return dmO!=null ? converter.toMetadataStructure(dmO,database,all) : null;
	}
	public synchronized ODocument loadMetadataStructureO(String uid, OGraphDatabase database) throws Exception {
        queryLoadMM.reset();
        queryLoadMM.resetPagination();
		List<ODocument> result = database.query(queryLoadMM,uid);
		return result.size()==1 ? result.get(0) : null;
	}
	
	//Load dataset metadata
	public DM loadDatasetMetadata(String uid, boolean all) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadDatasetMetadata(uid, database, all);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public DM loadDatasetMetadata(String uid, OGraphDatabase database, boolean all) throws Exception {
		ODocument dmO = loadDatasetMetadataO(uid, database);
		return dmO!=null ? converter.toDM(dmO, all) : null;
	}
	public synchronized ODocument loadDatasetMetadataO(String uid, OGraphDatabase database) throws Exception {
		queryLoadDM.reset();
		queryLoadDM.resetPagination();
		List<ODocument> result = database.query(queryLoadDM,uid);
		return result.size()==1 ? result.get(0) : null;
	}

	//Temp methods to query with "Like" wildcards
	public Collection<DM> loadDatasetMetadata(String[] uids, boolean all) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadDatasetMetadata(uids, database, all);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<DM> loadDatasetMetadata(String[] uids, OGraphDatabase database, boolean all) throws Exception {
		Collection<DM> result = new LinkedList<DM>();
		for (ODocument dmO : loadDatasetMetadataO(uids, database))
			result.add(converter.toDM(dmO, all));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetMetadataO(String[] uids,OGraphDatabase database) throws Exception {
        if (uids==null && uids.length>0)
            return new LinkedList<ODocument>();

        StringBuilder queryBuffer = new StringBuilder();
        for (int i=0; i<uids.length; i++ )
            queryBuffer.append(", ?");
        return (Collection<ODocument>)database.query(new OSQLSynchQuery<ODocument>("select from DMMain where index_uid in ["+queryBuffer.substring(2)+']'),uids);
	}

	//Load echo
	public Collection<String> loadEcho(String[] uids) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadEcho(uids, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<String> loadEcho(String[] uids, OGraphDatabase database) throws Exception {
		Collection<String> result = new LinkedList<String>();
		for (ODocument dmO : loadDatasetMetadataO(uids, database))
			result.add((String)dmO.field("uid"));
		return result;
	}

	//Temp methods to query with "Like" wildcards
	public Collection<DM> loadDatasetMetadataLike(String uid, boolean all) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadDatasetMetadataLike(uid, database, all);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<DM> loadDatasetMetadataLike(String uid, OGraphDatabase database, boolean all) throws Exception {
		Collection<DM> result = new LinkedList<DM>();
		for (ODocument dmO : loadDatasetMetadataLikeO(uid, database))
			result.add(converter.toDM(dmO, all));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetMetadataLikeO(String uid,OGraphDatabase database) throws Exception {
		queryLoadDMWithLike.reset();
		queryLoadDMWithLike.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadDMWithLike,uid);
	}
	//END of Temp methods to query with "Like" wildcards
	
	public Collection<DM> loadDatasetMetadata(boolean all) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadDatasetMetadata(database, all);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<DM> loadDatasetMetadata(OGraphDatabase database, boolean all) throws Exception {
		Collection<DM> result = new LinkedList<DM>();
		for (ODocument dmO : loadDatasetMetadataO(database))
			result.add(converter.toDM(dmO, all));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetMetadataO(OGraphDatabase database) throws Exception {
		queryLoadAllDM.reset();
		queryLoadAllDM.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadAllDM);
	}


    //Load ALL datasets iterable
	public CompletenessIterator<DM> loadDMProducer(final boolean all) throws Exception {
        final Iterator<ODocument> producerO = loadDMProducerO().iterator();
        return new CompletenessIterator<DM>() {
            int index = 0;
            @Override public int getIndex() { return index; }
            @Override public void remove() { producerO.remove(); }
            @Override public boolean hasNext() { return producerO.hasNext(); }
            @Override public DM next() {
                if (producerO.hasNext()) {
                    index++;
                    return converter.toDM(producerO.next(),all);
                } else
                    return null;
            }
        };
	}
	public synchronized Iterable<ODocument> loadDMProducerO() throws Exception {
        return browseClass("DMMain",OrientDatabase.msd);
	}
    public long countDM() throws Exception {
        return countClass("DMMain",OrientDatabase.msd);
    }




    //other
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetsObyRegionCL(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadDMbyRegionCL.reset();
		queryLoadDMbyRegionCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadDMbyRegionCL, systemO);
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetsObyCategoryCL(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadDMbyCategoryCL.reset();
		queryLoadDMbyCategoryCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadDMbyCategoryCL, systemO);
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetsObyDomainCL(ODocument systemO, OGraphDatabase database) throws Exception {
		queryLoadDMbyDomainCL.reset();
		queryLoadDMbyDomainCL.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadDMbyDomainCL, systemO);
	}



}
