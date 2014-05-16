package org.fao.fenix.d3s.search.services.spi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.commons.search.dto.Response;
import org.fao.fenix.commons.search.dto.filter.Filter;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public interface Search {

    @POST
	@Path("data")
	public Response getDataBasicAlgorithm(Filter filter) throws Exception;

    @POST
	@Path("meta")
	public Response getMetadataBasicAlgorithm(Filter filter) throws Exception;
}
