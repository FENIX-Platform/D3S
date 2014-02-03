package org.fao.fenix.search.services.spi;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.search.dto.SearchResponse;
import org.jboss.resteasy.annotations.GZIP;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public interface Search {

    @POST
    @GZIP
	@Path("test")
	public SearchResponse getDataBasicAlgorithmTest(@Context HttpServletRequest request, SearchFilter filter) throws Exception;
    @POST
    @GZIP
	@Path("data")
	public SearchResponse getDataBasicAlgorithm(@Context HttpServletRequest request, SearchFilter filter) throws Exception;

    @POST
    @GZIP
	@Path("meta")
	public SearchResponse getMetadataBasicAlgorithm(@Context HttpServletRequest request, SearchFilter filter) throws Exception;
}
