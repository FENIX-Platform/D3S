package org.fao.fenix.d3s.msd.services.spi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.commons.msd.dto.dsd.DSDColumn;
import org.fao.fenix.commons.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.commons.msd.dto.dsd.DSDDimension;

@Produces
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreDSD {

	//dimension
	@POST
	@Path("dimension")
	public void newDimension(DSDDimension dimension) throws Exception;
	@PUT
	@Path("dimension")
	public void updateDimension(DSDDimension dimension) throws Exception;
	@DELETE
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("dimension/{name}")
	public void deleteDimension(@PathParam("name") String name) throws Exception;

	//context system
	@POST
	@Path("context")
	public void newContextSystem(DSDContextSystem context) throws Exception;
	@DELETE
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("context/{name}")
	public void deleteContextSystem(@PathParam("name") String name) throws Exception;
	
	//column
	@PUT
	@Path("column/{datasetUID}")
	public void updateColumn(@PathParam("datasetUID") String uid, DSDColumn column) throws Exception;
	
	
}
