package org.fao.fenix.d3s.msd.dao.dm;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.fao.fenix.commons.msd.dto.full.dm.DM;
import org.fao.fenix.commons.msd.dto.full.dm.DMMeta;
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
		ODocument dmO = loadMetadataStructureO(uid);
		return dmO!=null ? converter.toMetadataStructure(dmO,all) : null;
	}
	public synchronized ODocument loadMetadataStructureO(String uid) throws Exception {
        queryLoadMM.reset();
        queryLoadMM.resetPagination();
		List<ODocument> result = getConnection().query(queryLoadMM,uid);
		return result.size()==1 ? result.get(0) : null;
	}
	
	//Load dataset metadata
	public DM loadDatasetMetadata(String uid, boolean all) throws Exception {
		ODocument dmO = loadDatasetMetadataO(uid);
		return dmO!=null ? converter.toDM(dmO, all) : null;
	}
	public synchronized ODocument loadDatasetMetadataO(String uid) throws Exception {
		queryLoadDM.reset();
		queryLoadDM.resetPagination();
		List<ODocument> result = getConnection().query(queryLoadDM,uid);
		return result.size()==1 ? result.get(0) : null;
	}

	//Temp methods to query with "Like" wildcards
	public Collection<DM> loadDatasetMetadata(String[] uids, boolean all) throws Exception {
		Collection<DM> result = new LinkedList<DM>();
		for (ODocument dmO : loadDatasetMetadataO(uids))
			result.add(converter.toDM(dmO, all));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetMetadataO(String[] uids) throws Exception {
        if (uids==null && uids.length>0)
            return new LinkedList<>();

        StringBuilder queryBuffer = new StringBuilder();
        for (int i=0; i<uids.length; i++ )
            queryBuffer.append(", ?");
        return (Collection<ODocument>)getConnection().query(new OSQLSynchQuery<ODocument>("select from DMMain where index_uid in ["+queryBuffer.substring(2)+']'),uids);
	}

	//Load echo
	public Collection<String> loadEcho(String[] uids) throws Exception {
		Collection<String> result = new LinkedList<String>();
		for (ODocument dmO : loadDatasetMetadataO(uids))
			result.add((String)dmO.field("uid"));
		return result;
	}

	//Temp methods to query with "Like" wildcards
	public Collection<DM> loadDatasetMetadataLike(String uid, boolean all) throws Exception {
		Collection<DM> result = new LinkedList<DM>();
		for (ODocument dmO : loadDatasetMetadataLikeO(uid))
			result.add(converter.toDM(dmO, all));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetMetadataLikeO(String uid) throws Exception {
		queryLoadDMWithLike.reset();
		queryLoadDMWithLike.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadDMWithLike,uid);
	}
	//END of Temp methods to query with "Like" wildcards
	
	public Collection<DM> loadDatasetMetadata(boolean all) throws Exception {
		Collection<DM> result = new LinkedList<DM>();
		for (ODocument dmO : loadDatasetMetadataO())
			result.add(converter.toDM(dmO, all));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetMetadataO() throws Exception {
		queryLoadAllDM.reset();
		queryLoadAllDM.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadAllDM);
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
        return browseClass("DMMain");
	}
    public long countDM() throws Exception {
        return countClass("DMMain");
    }




    //other
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetsObyRegionCL(ODocument systemO) throws Exception {
		queryLoadDMbyRegionCL.reset();
		queryLoadDMbyRegionCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadDMbyRegionCL, systemO);
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetsObyCategoryCL(ODocument systemO) throws Exception {
		queryLoadDMbyCategoryCL.reset();
		queryLoadDMbyCategoryCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadDMbyCategoryCL, systemO);
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadDatasetsObyDomainCL(ODocument systemO) throws Exception {
		queryLoadDMbyDomainCL.reset();
		queryLoadDMbyDomainCL.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadDMbyDomainCL, systemO);
	}



}
