package org.fao.fenix.d3s.cache;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.fao.fenix.commons.search.dto.filter.ColumnValueFilter;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.OutputParameters;
import org.fao.fenix.d3s.search.dto.SearchFilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public abstract class Cache extends SearchStep {
	
	private boolean initialized = false;
	public final void init(String propertyPrefix) throws Exception {
		if (!initialized) {
            Properties cacheProperties = new Properties();
			if (propertyPrefix==null)
				propertyPrefix = "";
			int beginIndex = propertyPrefix.length();
			for (Object propertyName : initProperties.keySet())
				if (((String)propertyName).startsWith(propertyPrefix))
                    cacheProperties.setProperty(((String)propertyName).substring(beginIndex), initProperties.getProperty((String)propertyName));
			initCache(cacheProperties);
            initialized = true;
		}
	}
    protected abstract void initCache(Properties cacheProperties) throws Exception;
	public final boolean isInitialized() { return initialized; }

    public void storeData(ResourceFilter key, ODocument dataset, SearchStep value) throws Exception { storeData(key, Arrays.asList(dataset), value); }
    public void loadData(ResourceFilter key, ODocument dataset) throws Exception { loadData(key, Arrays.asList(dataset)); }

	public abstract void storeData(ResourceFilter key, Collection<ODocument> dataset, SearchStep value) throws Exception;
	public abstract void loadData(ResourceFilter key, Collection<ODocument> dataset) throws Exception;



    //Find unique filter key
    private static final String[] keyBusinessParameters = new String[] {
            "globalAggregation",
            "firstAggregation",
            "secondAggregation"
    };

    @JsonIgnore
    public String getKey(ResourceFilter filter) {
        StringBuilder key = new StringBuilder();
        getKey(key,filter.getMetadata());
        getKey(key,filter.getData());
        getOutParamsKey(key,getFlow().getBusinessOutputParameters());
        getBusinessParamsKey(key,getFlow().getBusinessParameters());
        return key.toString();
    }
    @JsonIgnore
    private void getKey(StringBuilder key, Map<String, Collection<ColumnValueFilter>> filter) {
        if (filter!=null)
            for (Map.Entry<String, Collection<ColumnValueFilter>> filterElement : filter.entrySet()) {
                key.append(filterElement.getKey());
                Collection<ColumnValueFilter> filterValue = filterElement.getValue();
                if (filterValue!=null)
                    for (ColumnValueFilter filterValueInstance : filterValue)
                        key.append(filterValueInstance.getKey());
            }
    }

    private void getOutParamsKey(StringBuilder key, Map<String,OutputParameters> outParams) {
        if (outParams!=null)
            for (Map.Entry<String, OutputParameters> filterElement : outParams.entrySet()) {
                key.append(filterElement.getKey());
                OutputParameters filterValue = filterElement.getValue();
                if (filterValue!=null)
                    key.append(filterValue.getIdKey());
            }
    }

    private void getBusinessParamsKey(StringBuilder key, Map<String,Object> businessParams) {
        if (businessParams!=null) {
            for (String keyParam : keyBusinessParameters) {
                Object paramValue = businessParams.get(keyParam);
                if (paramValue!=null)
                    key.append(keyParam).append(paramValue.hashCode());
            }

        }

    }

}
