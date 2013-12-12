package org.fao.fenix.search.services.spi;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.search.dto.SearchFilter;
import org.jboss.resteasy.annotations.GZIP;

@Path("find")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public interface Search {

    @POST
    @GZIP
	@Path("test")
	public Response getDataBasicAlgorithmTest(@Context HttpServletRequest request, SearchFilter filter);
    @POST
    @GZIP
	@Path("data")
	public Response getDataBasicAlgorithm(@Context HttpServletRequest request, SearchFilter filter);

    @POST
    @GZIP
	@Path("meta")
	public Response getMetadataBasicAlgorithm(@Context HttpServletRequest request, SearchFilter filter);
}
