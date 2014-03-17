package org.fao.fenix.d3s.cache.impl;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.cache.Cache;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.commons.utils.SoftMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("prototype")
public class RamCache extends Cache {

    class DatasetsStatus {

        int datasetsNumber;
        String uids;
        String updates;
        SearchStep data;

        DatasetsStatus(SearchStep source, Collection<ODocument> datasets) {
            StringBuilder uidsBuffer = new StringBuilder();
            StringBuilder updatesBuffer = new StringBuilder();

            if (datasets!=null) {
                for (ODocument dataset : datasets) {
                    uidsBuffer.append(dataset.field("uid"));
                    Date updateDate = dataset.field("updateDate");
                    updatesBuffer.append(updateDate!=null ? updateDate.getTime() : '|');
                }
                datasetsNumber = datasets.size();
                uids = uidsBuffer.toString();
                updates = updatesBuffer.toString();
            }

            if (source!=null && source.hasData()) {
                data = source;
                Collection<Object[]> data = new LinkedList<Object[]>();
                for (Object[] row : source)
                    data.add(row);
                source.setData(data);
            }
        }

        //Utils

        @Override
        public boolean equals(Object obj) {
            DatasetsStatus status = obj!=null && obj instanceof DatasetsStatus ? (DatasetsStatus)obj : null;
            return status!=null && status.datasetsNumber==datasetsNumber && status.uids.equals(uids) && status.updates.equals(updates);
        }
    }

    private static Map<String,DatasetsStatus> datasetsStatus = new SoftMap<String, DatasetsStatus>();


    @Override protected void initCache(Properties cacheProperties) throws Exception { }

    @Override
	public void storeData(ResourceFilter filter, Collection<ODocument> datasets, SearchStep value) throws Exception {
        cloneResult(value);
        if (filter!=null)
            datasetsStatus.put(getKey(filter),new DatasetsStatus(this, datasets));
	}


	@Override
	public void loadData(ResourceFilter filter, Collection<ODocument> datasets) throws Exception {
        data = null;
        String key = getKey(filter);
        DatasetsStatus status = datasetsStatus.get(key);
        if (status!=null)
            if (status.equals(new DatasetsStatus(null,datasets)))
                cloneResult(status.data);
            else
                datasetsStatus.remove(key);
	}

}
