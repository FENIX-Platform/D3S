package org.fao.fenix.cl.merge.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.fao.fenix.cl.merge.CodeSystemMerge;
import org.fao.fenix.msd.dao.cl.CodeListConverter;
import org.fao.fenix.msd.dao.cl.CodeListLinkLoad;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.cl.type.CodeRelationshipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class CodeListsInterception extends CodeSystemMerge {
	
	@Autowired private CodeListConverter clConverter;
	@Autowired private CodeListLinkLoad clLinkDao;


	@Override
	protected String getMergeKeyPrefix() { return "interceptionMerge"; }

	@Override
	public CodeSystem createMerge(OGraphDatabase database, Collection<ODocument> csList) throws Exception {
		if (csList==null || csList.size()<2)
			return null;
		
		//Create virtual codelist based on master structure
		Iterator<ODocument> csListIterator = csList.iterator();
		ODocument masterO = csListIterator.next();
		CodeSystem master = clConverter.toSystem(masterO, true);
		master.setVirtualDate(new Date());
		
		//Find master codes related to the other codelists
		Set<String> relatedCodes = new HashSet<String>();
		while (csListIterator.hasNext())
			for (ODocument relation : clLinkDao.loadRelationshipsFromCLtoCLO(masterO, csListIterator.next(), database)) {
				CodeRelationshipType relationType = CodeRelationshipType.getByCode((String)relation.field("type"));
				if (relationType==CodeRelationshipType.oneToMany || relationType==CodeRelationshipType.oneToOne)
					relatedCodes.add((String)relation.field("out.code"));
			}
		
		//Collect only related codes
		for (Iterator<Code> codesIterator = master.getRootCodes().iterator(); codesIterator.hasNext(); )
			if (!collectRelated(codesIterator.next(), relatedCodes))
				codesIterator.remove();
		
		//Return newly created code list
		return master;
	}
	
	private boolean collectRelated(Code node, Set<String> relatedCodes) {
		boolean related = relatedCodes.contains(node.getCode());
		if (!node.isLeaf())
			for (Iterator<Code> childIterator = node.getChilds().iterator(); childIterator.hasNext(); )
				if (collectRelated(childIterator.next(), relatedCodes))
					related = true;
				else
					childIterator.remove();
		return related;
	}

}
