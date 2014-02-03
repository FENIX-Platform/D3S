package org.fao.fenix.msd.services.spi;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDimension;

@Produces
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreDSD {

	//dimension
	@POST
	@Path("dimension")
	public void newDimension(@Context HttpServletRequest request, DSDDimension dimension) throws Exception;
	@PUT
	@Path("dimension")
	public void updateDimension(@Context HttpServletRequest request, DSDDimension dimension) throws Exception;
	@DELETE
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("dimension/{name}")
	public void deleteDimension(@Context HttpServletRequest request, @PathParam("name") String name) throws Exception;

	//context system
	@POST
	@Path("context")
	public void newContextSystem(@Context HttpServletRequest request, DSDContextSystem context) throws Exception;
	@DELETE
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("context/{name}")
	public void deleteContextSystem(@Context HttpServletRequest request, @PathParam("name") String name) throws Exception;
	
	//column
	@PUT
	@Path("column/{datasetUID}")
	public void updateColumn(@Context HttpServletRequest request, @PathParam("datasetUID") String uid, DSDColumn column) throws Exception;
	
	
}
