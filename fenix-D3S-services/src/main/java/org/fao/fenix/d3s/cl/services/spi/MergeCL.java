package org.fao.fenix.d3s.cl.services.spi;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.commons.msd.dto.cl.CodeSystem;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface MergeCL {

	@POST
	@Path("default")
	public CodeSystem getStandardMerge(Collection<CodeSystem> clList) throws Exception;
	@POST
	@Path("interception")
	public CodeSystem getInterceptionMerge(Collection<CodeSystem> clList) throws Exception;
	@POST
	@Path("union")
	public CodeSystem getUnionMerge(Collection<CodeSystem> clList) throws Exception;
	
	@PUT
	@Path("update/default")
	public CodeSystem updStandardMerge(Collection<CodeSystem> clList) throws Exception;
	@PUT
	@Path("update/interception")
	public CodeSystem updInterceptionMerge(Collection<CodeSystem> clList) throws Exception;
	@PUT
	@Path("update/union")
	public CodeSystem updUnionMerge(Collection<CodeSystem> clList) throws Exception;
	
}
