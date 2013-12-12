package org.fao.fenix.cl.services.impl;

import java.util.Collection;

import org.fao.fenix.cl.merge.CodeSystemMerge;
import org.fao.fenix.cl.merge.impl.CodeListsInterception;
import org.fao.fenix.cl.merge.impl.CodeListsStandardUnion;
import org.fao.fenix.cl.merge.impl.CodeListsUnion;
import org.fao.fenix.msd.dao.cl.CodeListLoad;
import org.fao.fenix.msd.dao.cl.CodeListStore;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasicMergeImpl {
	
	public static enum MergeType { standard, interception, union; }
	
	@Autowired private CodeListsStandardUnion clStandardMerge;
	@Autowired private CodeListsInterception clInterception;
	@Autowired private CodeListsUnion clUnion;
	@Autowired private CodeListLoad clDao;
	@Autowired private CodeListStore clStoreDao;
	
	public CodeSystem merge (MergeType type, Collection<CodeSystem> clList, boolean save, boolean update) throws Exception {
		CodeSystemMerge merger = null;
		switch (type) {
			case standard: merger = clStandardMerge; break;
			case interception: merger = clInterception; break;
			case union: merger = clUnion; break;
			default: throw new Exception("Undefined merge type: "+type);
		}
		
		String[] mergeKey = merger.getMergeKey(clList);
		CodeSystem merge = clDao.loadSystem(mergeKey[0], mergeKey[1], true);
		if (merge==null) {
			merge = merger.getMerge(clList);
			if (save && merge!=null)
				clStoreDao.storeCodeList(merge);
			if (update && merge!=null)
				clStoreDao.updateCodeList(merge,false);
		}
		
		return merge;
	}

}
