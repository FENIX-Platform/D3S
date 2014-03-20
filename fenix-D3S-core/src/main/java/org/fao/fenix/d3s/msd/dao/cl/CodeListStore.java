package org.fao.fenix.d3s.msd.dao.cl;

import java.util.*;

import com.orientechnologies.orient.core.index.OIndexException;
import org.fao.fenix.d3s.msd.dao.common.CommonsStore;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.msd.dao.dm.DMStore;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.cl.type.CSHierarchyType;
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


    //UPDATE
	//codelist
	public int updateCodeList(CodeSystem cl, boolean append) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			int count = append ? appendCodeList(cl, database) : updateCodeList(cl, database);
			return count;
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

        }
		Collection<ODocument> keywords = new ArrayList<ODocument>(); //Nothing to delete about keywords
        if (cl.getKeyWords()!=null)
            for (String keyword : cl.getKeyWords())
			    keywords.add(storeKeyword(keyword, database).save());
		csmain.field("keywords", keywords.size()>0 ? keywords : null, OType.LINKLIST);

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

        //codes
        cl.resetLevels();
        appendCodes(csmain,cl.getRootCodes(),database);

        //Index
        index.rebuildIndex(csmain,false);

        return 1;
	}

    private Map<String, ODocument> getExistingCodesO (ODocument csO) throws Exception {
        Map<String, ODocument> existingCodesO = new HashMap<>();
        getExistingCodesO(existingCodesO, (Collection<ODocument>)csO.field("rootCodes"));
        return existingCodesO;
    }

    private void getExistingCodesO (Map<String, ODocument> existingCodesO, Collection<ODocument> codesO) throws Exception {
        if (codesO!=null)
            for (ODocument codeO : codesO)
                if (codeO!=null) {
                    getExistingCodesO(existingCodesO, (Collection<ODocument>)codeO.field("childs"));
                    existingCodesO.put((String)codeO.field("code"),codeO);
                }
    }

    private Collection<ODocument> getRootCodesO (Collection<ODocument> codesO) {
        Collection<ODocument> rootCodesO = new LinkedList<>();
        for (ODocument codeO : codesO) {
            Collection<ODocument> parentsO = codeO.field("parents");
            if (parentsO==null || parentsO.size()==0)
                rootCodesO.add(codeO);
        }
        return rootCodesO;
    }


    private void compressGraph (CodeSystem cl) {
        Collection<Code> rootCodes = cl.getRootCodes();
        Collection<Code> compressed = new LinkedList<>();
//        if (rootCodes!=null)
//            for ()
    }
    private Code compressGraph (Code code, Map<String,Code> processedCodes) {
        //TODO
        return null;
    }


    private void updateCodesContent (Map<String,ODocument> codesO, Map<String,Code> processedCodes, Set<String> storedCodes, ODocument csO, Collection<Code> codes, boolean append, OGraphDatabase database) throws Exception {
        if (codes!=null)
            for (Code code : codes) {
                String c = code.getCode();
                ODocument codeO = codesO.get(c);

                if (processedCodes.put(c, code)!=null) //Duplicate key error
                    throw new Exception ("Duplicate ")

                if (codeO!=null) {
                    if (append)
                        appendToCode(codeO,code);
                    else
                        updateCode(codeO,code);
                } else {
                    codesO.put(c, storeCode(code, csO, null, false, database));
                    storedCodes.add(c);
                }

                updateCodesContent(codesO, processedCodes, storedCodes, csO, code.getChilds(), append, database);
            }
    }

    private void updateCodesConnections(Map<String,ODocument> updatedCodes, Code code, ODocument parentO) throws Exception {



        ODocument codeO = updatedCodes.get(code.getCode());
        Collection<ODocument> parentsO = codeO.field("parents");
        Collection<ODocument> childsO = codeO.field("childs");
        Collection<Code> childs = code.getChilds();

        if (parentO!=null) {
            if (parentsO == null)
                parentsO = new LinkedList<ODocument>();
            parentsO.add(parentO);
        }
        if (childs!=null) {
            if (childsO == null)
                childsO = new LinkedList<ODocument>();
            for (Code child : childs) {
                ODocument childO = updatedCodes.get(child.getCode());
                if (childO!=null)
                    childsO.add(childO);
            }
        }

        codeO.field("parents", parentsO);
        codeO.field("childs", childsO);

        codeO.save();

        if (!code.isLeaf())
            for (Code c : code.getChilds())
                addConnections(updatedCodes, c, codeO);
    }
    private ODocument appendToCode(Map<String, ODocument> updatedCodes, Code code, OGraphDatabase database) throws Exception {
        ODocument codeO = appendToCode(code,database);
        if (codeO!=null)
            updatedCodes.put(code.getCode(),codeO);
        if (!code.isLeaf())
            for (Code c : code.getChilds())
                appendToCode(updatedCodes, c, database);
        return codeO;
    }
    private ODocument storeCode(Map<String,ODocument> updatedCodes, ODocument csO, Code code, OGraphDatabase database) throws Exception {
        ODocument codeO = null;
        if (!updatedCodes.containsKey(code.getCode()))
            updatedCodes.put(code.getCode(), codeO=storeCode(code, csO, null, false, database));
        if (!code.isLeaf())
            for (Code c : code.getChilds())
                storeCode(updatedCodes, csO, c, database);
        return codeO;
    }

	//code
	public int updateCode(Code code) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return updateCode(code, database)!=null ? 1 : 0;
		} finally {
			if (database!=null)
				database.close();
		}
	}

	public ODocument updateCode(Code code, OGraphDatabase database) throws Exception {
		ODocument codeO = loadDAO.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
		updateCode(codeO,code);
        return codeO.save();
	}

	public ODocument appendToCode(Code code, OGraphDatabase database) throws Exception {
		ODocument codeO = loadDAO.loadCodeO(code.getSystemKey(), code.getSystemVersion(), code.getCode(), database);
        appendToCode(codeO,code);
		return codeO.save();
	}

    private void updateCode(ODocument codeO, Code code) throws Exception {
        if (codeO!=null) {
            codeO.field("level", code.getLevel());
            codeO.field("title", code.getTitle());
            codeO.field("abstract", code.getDescription());
            codeO.field("supplemental", code.getSupplemental());
            codeO.field("fromDate", code.getFromDate());
            codeO.field("toDate", code.getToDate());

            Collection<ODocument> exclusionList = new LinkedList<ODocument>();
            if (code.getExclusionList()!=null)
                for (Code exclusion : code.getExclusionList())
                    exclusionList.add(loadDAO.loadCodeO(exclusion.getSystemKey(), exclusion.getSystemVersion(), exclusion.getCode(), database));
            codeO.field("exclusions",exclusionList.size()>0 ? exclusionList : null, OType.LINKLIST);

            index.rebuildCodeIndex(codeO);
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

            if (code.getExclusionList()!=null) {
                Collection<ODocument> exclusionList = new LinkedList<ODocument>();
                for (Code exclusion : code.getExclusionList())
                    exclusionList.add(loadDAO.loadCodeO(exclusion.getSystemKey(), exclusion.getSystemVersion(), exclusion.getCode(), database));
                codeO.field("exclusions",exclusionList.size()>0 ? exclusionList : null, OType.LINKLIST);
            }

            index.rebuildCodeIndex(codeO);
        }
    }


	//DELETE
	//codelist
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
			Collection<ODocument> keywords = (Collection<ODocument>)systemO.field("keywords");
			keywords.remove(keywordO);
			systemO.field("keywords", keywords, OType.LINKLIST);
			systemO.save();
		}
	}
	
	//STORE
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
        if (cl.getSystem()==null) {
            cl.setSystem(createCodeSystemName(database));
        }
        ODocument csmain = loadDAO.loadSystemO(cl.getSystem(), cl.getVersion(), database);
        if (csmain!=null)
            throw new OIndexException("Found duplicated key");

        counter  = 0;
        csmain = database.createVertex("CSVersion");
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
        Collection<ODocument> keywords = new ArrayList<ODocument>();
        if (cl.getKeyWords()!=null)
            for (String keyword : cl.getKeyWords())
                keywords.add(storeKeyword(keyword, database));
        csmain.field("keywords", keywords.size()>0 ? keywords : null, OType.LINKLIST);

		if (cl.getSource()!=null)
			csmain.field("source", cmStoreDAO.storeContactIdentity(cl.getSource(), database));
		if (cl.getProvider()!=null)
			csmain.field("provider", cmStoreDAO.storeContactIdentity(cl.getProvider(), database));

		//code list
        csmain.save();
        cl.resetLevels();
		Collection<ODocument> rootCodes = new ArrayList<ODocument>();
		if (cl.getRootCodes()!=null)
			for (Code code : cl.getRootCodes())
			    rootCodes.add(storeCode(code, csmain, null, true, database));
		csmain.field("rootCodes", rootCodes.size()>0 ? rootCodes : null, OType.LINKLIST);

		csmain.save();
		if (cl.getRegion()!=null)
			csmain.field("region", loadDAO.loadCodeO(cl.getRegion().getSystemKey(), cl.getRegion().getSystemVersion(), cl.getRegion().getCode(), database));
		if (cl.getCategory()!=null)
			csmain.field("category", loadDAO.loadCodeO(cl.getCategory().getSystemKey(), cl.getCategory().getSystemVersion(), cl.getCategory().getCode(), database));

        //Index
        index.rebuildIndex(csmain,false);

        return csmain.save();
	}

	public ODocument storeKeyword(String keyword, OGraphDatabase database) throws Exception {
		ODocument cskeyword = loadDAO.loadKeywordO(keyword, database);
		return cskeyword!=null ? cskeyword : database.createVertex("CSKeyword").field("keyword", keyword).save();
	}
	
	private int counter;
	private ODocument storeCode(Code code, ODocument system, ODocument parent, boolean storeChilds, OGraphDatabase database) throws Exception {
		ODocument codeO = loadDAO.loadCodeO(system, code.getCode(), database);
		if (codeO==null) {
			codeO = database.createVertex("CSCode");
			codeO.field("code", code.getCode());
			codeO.field("level", code.getLevel());
			codeO.field("title", code.getTitle());
			codeO.field("abstract", code.getDescription());
			codeO.field("supplemental", code.getSupplemental());
            codeO.field("fromDate", code.getFromDate());
            codeO.field("toDate", code.getToDate());

			//connected hierarchy elements
			codeO.field("system", system);
			
			codeO.save();
            if (storeChilds) {
                Collection<ODocument> childs = new LinkedList<ODocument>();
                if (code.getChilds()!=null)
                    for (Code child : code.getChilds())
                        childs.add(storeCode(child, system, codeO, storeChilds, database));
                codeO.field("childs", childs);
            }
			
			Collection<ODocument> exclusionList = new LinkedList<ODocument>();
			if (code.getExclusionList()!=null)
				for (Code exclusion : code.getExclusionList())
					exclusionList.add(loadDAO.loadCodeO(exclusion.getSystemKey(), exclusion.getSystemVersion(), exclusion.getCode(), database));
			codeO.field("exclusions",exclusionList.size()>0 ? exclusionList : null, OType.LINKLIST);

            index.rebuildCodeIndex(codeO);

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
			Collection<ODocument> parents = (Collection<ODocument>)codeO.field("parents");
			if (parents==null)
				parents = new LinkedList<ODocument>();
			parents.add(parent);
			codeO.field("parents", parents);
		}

		return codeO.save();
	}

	
	//Utils
	public String createCodeSystemName(OGraphDatabase database) {
        return "_DynamicCodeSystem_"+new com.eaio.uuid.UUID().toString();
	}

    //Hierarchy utils

    public int createHierarchyLinks() throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try { return createHierarchyLinks(database);
        } finally { if (database!=null) database.close(); }
    }
    public int createHierarchyLinks(OGraphDatabase database) throws Exception {
    	int count = 0;
    	for (ODocument systemO : loadDAO.loadSystemO(database))
    		count += createHierarchyLinks(systemO, database);
    	return count;
    }
    public int createHierarchyLinks(String system, String version) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try { return createHierarchyLinks(system, version, database);
        } finally { if (database!=null) database.close(); }
    }
    public int createHierarchyLinks(String system, String version, OGraphDatabase database) throws Exception {
        return createHierarchyLinks(loadDAO.loadSystemO(system, version, database), database);
    }
    @SuppressWarnings("unchecked")
	public int createHierarchyLinks(ODocument clO, OGraphDatabase database) throws Exception {
        //Remove old hierarchy informations
        storeLinkDAO.deleteCodeHierarchies(clO,database);
        //Add hierarchy informations
        Collection<ODocument> rootCodes = (Collection<ODocument>)clO.field("rootCodes");
        int count = 0;
        if (rootCodes!=null)
            for (ODocument codeO : rootCodes)
            	count+=createHierarchyLink(codeO,database);
        return count;
    }
    
	public int createHierarchyLink(ODocument parentO, OGraphDatabase database) throws Exception {
        int count = 0;
        Collection<ODocument> childrenO = parentO.field("childs");
        if (childrenO!=null && childrenO.size()>0) {
            for (ODocument childO : childrenO) {
                count += createHierarchyLink(childO,database)+1;
                storeLinkDAO.storeCodeHierarchy(parentO,childO,CSHierarchyType.child,database);
                parentO.save(); childO.save();
                storeLinkDAO.storeCodeHierarchy(childO,parentO,CSHierarchyType.parent,database);
                parentO.save(); childO.save();
            }
        }
        return count;
    }

}
