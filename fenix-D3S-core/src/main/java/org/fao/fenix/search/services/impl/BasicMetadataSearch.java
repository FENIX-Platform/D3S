package org.fao.fenix.search.services.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.fao.fenix.msd.dao.dm.DMConverter;
import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.fao.fenix.search.bl.datasetFilter.DatasetPropertiesFilter;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.search.dto.SearchMetadataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class BasicMetadataSearch extends SearchOperation {

	//Component dependencies
	@Autowired private DatasetPropertiesFilter mainFilter;
//	@Autowired private SearchConverterFilter encodeFilter;
	@Autowired private DMConverter dmConverter;


	//Search main flow
	@Override
	@SuppressWarnings("unchecked")
	public SearchMetadataResponse searchFlow(SearchFilter filter) throws Exception {
		SearchMetadataResponse result = new SearchMetadataResponse();
		OGraphDatabase database = getFlow().getMsdDatabase();

        //Dataset filtering
        Collection<ODocument> datasets = search(filter,database);
        //Set result
        result.setDatasets(dmConverter.toDM(datasets, false));

		return result;
	}

    protected Collection<ODocument> search(SearchFilter filter, OGraphDatabase database) throws Exception {
        Collection<ODocument> datasets = mainFilter.filter(filter, null);
        return datasets;
    }

}
