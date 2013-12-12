package org.fao.fenix.msd.services.spi;

import org.jboss.resteasy.annotations.GZIP;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("msd/dsd")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface LoadDSD {

	@GET
    @GZIP
	@Path("datasource")
	public Response getDatasources(@Context HttpServletRequest request);
	@GET
    @GZIP
	@Path("dimension")
	public Response getDimensions(@Context HttpServletRequest request);
	@GET
    @GZIP
	@Path("context")
	public Response getContextSystems(@Context HttpServletRequest request);
	
}
