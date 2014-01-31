package org.fao.fenix.cl.services.spi;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.msd.dto.cl.CodeSystem;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface MergeCL {

	@POST
	@Path("default")
	public Response getStandardMerge(Collection<CodeSystem> clList);
	@POST
	@Path("interception")
	public Response getInterceptionMerge(Collection<CodeSystem> clList);
	@POST
	@Path("union")
	public Response getUnionMerge(Collection<CodeSystem> clList);
	
	@PUT
	@Path("update/default")
	public Response updStandardMerge(Collection<CodeSystem> clList);
	@PUT
	@Path("update/interception")
	public Response updInterceptionMerge(Collection<CodeSystem> clList);
	@PUT
	@Path("update/union")
	public Response updUnionMerge(Collection<CodeSystem> clList);
	
}
