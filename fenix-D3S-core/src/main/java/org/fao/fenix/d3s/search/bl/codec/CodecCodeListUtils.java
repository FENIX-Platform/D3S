package org.fao.fenix.d3s.search.bl.codec;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.fao.fenix.d3s.msd.dao.cl.CodeListConverter;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLinkLoad;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.type.CodeRelationshipType;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;

public class CodecCodeListUtils {
	
	private @Inject CodeListLoad clDao;
	private @Inject CodeListLinkLoad clLinkDao;
	private @Inject CodeListConverter clConverter;
	
	public CodeListConverter getConverter() { return clConverter; }
	public CodeListLoad getDao() { return clDao; }
	
	public String toString(Code code) { return code.getSystemKey()+'|'+code.getSystemVersion()+'|'+code.getCode(); }
	
	public String toString(ODocument code) { return code.field("system.system")+"|"+code.field("system.version")+"|"+code.field("code"); }

	@SuppressWarnings("unchecked")
	public Map<String,Collection<ODocument>>[] getRelationsBetween(ODocument clFromO, ODocument clToO, OGraphDatabase database) throws Exception {
		Map<String,Collection<ODocument>> ooRelations = new HashMap<String, Collection<ODocument>>();
		Map<String,Collection<ODocument>> omRelations = new HashMap<String, Collection<ODocument>>();
		Map<String,Collection<ODocument>> moRelations = new HashMap<String, Collection<ODocument>>();
		Collection<ODocument> relationList = null;
		for (ODocument relation : clLinkDao.loadRelationshipsFromCLtoCLO(clFromO, clToO, database)) {
			String sourceCodeKey = toString((ODocument)relation.field("out"));
			switch(CodeRelationshipType.getByCode((String)relation.field("type"))) {
			case oneToOne:
				relationList = ooRelations.get(sourceCodeKey);
				if (relationList==null)
					ooRelations.put(sourceCodeKey, relationList = new LinkedList<ODocument>());
				relationList.add(relation);
				break;
			case oneToMany:
				relationList = omRelations.get(sourceCodeKey);
				if (relationList==null)
					omRelations.put(sourceCodeKey, relationList = new LinkedList<ODocument>());
				relationList.add(relation);
				break;
			case manyToOne:
				relationList = moRelations.get(sourceCodeKey);
				if (relationList==null)
					moRelations.put(sourceCodeKey, relationList = new LinkedList<ODocument>());
				relationList.add(relation);
				break;
			}
		}
		return new Map[]{ooRelations, omRelations, moRelations};
	}

}
