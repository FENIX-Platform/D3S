package org.fao.fenix.d3s.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dto.cl.Code;
import org.fao.fenix.d3s.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.search.dto.SearchFilter;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;

public class SearchFlow {

    private CodeListLoad clLoadDao = SpringContext.getBean(CodeListLoad.class);

    private long pid;
    private OGraphDatabase msdDatabase;
    private Collection<ODocument> involvedDatasets;
	private Map<ODocument, SearchFilter> encodedFilters = new HashMap<ODocument, SearchFilter>();
	private Map<ODocument,Map<String,ODocument>> columnsByDimension = new HashMap<ODocument, Map<String,ODocument>>();
    private Map<String,Object> businessParameters = new HashMap<String, Object>();
    private Map<Code,ODocument> loadedCodes = new HashMap<Code, ODocument>();
    private Map<CodeSystem,ODocument> loadedSystems = new HashMap<CodeSystem, ODocument>();
    private Map<String,Object> attributes = new HashMap<String, Object>();


    SearchFlow() {
        pid = Thread.currentThread().getId()+System.currentTimeMillis();
    }

	//GET-SET
	public long getPid() {
        return pid;
    }

    public Map<ODocument, SearchFilter> getEncodedFilters() {
		return encodedFilters;
	}

    public Map<String, Object> getBusinessParameters() {
        return businessParameters;
    }

    public void setBusinessParameters(Map<String, Object> businessParameters) {
        this.businessParameters.clear();
        if (businessParameters!=null)
            this.businessParameters.putAll(businessParameters);
    }

    public Map<Code,ODocument> getLoadedCodes () {
        return loadedCodes;
    }

    public OGraphDatabase getMsdDatabase() {
        return msdDatabase;
    }

    public void setMsdDatabase(OGraphDatabase msdDatabase) {
        this.msdDatabase = msdDatabase;
    }

    public Map<CodeSystem, ODocument> getLoadedSystems() {
        return loadedSystems;
    }

    public void setLoadedSystems(Map<CodeSystem, ODocument> loadedSystems) {
        this.loadedSystems = loadedSystems;
    }

    public Collection<ODocument> getInvolvedDatasets() {
        return involvedDatasets;
    }

    public void setInvolvedDatasets(Collection<ODocument> involvedDatasets) {
        this.involvedDatasets = involvedDatasets;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    //Utils
    public void addEncodedFilter(ODocument dataset, SearchFilter customFilter) {
        encodedFilters.put(dataset, customFilter);
    }
    public void addBusinessParameter(String key, String value) {
        businessParameters.put(key, value);
    }
    public void addLoadedCode(Code code, ODocument codeO) {
        loadedCodes.put(code,codeO);
    }
    public void addLoadedSystem(CodeSystem system, ODocument codeO) {
        loadedSystems.put(system,codeO);
    }
    public ODocument getLoadedCode(Code code) throws Exception {
        ODocument codeO = loadedCodes.get(code);
        if (codeO == null)
            loadedCodes.put(code,codeO=clLoadDao.loadCodeO(getLoadedSystem(code.getSystemKey(),code.getSystemVersion()),code.getCode(),getMsdDatabase()));
        return codeO;
    }
    public ODocument getLoadedCode(String system, String version, String code) throws Exception {
        return getLoadedCode(new Code(system,version,code));
    }
    public ODocument getLoadedSystem(CodeSystem system) throws Exception {
        ODocument systemO = loadedCodes.get(system);
        if (systemO == null)
            loadedSystems.put(system, systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion(), getMsdDatabase()));
        return systemO;
    }
    public ODocument getLoadedSystem(String system, String version) throws Exception {
        return getLoadedSystem(new CodeSystem(system,version));
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    public void setAttribute(String key, Object value) {
        attributes.put(key,value);
    }



	@SuppressWarnings("unchecked")
	public Map<String,ODocument> getColumnsByDimension(ODocument dataset) {
		Map<String,ODocument> result = columnsByDimension.get(dataset);
		if (result==null) {
			columnsByDimension.put(dataset, result = new HashMap<String,ODocument>());
			for (ODocument column : (Collection<ODocument>)dataset.field("dsd.columns"))
				result.put((String)column.field("dimension.name"), column);
		}
		return result;
	}
	
}
