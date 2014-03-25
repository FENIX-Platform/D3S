package org.fao.fenix.d3s.msd.dao.cl;

import java.util.*;

import com.orientechnologies.orient.core.index.OIndexException;
import org.fao.fenix.commons.msd.utils.DataUtils;
import org.fao.fenix.d3s.msd.dao.common.CommonsStore;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.msd.dao.dm.DMStore;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class CodeListStore extends OrientDao {
	@Autowired private CodeListLoad loadDAO;
	@Autowired private CodeListLinkLoad loadLinkDAO;
	@Autowired private CodeListLinkStore storeLinkDAO;
	@Autowired private DMStore dmStoreDAO;
    @Autowired private CommonsStore cmStoreDAO;
    @Autowired private CodeListIndex index;


    //STORE CODE LIST
    public void storeCodeList(CodeSystem cl) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            database.declareIntent( new OIntentMassiveInsert() );
            storeCodeList(cl, database);
        } finally {
            if (database!=null) {
                database.declareIntent(null);
                database.close();
            }
        }
    }
    public void storeKeyword(String keyword) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            storeKeyword(keyword, database);
        } finally {
            if (database!=null)
                database.close();
        }
    }

    public ODocument storeCodeList(CodeSystem cl, OGraphDatabase database) throws Exception {
        if (cl.getSystem()==null)
            cl.setSystem(createCodeSystemName(database));
        ODocument clO = loadDAO.loadSystemO(cl.getSystem(), cl.getVersion(), database);
        if (clO!=null)
            throw new OIndexException("Found duplicated key");

        clO = database.createVertex("CSVersion");
        clO.field("system", cl.getSystem());
        clO.field("version", cl.getVersion());
        clO.field("title", cl.getTitle());
        clO.field("abstract", cl.getDescription());
        clO.field("startDate", cl.getStartDate());
        clO.field("endDate", cl.getEndDate());
        clO.field("virtualDate", cl.getVirtualDate());
        clO.field("sharingPolicy", cl.getSharingPolicy()!=null ? cl.getSharingPolicy().getCode() : null);
        clO.field("levelsNumber", cl.getLevelsNumber());

        //connected elements
        Collection<ODocument> keywords = new ArrayList<ODocument>();
        if (cl.getKeyWords()!=null)
            for (String keyword : cl.getKeyWords())
                keywords.add(storeKeyword(keyword, database));
        clO.field("keywords", keywords.size()>0 ? keywords : null, OType.LINKLIST);

        if (cl.getSource()!=null)
            clO.field("source", cmStoreDAO.storeContactIdentity(cl.getSource(), database));
        if (cl.getProvider()!=null)
            clO.field("provider", cmStoreDAO.storeContactIdentity(cl.getProvider(), database));

        //code list
        clO.save();
        Map<String, ODocument> storedCodes = new HashMap<>();
        Collection<ODocument> rootCodes = new ArrayList<>();
        if (cl.getRootCodes()!=null)
            for (Code code : cl.getRootCodes())
                rootCodes.add(storeCode(code, clO, null, true, storedCodes, database));
        clO.field("rootCodes", rootCodes.size()>0 ? rootCodes : null, OType.LINKLIST);
        clO.save();

        //Code metadata
        if (cl.getRegion()!=null)
            clO.field("region", loadDAO.loadCodeO(cl.getRegion().getSystemKey(), cl.getRegion().getSystemVersion(), cl.getRegion().getCode(), database));
        if (cl.getCategory()!=null)
            clO.field("category", loadDAO.loadCodeO(cl.getCategory().getSystemKey(), cl.getCategory().getSystemVersion(), cl.getCategory().getCode(), database));

        //Index
        index.rebuildIndex(clO,false);

        return clO.save();
    }

    public ODocument storeKeyword(String keyword, OGraphDatabase database) throws Exception {
        ODocument cskeyword = loadDAO.loadKeywordO(keyword, database);
        return cskeyword!=null ? cskeyword : database.createVertex("CSKeyword").field("keyword", keyword).save();
    }

    private ODocument storeCode(Code code, ODocument system, ODocument parent, boolean storeChilds, Map<String,ODocument> storedCodes, OGraphDatabase database) throws Exception {
        ODocument codeO = storedCodes!=null ? storedCodes.get(code.getCode()) : loadDAO.loadCodeO(system, code.getCode(), database);
        if (codeO==null) {
            storedCodes.put(code.getCode(), codeO = database.createVertex("CSCode"));
            codeO.field("code", code.getCode());
            codeO.field("level", code.getLevel());
            codeO.field("title", code.getTitle());
            codeO.field("abstract", code.getDescription());
            codeO.field("supplemental", code.getSupplemental());
            codeO.field("fromDate", code.getFromDate());
            codeO.field("toDate", code.getToDate());
            codeO.field("system", system);
            codeO.save();

            //connected hierarchy elements
            if (storeChilds) {
                Collection<ODocument> childs = new LinkedList<>();
                if (code.getChilds()!=null)
                    for (Code child : code.getChilds())
                        childs.add(storeCode(child, system, codeO, storeChilds, storedCodes, database));
                codeO.field("childs", childs);
            }

            index.rebuildCodeIndex(codeO, false);

            codeO.save();

            //connected external elements
            if (code.getRelations()!=null)
                storeLinkDAO.storeCodeRelationship(code.getRelations());
            if (code.getConversions()!=null)
                storeLinkDAO.storeCodeConversion(code.getConversions());
            if (code.getPropaedeutics()!=null)
                storeLinkDAO.storeCodePropaedeutic(code.getPropaedeutics());
        }

        if (parent!=null) {
            Collection<ODocument> parents = codeO.field("parents");
            if (parents==null)
                parents = new LinkedList<>();
            parents.add(parent);
            codeO.field("parents", parents);
        }

        return codeO.save();
    }



    /**************************************************************************************************/
    //UPDATE CODE LIST METADATA
	public int updateCodeList(CodeSystem cl, boolean append) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return append ? appendCodeList(cl, database) : updateCodeList(cl, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	
	public int updateCodeList(CodeSystem cl, OGraphDatabase database) throws Exception {
		ODocument csmain = loadDAO.loadSystemO(cl.getSystem(), cl.getVersion(), database);
		if (csmain==null)
			return 0;

		csmain.field("system", cl.getSystem());
		csmain.field("version", cl.getVersion());
		csmain.field("title", cl.getTitle());
		csmain.field("abstract", cl.getDescription());
		csmain.field("startDate", cl.getStartDate());
		csmain.field("endDate", cl.getEndDate());
		csmain.field("virtualDate", cl.getVirtualDate());
		csmain.field("sharingPolicy", cl.getSharingPolicy()!=null ? cl.getSharingPolicy().getCode() : null);
		csmain.field("levelsNumber", cl.getLevelsNumber());

		//connected elements
        if (cl.getKeyWords()!=null) {
            Collection<ODocument> keywords = new ArrayList<>(); //Nothing to delete about keywords
            if (cl.getKeyWords()!=null)
                for (String keyword : cl.getKeyWords())
                    keywords.add(storeKeyword(keyword, database).save());
            csmain.field("keywords", keywords.size()>0 ? keywords : null, OType.LINKLIST);
        }

		if (csmain.field("category")!=null)
			((ODocument)csmain.field("category")).delete();
		if (cl.getCategory()!=null)
			csmain.field("category", loadDAO.loadCodeO(cl.getCategory().getSystemKey(), cl.getCategory().getSystemVersion(), cl.getCategory().getCode(), database));
		else
			csmain.field("category", null, OType.LINK);

		if (cl.getRegion()!=null)
			csmain.field("region", loadDAO.loadCodeO(cl.getRegion().getSystemKey(), cl.getRegion().getSystemVersion(), cl.getRegion().getCode(), database));
		else
			csmain.field("region", null, OType.LINK);

		if (cl.getSource()!=null)
			csmain.field("source", cmStoreDAO.storeContactIdentity(cl.getSource(), database));
		else
			csmain.field("source", null, OType.LINK);

		if (cl.getProvider()!=null)
			csmain.field("provider", cmStoreDAO.storeContactIdentity(cl.getProvider(), database));
		else
			csmain.field("provider", null, OType.LINK);

		csmain.save();

        //Index
        index.rebuildIndex(csmain,false);

		return 1;
	}


	public int appendCodeList(CodeSystem cl, OGraphDatabase database) throws Exception {
		ODocument csmain = loadDAO.loadSystemO(cl.getSystem(), cl.getVersion(), database);
		if (csmain==null)
			return 0;

		if (cl.getSystem()!=null)
			csmain.field("system", cl.getSystem());
		if (cl.getVersion()!=null)
			csmain.field("version", cl.getVersion());
		if (cl.getTitle()!=null)
			csmain.field("title", cl.getTitle());
		if (cl.getDescription()!=null)
			csmain.field("abstract", cl.getDescription());
		if (cl.getStartDate()!=null)
			csmain.field("startDate", cl.getStartDate());
		if (cl.getEndDate()!=null)
			csmain.field("endDate", cl.getEndDate());
		if (cl.getVirtualDate()!=null)
			csmain.field("virtualDate", cl.getVirtualDate());
		if (cl.getSharingPolicy()!=null)
			csmain.field("sharingPolicy", cl.getSharingPolicy().getCode());
		if (cl.getLevelsNumber()!=null)
			csmain.field("levelsNumber", cl.getLevelsNumber());

		//connected elements
		if (cl.getKeyWords()!=null) {
			Collection<ODocument> keywords = new ArrayList<ODocument>(); //Nothing to delete about keywords
			for (String keyword : cl.getKeyWords())
				keywords.add(storeKeyword(keyword, database).save());
			csmain.field("keywords", keywords.size()>0 ? keywords : null, OType.LINKLIST);
		}

		if (cl.getCategory()!=null)
			csmain.field("category", loadDAO.loadCodeO(cl.getCategory().getSystemKey(), cl.getCategory().getSystemVersion(), cl.getCategory().getCode(), database));

		if (cl.getRegion()!=null)
			csmain.field("region", loadDAO.loadCodeO(cl.getRegion().getSystemKey(), cl.getRegion().getSystemVersion(), cl.getRegion().getCode(), database));

		if (cl.getSource()!=null)
			csmain.field("source", cmStoreDAO.storeContactIdentity(cl.getSource(), database));

		if (cl.getProvider()!=null)
			csmain.field("provider", cmStoreDAO.storeContactIdentity(cl.getProvider(), database));

        //Index
        index.rebuildIndex(csmain,false);

        return 1;
	}




    /**************************************************************************************************/
    //UPDATE CODE LIST STRUCTURE
    public int updateCodes(CodeSystem cl, boolean append) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            return updateCodes(cl, append, database);
        } finally {
            if (database!=null)
                database.close();
        }
    }

    public int updateCodes(CodeSystem cl, boolean append, OGraphDatabase database) throws Exception {
        ODocument clO = loadDAO.loadSystemO(cl.getSystem(), cl.getVersion(), database);
        if (clO==null)
            return 0;
        else
            return append ? appendCodes(clO, cl, database) : updateCodes(clO, cl, database);
    }

    public int updateCodes(ODocument clO, CodeSystem cl, OGraphDatabase database) throws Exception {
        //TODO update support will be added after codes delete revision
        throw new UnsupportedOperationException("Only append mode is supported.");
    }
    public int appendCodes(ODocument clO, CodeSystem cl, OGraphDatabase database) throws Exception {
        //Retrieve existing root codes level
        Set<ODocument> rootCodesO = DataUtils.toSet((Collection<ODocument>) clO.field("rootCodes"));
        //Retrieve all existing codes
        Map<String, ODocument> existingCodesO = new HashMap<>();
        getExistingCodesO(existingCodesO,rootCodesO);

        //Store new codes, update existing codes content ì and update root codes level
        Set<String> visitedCodes = new HashSet<>();
        Collection<Code> rootCodes = cl.getRootCodes();
        if (rootCodes!=null)
            for (Code code : rootCodes)
                rootCodesO.add(updateCodesContent(clO, code, existingCodesO, visitedCodes, true, database));
        clO.field("rootCodes",DataUtils.toList(rootCodesO)).save();
        //Update codes hierarchy
        int count = 0;
        visitedCodes.clear();
        if (rootCodes!=null)
            for (Code code : rootCodes)
                count+=appendCodesHierarchy(code, existingCodesO, visitedCodes);

        return count;
    }

    private void getExistingCodesO (Map<String, ODocument> existingCodesO, Collection<ODocument> codesO) throws Exception {
        if (codesO!=null)
            for (ODocument codeO : codesO)
                if (codeO!=null) {
                    getExistingCodesO(existingCodesO, (Collection<ODocument>)codeO.field("childs"));
                    existingCodesO.put((String)codeO.field("code"),codeO);
                }
    }

    private ODocument updateCodesContent(ODocument clO, Code code, Map<String, ODocument> existingCodesO, Set<String> visited, boolean append, OGraphDatabase database) throws Exception {
        if (visited.contains(code.getCode()))
            return existingCodesO.get(code.getCode());
        visited.add(code.getCode());

        ODocument codeO = existingCodesO.get(code.getCode());
        if (codeO==null)
            existingCodesO.put(code.getCode(), codeO = storeCode(code, clO, null, false, existingCodesO, database));
        else if (append)
            appendToCode(codeO, code);
        else
            updateCode(codeO,code);

        Collection<Code> children = code.getChilds();
        if (children!=null)
            for (Code child : children)
                updateCodesContent(clO, child, existingCodesO, visited, append, database);

        return codeO;
    }

    private int appendCodesHierarchy (Code code, Map<String, ODocument> existingCodesO, Set<String> visited) {
        if (visited.contains(code.getCode()))
            return 0;
        visited.add(code.getCode());

        ODocument codeO = existingCodesO.get(code.getCode());
        if (!code.isRoot()) {
            Set<ODocument> parentsO = DataUtils.toSet((Collection<ODocument>) codeO.field("parents"));
            for (Code parent : code.getParents())
                parentsO.add(existingCodesO.get(parent));
            codeO.field("parents",DataUtils.toList(parentsO));
        }
        if (!code.isLeaf()) {
            Set<ODocument> childrenO = DataUtils.toSet((Collection<ODocument>) codeO.field("childs"));
            for (Code child : code.getChilds())
                childrenO.add(existingCodesO.get(child.getCode()));
            codeO.field("childs",DataUtils.toList(childrenO));
        }
        codeO.save();

        int count = 1;
        if (!code.isLeaf())
            for (Code child : code.getChilds())
                count += appendCodesHierarchy(child,existingCodesO,visited);

        return count;
    }





    /**************************************************************************************************/
	//UPDATE SINGLE CODE
	public int updateCode(Code code) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return updateCode(code, database)!=null ? 1 : 0;
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int appendToCode(Code code) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return appendToCode(code, database)!=null ? 1 : 0;
		} finally {
			if (database!=null)
				database.close();
		}
	}

	public ODocument updateCode(Code code, OGraphDatabase database) throws Exception {
		ODocument codeO = loadDAO.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
		if (codeO!=null) {
            updateCode(codeO, code);
            return codeO.save();
        } else
            return null;
	}

	public ODocument appendToCode(Code code, OGraphDatabase database) throws Exception {
		ODocument codeO = loadDAO.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
        if (codeO!=null) {
            appendToCode(codeO, code);
            return codeO.save();
        } else
            return null;
	}

    private void updateCode(ODocument codeO, Code code) throws Exception {
        if (codeO!=null) {
            codeO.field("level", code.getLevel());
            codeO.field("title", code.getTitle());
            codeO.field("abstract", code.getDescription());
            codeO.field("supplemental", code.getSupplemental());
            codeO.field("fromDate", code.getFromDate());
            codeO.field("toDate", code.getToDate());

            index.rebuildCodeIndex(codeO, false);
        }
    }
    private void appendToCode(ODocument codeO, Code code) throws Exception {
        if (codeO!=null) {
            if (code.getLevel()!=null)
                codeO.field("level", code.getLevel());
            if (code.getTitle()!=null) {
                Map<String,String> labelO = codeO.field("title");
                if (labelO==null)
                    labelO = new HashMap<>();
                labelO.putAll(code.getTitle());
                codeO.field("title", labelO);
            }
            if (code.getDescription()!=null) {
                Map<String,String> labelO = codeO.field("abstract");
                if (labelO==null)
                    labelO = new HashMap<>();
                labelO.putAll(code.getDescription());
                codeO.field("abstract", labelO);
            }
            if (code.getSupplemental()!=null) {
                Map<String,String> labelO = codeO.field("supplemental");
                if (labelO==null)
                    labelO = new HashMap<>();
                labelO.putAll(code.getSupplemental());
                codeO.field("supplemental", labelO);
            }
            if (code.getFromDate()!=null)
                codeO.field("fromDate", code.getFromDate());
            if (code.getToDate()!=null)
                codeO.field("toDate", code.getToDate());

            index.rebuildCodeIndex(codeO, false);
        }
    }




    /**************************************************************************************************/
	//DELETE CODE LIST
	public int deleteCodeList(String system, String version) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			int count = deleteCodeList(system, version, database);
			return count;
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteCodeList(String system, String version, OGraphDatabase database) throws Exception {
		ODocument systemO = loadDAO.loadSystemO(system, version, database);
		if (systemO==null)
			return 0;
		//Disconnect CodeList
		disconnectCodeList(systemO, database);
		dmStoreDAO.disconnectCodeList(systemO, database);
		//Delete CodeSystem
		return deleteGraph(systemO, new HashSet<String>(Arrays.asList(new String[]{"CMContactIdentity","CSKeyword","CSPropaedeutic","CSRelationship","CSConversion"})));
	}
	private void disconnectCodeList(ODocument systemO, OGraphDatabase database) throws Exception {
		for (ODocument edge : loadLinkDAO.loadLinksFromCLO(systemO, database))
			database.removeEdge(edge);
	}
	//keyword
	public int deleteKeyword(String keyword) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return deleteKeyword(keyword, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteKeyword(String keyword, OGraphDatabase database) throws Exception {
		ODocument cskeyword = loadDAO.loadKeywordO(keyword, database);
		if (cskeyword==null)
			return 0;
		disconnectKeyword(cskeyword, database);
		cskeyword.delete();
		return 1;
	}
	@SuppressWarnings("unchecked")
	private void disconnectKeyword(ODocument keywordO, OGraphDatabase database) throws Exception {
		for (ODocument systemO : loadDAO.loadSystemByKeywordO(keywordO, database)) {
			Collection<ODocument> keywords = systemO.field("keywords");
			keywords.remove(keywordO);
			systemO.field("keywords", keywords, OType.LINKLIST);
			systemO.save();
		}
	}





    /**************************************************************************************************/
	//Utils
	public String createCodeSystemName(OGraphDatabase database) {
        return "_DynamicCodeSystem_"+new com.eaio.uuid.UUID().toString();
	}

}
