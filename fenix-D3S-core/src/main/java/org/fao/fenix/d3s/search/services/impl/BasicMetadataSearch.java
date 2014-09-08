package org.fao.fenix.d3s.search.services.impl;

import java.util.Collection;

import org.fao.fenix.commons.search.dto.Response;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.bl.datasetFilter.DatasetPropertiesFilter;
import org.fao.fenix.d3s.msd.dao.dm.DMConverter;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;

public class BasicMetadataSearch extends SearchOperation {

	//Component dependencies
	@Inject private DatasetPropertiesFilter mainFilter;
//	@Inject private SearchConverterFilter encodeFilter;
	@Inject private DMConverter dmConverter;


	//Search main flow
	@Override
	@SuppressWarnings("unchecked")
	public Response searchFlow(ResourceFilter filter) throws Exception {

        //Dataset filtering
        Collection<ODocument> datasets = search(filter);

        //Build result
        if (datasets!=null && datasets.size()>0) {
            Response response = new Response();
            response.addResources(buildResponse(dmConverter.toDM(datasets, false)));
            return response;
        } else
            return null;
	}

    protected Collection<ODocument> search(ResourceFilter filter) throws Exception {
        getFlow().reset();
        Collection<ODocument> datasets = mainFilter.filter(filter, null);
        return datasets;
    }

}
