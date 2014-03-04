package org.fao.fenix.d3s.search.services.spi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.d3s.search.dto.SearchDataResponse;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.d3s.search.dto.SearchMetadataResponse;
import org.jboss.resteasy.annotations.GZIP;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
public interface Search {

    @POST
    @GZIP
	@Path("test")
	public SearchDataResponse getDataBasicAlgorithmTest(SearchFilter filter) throws Exception;
    @POST
    @GZIP
	@Path("data")
	public SearchDataResponse getDataBasicAlgorithm(SearchFilter filter) throws Exception;

    @POST
    @GZIP
	@Path("meta")
	public SearchMetadataResponse getMetadataBasicAlgorithm(SearchFilter filter) throws Exception;
}
