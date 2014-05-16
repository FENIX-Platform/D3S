package org.fao.fenix.d3s.msd.dao.cl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.server.tools.SupportedLanguages;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.commons.utils.CompletenessIterator;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import javax.inject.Inject;

public class CodeListLoad extends OrientDao {
	
	@Inject private CodeListConverter converter;

	private static OSQLSynchQuery<ODocument> queryLoadSystem = new OSQLSynchQuery<ODocument>("select from CSVersion where system = ? and version = ?");
	private static OSQLSynchQuery<ODocument> queryLoadAllSystem = new OSQLSynchQuery<ODocument>("select from CSVersion");
	private static OSQLSynchQuery<ODocument> queryLoadSystemByKeyword = new OSQLSynchQuery<ODocument>("select from CSVersion where ? in keywords");
	private static OSQLSynchQuery<ODocument> queryLoadCode = new OSQLSynchQuery<ODocument>("select from CSCode where system = ? and code = ?");
	private static OSQLSynchQuery<ODocument> queryLoadCodes = new OSQLSynchQuery<ODocument>("select from CSCode where system = ? and code in ?");
	private static OSQLSynchQuery<ODocument> queryLoadCodeLevel = new OSQLSynchQuery<ODocument>("select from CSCode where system = ? and level = ? order by code");
	private static OSQLSynchQuery<ODocument> queryLoadKeyword = new OSQLSynchQuery<ODocument>("select from CSKeyword where keyword = ?");
	private static OSQLSynchQuery<ODocument> queryLoadAllKeywords = new OSQLSynchQuery<ODocument>("select from CSKeyword");

	//Load system
	public CodeSystem loadSystem(String system, String version, boolean all) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadSystem(system, version, database, all);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public CodeSystem loadSystem(String system, String version, OGraphDatabase database, boolean all) throws Exception {
		ODocument systemO = loadSystemO(system, version, database);
		return systemO!=null ? converter.toSystem(systemO, all) : null;
	}
	public synchronized ODocument loadSystemO(String system, String version, OGraphDatabase database) throws Exception {
		queryLoadSystem.reset();
		queryLoadSystem.resetPagination();
		List<ODocument> result = database.query(queryLoadSystem, system, version);
		return result.size()==1 ? result.get(0) : null;
	}

    //Load ALL code lists
	public Collection<CodeSystem> loadSystem(boolean all) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadSystem(database, all);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<CodeSystem> loadSystem(OGraphDatabase database, boolean all) throws Exception {
		Collection<CodeSystem> result = new LinkedList<CodeSystem>();
		for (ODocument systemO : loadSystemO(database))
				result.add(converter.toSystem(systemO, all));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadSystemO(OGraphDatabase database) throws Exception {
		queryLoadAllSystem.reset();
		queryLoadAllSystem.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadAllSystem);
	}

    //Load ALL code iterable
	public CompletenessIterator<CodeSystem> loadSystemProducer(final boolean all) throws Exception {
        final Iterator<ODocument> producerO = loadSystemProducerO().iterator();
        return new CompletenessIterator<CodeSystem>() {
            int index = 0;
            @Override public int getIndex() { return index; }
            @Override public void remove() { producerO.remove(); }
            @Override public boolean hasNext() { return producerO.hasNext(); }
            @Override public CodeSystem next() {
                if (producerO.hasNext()) {
                    index++;
                    return converter.toSystem(producerO.next(),all);
                } else
                    return null;
            }
        };
	}
	public synchronized Iterable<ODocument> loadSystemProducerO() throws Exception {
        return browseClass("CSVersion",OrientDatabase.msd);
	}
    public long countSystems() throws Exception {
        return countClass("CSVersion",OrientDatabase.msd);
    }


    //Load by single keyword
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadSystemByKeywordO(ODocument keyword, OGraphDatabase database) throws Exception {
		queryLoadSystemByKeyword.reset();
		queryLoadSystemByKeyword.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadSystemByKeyword, keyword);
	}

	
	//Load code
	public Code loadCode(String system, String version, String code, Integer levels) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadCode(system, version, code, database, levels);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Code loadCode(String system, String version, String code, OGraphDatabase database, Integer levels) throws Exception {
		ODocument codeO = loadCodeO(system, version, code, database);
		return codeO!=null ? converter.toCode(codeO, true, levels!=null?levels:CodeListConverter.NO_LEVELS,null) : null;
	}
	public synchronized ODocument loadCodeO(String system, String version, String code, OGraphDatabase database) throws Exception {
		return loadCodeO(loadSystemO(system, version, database),code, database);
	}
	public synchronized ODocument loadCodeO(ODocument systemO, String code, OGraphDatabase database) throws Exception {
		queryLoadCode.reset();
		queryLoadCode.resetPagination();
		List<ODocument> result = database.query(queryLoadCode,systemO,code);
		return result.size()==1 ? result.get(0) : null;
	}

	//Load codes
	public Collection<Code> loadCodes(String system, String version, Collection<String> codes, Integer levels) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadCodes(system, version, codes, database, levels);
		} finally {
			if (database!=null)
				database.close();
		}
	}

    //TODO verificare anomalia con test in multithreading
	public synchronized Collection<Code> loadCodes(String system, String version, Collection<String> codes, OGraphDatabase database, Integer levels) throws Exception {
		Collection <ODocument> codeO = loadCodesO(system, version, codes, database);
		List<Code> toRet = new LinkedList<Code>();

		if (codeO==null)
			return toRet;
		
		for (ODocument cO: codeO)
				toRet.add(converter.toCode(cO, true, levels!=null?levels:CodeListConverter.NO_LEVELS, null));

		return toRet;
	}
	

	public synchronized List<ODocument> loadCodesO(String system, String version, Collection<String> codes, OGraphDatabase database) throws Exception {
		queryLoadCodes.reset();
		queryLoadCodes.resetPagination();
		List<ODocument> result = database.query(queryLoadCodes,loadSystemO(system, version, database),codes);
		return result;
	}

	//Load branch
	public Collection<Code> loadCodeBranch(String system, String version, String code) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadCodeBranch(loadCodeO(system, version, code, database), database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<Code> loadCodeBranch(String system, String version, String code, OGraphDatabase database) throws Exception {
		return loadCodeBranch(loadCodeO(system, version, code, database), database);
	}
	public Collection<Code> loadCodeBranch(ODocument sourceCodeO, OGraphDatabase database) throws Exception {
		return converter.toCode(loadCodeBranchO(sourceCodeO, database),true);
	}
	public Collection<ODocument> loadCodeBranchO(String system, String version, String code, OGraphDatabase database) throws Exception {
		return loadCodeBranchO(loadCodeO(system, version, code, database), database);
	}
	@SuppressWarnings("unchecked")
	public Collection<ODocument> loadCodeBranchO(ODocument codeO, OGraphDatabase database) throws Exception {
		LinkedList<ODocument> branchO = new LinkedList<ODocument>();
		branchO.push(codeO);
		for (Collection<ODocument> parentsO = (Collection<ODocument>)codeO.field("parents"); parentsO!=null; parentsO = (Collection<ODocument>)codeO.field("parents"))
			if (parentsO.size()==1)
				branchO.push(codeO=parentsO.iterator().next());
			else
				break;
		
		return branchO;
	}
	
	
	//Load code level
	public Collection<Code> loadCodeLevel(String system, String version, Integer level) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadCodeLevel(system, version, level, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<Code> loadCodeLevel(String system, String version, Integer level, OGraphDatabase database) throws Exception {
		return converter.toCode(loadCodeLevelO(system, version, level, database), true);
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadCodeLevelO(String system, String version, Integer level, OGraphDatabase database) throws Exception {
		queryLoadCodeLevel.reset();
		queryLoadCodeLevel.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadCodeLevel,loadSystemO(system, version, database),level);
	}

	@SuppressWarnings("unchecked")
	public Collection<ODocument> loadCodeLevelO(ODocument codeO, OGraphDatabase database) throws Exception {
		if (codeO==null)
			return null;
		ODocument parentO = codeO.field("parent");
		return parentO!=null ? (Collection<ODocument>)parentO.field("childs") : (Collection<ODocument>)((ODocument)codeO.field("system")).field("rootCodes");
	}
	
	
	//LOAD CODE RELATIONS
	
	
	//Load keyword
	public synchronized ODocument loadKeywordO(String keyword, OGraphDatabase database) throws Exception {
		queryLoadKeyword.reset();
		queryLoadKeyword.resetPagination();
		List<ODocument> result = database.query(queryLoadKeyword,keyword);
		return result.size()==1 ? result.get(0) : null;
	}
	
	public Collection<String> loadKeyword() throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadKeyword(database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<String> loadKeyword(OGraphDatabase database) throws Exception {
		Collection<String> result = new LinkedList<String>();
		for (ODocument keywordO : loadKeywordO(database))
			result.add((String)keywordO.field("keyword"));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadKeywordO(OGraphDatabase database) throws Exception {
		queryLoadAllKeywords.reset();
		queryLoadAllKeywords.resetPagination();
		return (Collection<ODocument>)database.query(queryLoadAllKeywords);
	}


    //Hierarchy relationship check
    public boolean hasChild(Code parent, Code child) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            return hasChildO(loadCodeO(parent.getSystemKey(), parent.getSystemVersion(), parent.getCode(), database), loadCodeO(child.getSystemKey(), child.getSystemVersion(), child.getCode(), database), database);
        } finally {
            if (database!=null)
                database.close();
        }
    }

    public boolean hasChild(ORID parent, ORID child, OGraphDatabase database) throws Exception {
        return hasChildO((ODocument)database.load(parent), (ODocument)database.load(child), database);
    }

    @SuppressWarnings("unchecked")
    public boolean hasChildO(ODocument parentO, ODocument childO, OGraphDatabase database) throws Exception {
        if (childO.equals(parentO))
            return true;

        Collection<ODocument> parentsO = (Collection<ODocument>) childO.field("parents");
        if (parentsO==null)
            return false;

        for (ODocument cO : parentsO)
            if (hasChildO(parentO, cO, database))
                return true;

        return false;
    }


    //Load codes or codelists by title
    public Collection<Code> loadCodesByTitle(String text, SupportedLanguages language) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            return loadCodesByTitle(text, language, database);
        } finally {
            if (database!=null)
                database.close();
        }
    }
    public Collection<Code> loadCodesByTitle(String text, SupportedLanguages language, OGraphDatabase database) throws Exception {
        return converter.toCode(loadByTitleO(text, language, false, database),false);
    }

    public Collection<CodeSystem> loadCodeSystemsByTitle(String text, SupportedLanguages language) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            return loadCodeSystemsByTitle(text, language, database);
        } finally {
            if (database!=null)
                database.close();
        }
    }
    public Collection<CodeSystem> loadCodeSystemsByTitle(String text, SupportedLanguages language, OGraphDatabase database) throws Exception {
        return converter.toSystem(loadByTitleO(text, language, true, database));
    }

    public synchronized Collection<ODocument> loadByTitleO(String text, SupportedLanguages language, boolean codeList, OGraphDatabase database) throws Exception {
        StringBuilder query = new StringBuilder("select ");
        if (codeList)
            query.append("distinct(system) as system ");
        query.append("from CSCode where index_title_").append(language.getCode()).append(" containstext ?");

        Collection<ODocument> data = new LinkedList<>();
        for (String word : text.split("[^\\w%]+"))
            if (codeList)
                for (ODocument id : (Collection<ODocument>)database.query(new OSQLSynchQuery<ODocument>(query.toString()),word))
                    data.add((ODocument)id.field("system"));
            else
                data.addAll ((Collection<ODocument>)database.query(new OSQLSynchQuery<ODocument>(query.toString()),word));
        return data;
    }

    ////////////

}
