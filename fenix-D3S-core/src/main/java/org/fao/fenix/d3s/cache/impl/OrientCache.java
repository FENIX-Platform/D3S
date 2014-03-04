package org.fao.fenix.d3s.cache.impl;

import java.util.Collection;
import java.util.Properties;

import org.fao.fenix.d3s.cache.Cache;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
@Scope("prototype")
public class OrientCache extends Cache {

    @Override
    protected void initCache(Properties cacheProperties) throws Exception {
        String url = cacheProperties.getProperty("url");
    }

    @Override
	public void storeData(SearchFilter filter, Collection<ODocument> dataset, SearchStep value) throws Exception {
        cloneResult(value);
		// TODO Auto-generated method stub
	}

	@Override
	public void loadData(SearchFilter filter, Collection<ODocument> dataset) throws Exception {
        data = null;
		// TODO Auto-generated method stub
	}

}
