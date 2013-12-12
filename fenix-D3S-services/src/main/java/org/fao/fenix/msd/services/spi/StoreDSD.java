package org.fao.fenix.msd.services.spi;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDimension;

@Path("msd/dsd")
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreDSD {
	//dimension
	@POST
	@Path("dimension")
	public Response newDimension(@Context HttpServletRequest request, DSDDimension dimension);
	@PUT
	@Path("dimension")
	public Response updateDimension(@Context HttpServletRequest request, DSDDimension dimension);
	@DELETE
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("dimension/{name}")
	public Response deleteDimension(@Context HttpServletRequest request, @PathParam("name") String name);
	//context system
	@POST
	@Path("context")
	public Response newContextSystem(@Context HttpServletRequest request, DSDContextSystem context);
	@DELETE
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("context/{name}")
	public Response deleteContextSystem(@Context HttpServletRequest request, @PathParam("name") String name);
	
	//column
	@PUT
	@Path("column/{datasetUID}")
	public Response updateColumn(@Context HttpServletRequest request, @PathParam("datasetUID") String uid, DSDColumn column);
	
	
}
