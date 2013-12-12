package org.fao.fenix.cache;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.dto.SearchFilter;

import java.util.Arrays;
import java.util.Collection;
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

    public void storeData(SearchFilter key, ODocument dataset, SearchStep value) throws Exception { storeData(key, Arrays.asList(dataset), value); }
    public void loadData(SearchFilter key, ODocument dataset) throws Exception { loadData(key, Arrays.asList(dataset)); }

	public abstract void storeData(SearchFilter key, Collection<ODocument> dataset, SearchStep value) throws Exception;
	public abstract void loadData(SearchFilter key, Collection<ODocument> dataset) throws Exception;
	

}
