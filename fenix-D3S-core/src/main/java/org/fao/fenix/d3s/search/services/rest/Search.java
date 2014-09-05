package org.fao.fenix.d3s.search.services.rest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.commons.search.dto.Response;
import org.fao.fenix.commons.search.dto.filter.Filter;
import org.fao.fenix.d3s.search.services.impl.BasicDataSearch;
import org.fao.fenix.d3s.search.services.impl.BasicMetadataSearch;

@Path("find")
public class Search implements org.fao.fenix.d3s.search.services.spi.Search {
    @Context private HttpServletRequest request;
    @Inject private BasicDataSearch searchDataImpl;
    @Inject private BasicMetadataSearch searchMetadataImpl;

    @Override
	public Response getDataBasicAlgorithm(Filter filter) throws Exception {
        return searchDataImpl.search(filter);
	}

	@Override
	public Response getMetadataBasicAlgorithm(Filter filter) throws Exception {
        return searchMetadataImpl.search(filter);
	}



}
