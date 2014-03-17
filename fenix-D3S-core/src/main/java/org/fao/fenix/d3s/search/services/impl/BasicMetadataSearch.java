package org.fao.fenix.d3s.search.services.impl;

import java.util.Collection;
import java.util.LinkedList;

import org.fao.fenix.commons.msd.dto.dm.DM;
import org.fao.fenix.commons.msd.dto.dsd.DSD;
import org.fao.fenix.commons.search.dto.Response;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.commons.search.dto.resource.Resource;
import org.fao.fenix.commons.search.dto.resource.data.TableData;
import org.fao.fenix.d3s.search.bl.datasetFilter.DatasetPropertiesFilter;
import org.fao.fenix.d3s.msd.dao.dm.DMConverter;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.d3s.search.dto.SearchMetadataResponse;
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
	public Response searchFlow(ResourceFilter filter) throws Exception {

        //Dataset filtering
        Collection<ODocument> datasets = search(filter,getFlow().getMsdDatabase());

        //Build result
        if (datasets!=null && datasets.size()>0) {
            Response response = new Response();
            response.addResources(buildResponse(dmConverter.toDM(datasets, false)));
            return response;
        } else
            return null;
	}

    protected Collection<ODocument> search(ResourceFilter filter, OGraphDatabase database) throws Exception {
        Collection<ODocument> datasets = mainFilter.filter(filter, null);
        return datasets;
    }

}
