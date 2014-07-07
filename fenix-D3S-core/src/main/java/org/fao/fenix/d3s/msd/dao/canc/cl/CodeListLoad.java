package org.fao.fenix.d3s.msd.dao.canc.cl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.fao.fenix.commons.msd.dto.templates.canc.cl.Code;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodeSystem;
import org.fao.fenix.d3s.server.tools.SupportedLanguages;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
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
		ODocument systemO = loadSystemO(system, version);
		return systemO!=null ? converter.toSystem(systemO, all) : null;
	}
	public synchronized ODocument loadSystemO(String system, String version) throws Exception {
		queryLoadSystem.reset();
		queryLoadSystem.resetPagination();
		List<ODocument> result = getConnection().query(queryLoadSystem, system, version);
		return result.size()==1 ? result.get(0) : null;
	}

    //Load ALL code lists
	public Collection<CodeSystem> loadSystem(boolean all) throws Exception {
		Collection<CodeSystem> result = new LinkedList<CodeSystem>();
		for (ODocument systemO : loadSystemO())
				result.add(converter.toSystem(systemO, all));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadSystemO() throws Exception {
		queryLoadAllSystem.reset();
		queryLoadAllSystem.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadAllSystem);
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
        return browseClass("CSVersion");
	}
    public long countSystems() throws Exception {
        return countClass("CSVersion");
    }


    //Load by single keyword
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadSystemByKeywordO(ODocument keyword) throws Exception {
		queryLoadSystemByKeyword.reset();
		queryLoadSystemByKeyword.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadSystemByKeyword, keyword);
	}

	
	//Load code
	public Code loadCode(String system, String version, String code, Integer levels) throws Exception {
		ODocument codeO = loadCodeO(system, version, code);
		return codeO!=null ? converter.toCode(codeO, true, levels!=null?levels:CodeListConverter.NO_LEVELS,null) : null;
	}
	public synchronized ODocument loadCodeO(String system, String version, String code) throws Exception {
		return loadCodeO(loadSystemO(system, version),code);
	}
	public synchronized ODocument loadCodeO(ODocument systemO, String code) throws Exception {
		queryLoadCode.reset();
		queryLoadCode.resetPagination();
		List<ODocument> result = getConnection().query(queryLoadCode,systemO,code);
		return result.size()==1 ? result.get(0) : null;
	}

	//Load codes
    //TODO verificare anomalia con test in multithreading
	public synchronized Collection<Code> loadCodes(String system, String version, Collection<String> codes, Integer levels) throws Exception {
		Collection <ODocument> codeO = loadCodesO(system, version, codes);
		List<Code> toRet = new LinkedList<Code>();

		if (codeO==null)
			return toRet;
		
		for (ODocument cO: codeO)
				toRet.add(converter.toCode(cO, true, levels!=null?levels:CodeListConverter.NO_LEVELS, null));

		return toRet;
	}
	

	public synchronized List<ODocument> loadCodesO(String system, String version, Collection<String> codes) throws Exception {
		queryLoadCodes.reset();
		queryLoadCodes.resetPagination();
		List<ODocument> result = getConnection().query(queryLoadCodes,loadSystemO(system, version),codes);
		return result;
	}

	//Load branch
	public Collection<Code> loadCodeBranch(String system, String version, String code) throws Exception {
		return loadCodeBranch(loadCodeO(system, version, code));
	}
	public Collection<Code> loadCodeBranch(ODocument sourceCodeO) throws Exception {
		return converter.toCode(loadCodeBranchO(sourceCodeO),true);
	}
	public Collection<ODocument> loadCodeBranchO(String system, String version, String code) throws Exception {
		return loadCodeBranchO(loadCodeO(system, version, code));
	}
	@SuppressWarnings("unchecked")
	public Collection<ODocument> loadCodeBranchO(ODocument codeO) throws Exception {
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
		return converter.toCode(loadCodeLevelO(system, version, level), true);
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadCodeLevelO(String system, String version, Integer level) throws Exception {
		queryLoadCodeLevel.reset();
		queryLoadCodeLevel.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadCodeLevel,loadSystemO(system, version),level);
	}

	@SuppressWarnings("unchecked")
	public Collection<ODocument> loadCodeLevelO(ODocument codeO) throws Exception {
		if (codeO==null)
			return null;
		ODocument parentO = codeO.field("parent");
		return parentO!=null ? (Collection<ODocument>)parentO.field("childs") : (Collection<ODocument>)((ODocument)codeO.field("system")).field("rootCodes");
	}
	
	
	//LOAD CODE RELATIONS
	
	
	//Load keyword
	public synchronized ODocument loadKeywordO(String keyword) throws Exception {
		queryLoadKeyword.reset();
		queryLoadKeyword.resetPagination();
		List<ODocument> result = getConnection().query(queryLoadKeyword,keyword);
		return result.size()==1 ? result.get(0) : null;
	}
	
	public Collection<String> loadKeyword() throws Exception {
		Collection<String> result = new LinkedList<String>();
		for (ODocument keywordO : loadKeywordO())
			result.add((String)keywordO.field("keyword"));
		return result;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadKeywordO() throws Exception {
		queryLoadAllKeywords.reset();
		queryLoadAllKeywords.resetPagination();
		return (Collection<ODocument>)getConnection().query(queryLoadAllKeywords);
	}


    //Hierarchy relationship check
    public boolean hasChild(Code parent, Code child) throws Exception {
        return hasChildO(loadCodeO(parent.getSystemKey(), parent.getSystemVersion(), parent.getCode()), loadCodeO(child.getSystemKey(), child.getSystemVersion(), child.getCode()));
    }

    public boolean hasChild(ORID parent, ORID child) throws Exception {
        OGraphDatabase database = getConnection();
        return hasChildO((ODocument)database.load(parent), (ODocument)database.load(child));
    }

    @SuppressWarnings("unchecked")
    public boolean hasChildO(ODocument parentO, ODocument childO) throws Exception {
        if (childO.equals(parentO))
            return true;

        Collection<ODocument> parentsO = (Collection<ODocument>) childO.field("parents");
        if (parentsO==null)
            return false;

        for (ODocument cO : parentsO)
            if (hasChildO(parentO, cO))
                return true;

        return false;
    }


    //Load codes or codelists by title
    public Collection<Code> loadCodesByTitle(String text, SupportedLanguages language) throws Exception {
        return converter.toCode(loadByTitleO(text, language, false),false);
    }

    public Collection<CodeSystem> loadCodeSystemsByTitle(String text, SupportedLanguages language) throws Exception {
        return converter.toSystem(loadByTitleO(text, language, true));
    }

    public synchronized Collection<ODocument> loadByTitleO(String text, SupportedLanguages language, boolean codeList) throws Exception {
        StringBuilder query = new StringBuilder("select ");
        if (codeList)
            query.append("distinct(system) as system ");
        query.append("from CSCode where index_title_").append(language.getCode()).append(" containstext ?");

        Collection<ODocument> data = new LinkedList<>();
        for (String word : text.split("[^\\w%]+"))
            if (codeList)
                for (ODocument id : (Collection<ODocument>)getConnection().query(new OSQLSynchQuery<ODocument>(query.toString()),word))
                    data.add((ODocument)id.field("system"));
            else
                data.addAll ((Collection<ODocument>)getConnection().query(new OSQLSynchQuery<ODocument>(query.toString()),word));
        return data;
    }

    ////////////

}
