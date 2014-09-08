package org.fao.fenix.d3s.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.search.dto.OutputParameters;

import com.orientechnologies.orient.core.record.impl.ODocument;


public class SearchFlow {

    private long pid;
    private Collection<ODocument> involvedDatasets;
	private Map<ODocument, ResourceFilter> encodedFilters;
	private Map<ODocument,Map<String,ODocument>> columnsByDimension;
    private Map<String,Object> businessParameters;
    private Map<String,OutputParameters> businessOutputParameters;
    private Map<Code,ODocument> loadedCodes;
    private Map<CodeSystem,ODocument> loadedSystems;
    private Map<String,Object> attributes;


    public SearchFlow () {
        reset();
    }

    public void reset() {
        pid = Thread.currentThread().getId()+System.currentTimeMillis();
        involvedDatasets = null;
        encodedFilters = new HashMap<>();
        columnsByDimension = new HashMap<>();
        businessParameters = new HashMap<>();
        businessOutputParameters = new HashMap<>();
        loadedCodes = new HashMap<>();
        loadedSystems = new HashMap<>();
        attributes = new HashMap<>();
    }

	//GET-SET
	public long getPid() {
        return pid;
    }

    public Map<ODocument, ResourceFilter> getEncodedFilters() {
		return encodedFilters;
	}

    public Map<String, Object> getBusinessParameters() {
        return businessParameters;
    }
    public Object getBusinessParameter(String key) {
        return businessParameters.get(key);
    }

    public void setBusinessParameters(Map<String, Object> businessParameters) {
        this.businessParameters.clear();
        if (businessParameters!=null)
            this.businessParameters.putAll(businessParameters);
    }
    public void putBusinessParameter(String key, Object value) {
        businessParameters.put(key,value);
    }

    public Map<String, OutputParameters> getBusinessOutputParameters() {
        return businessOutputParameters;
    }
    public OutputParameters getBusinessOutputParameter(String key) {
        return businessOutputParameters.get(key);
    }

    public void setBusinessOutputParameters(Map<String, OutputParameters> businessOutputParameters) {
        this.businessOutputParameters.clear();
        if (businessOutputParameters!=null)
            this.businessOutputParameters.putAll(businessOutputParameters);
    }
    public void putBusinessOutputParameter(String key, OutputParameters value) {
        businessOutputParameters.put(key, value);
    }

    public Map<Code,ODocument> getLoadedCodes () {
        return loadedCodes;
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
    public void addEncodedFilter(ODocument dataset, ResourceFilter customFilter) {
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
    public ODocument getLoadedCode(Code code, CodeListLoad clLoadDao) throws Exception {
        ODocument codeO = loadedCodes.get(code);
        if (codeO == null)
            loadedCodes.put(code,codeO=clLoadDao.loadCodeO(getLoadedSystem(code.getSystemKey(),code.getSystemVersion(), clLoadDao),code.getCode()));
        return codeO;
    }
    public ODocument getLoadedCode(String system, String version, String code, CodeListLoad clLoadDao) throws Exception {
        return getLoadedCode(new Code(system,version,code), clLoadDao);
    }
    public ODocument getLoadedSystem(CodeSystem system, CodeListLoad clLoadDao) throws Exception {
        ODocument systemO = loadedCodes.get(system);
        if (systemO == null)
            loadedSystems.put(system, systemO = clLoadDao.loadSystemO(system.getSystem(), system.getVersion()));
        return systemO;
    }
    public ODocument getLoadedSystem(String system, String version, CodeListLoad clLoadDao) throws Exception {
        return getLoadedSystem(new CodeSystem(system,version), clLoadDao);
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
