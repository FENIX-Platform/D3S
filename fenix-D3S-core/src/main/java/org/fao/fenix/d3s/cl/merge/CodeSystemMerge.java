package org.fao.fenix.d3s.cl.merge;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class CodeSystemMerge extends OrientDao {
	@Autowired protected CodeListLoad clDao;
	
	public CodeSystem getMerge (Collection<CodeSystem> clList) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		return getMerge(database, loadClList(clList, database));
	}
	public CodeSystem getMerge(OGraphDatabase database, ODocument... csList) throws Exception { return getMerge(database, Arrays.asList(csList)); }

	public CodeSystem getMerge(OGraphDatabase database, Collection<ODocument> csList) throws Exception {
		if (csList!=null) {
			CodeSystem merge = createMerge(database, csList);
			String[] mergeKey = getMergeKey(csList);
			merge.setSystem(mergeKey[0]);
			merge.setVersion(mergeKey[1]);
			merge.setVirtualDate(new Date());
		}
		return null;
	}
	
	protected abstract String getMergeKeyPrefix();
	protected abstract CodeSystem createMerge(OGraphDatabase database, Collection<ODocument> csList) throws Exception;

	//Utils
	protected Collection<ODocument> loadClList (Collection<CodeSystem> clList, OGraphDatabase database) throws Exception {
		Collection<ODocument> list = new LinkedList<ODocument>();
		for (CodeSystem cl : clList)
			list.add(clDao.loadSystemO(cl.getSystem(), cl.getVersion(), database));
		return list;
	}
	
	public String[] getMergeKey(Collection<?> clList) {
		String[] mergeKey = new String[]{getMergeKeyPrefix(),""};
		for (Object cl : clList) {
			String[] key = getKey(cl);
			mergeKey[0]+='_'+key[0];
			mergeKey[1]+='_'+key[1];
		}
		mergeKey[1] = mergeKey[1].length()>0 ? mergeKey[1].substring(1) : mergeKey[1];
		return mergeKey;
	}
	private String[] getKey(Object cl) {
		if (cl instanceof CodeSystem)
			return new String[]{((CodeSystem)cl).getSystem(), ((CodeSystem)cl).getVersion()};
		else if (cl instanceof ODocument)
			return new String[]{(String)((ODocument)cl).field("system"), (String)((ODocument)cl).field("version")};
		else
			return new String[]{"",""};
	}


}
