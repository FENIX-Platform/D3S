package org.fao.fenix.msd.services.spi;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeConversion;
import org.fao.fenix.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;

@Produces
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreCodeList {

	//code list
	@POST
	@Path("system")
	public void newCodeList(@Context HttpServletRequest request, CodeSystem cl) throws Exception;
	@PUT
	@Path("system")
	public void updateCodeList(@Context HttpServletRequest request, CodeSystem cl)  throws Exception;
	@PUT
	@Path("system/append")
	public void appendCodeList(@Context HttpServletRequest request, CodeSystem cl)  throws Exception;
    @PUT
    @Consumes()
    @Path("system/index/{system}/{version}")
    public void restoreCodeList(@Context HttpServletRequest request, @PathParam("system") String system, @PathParam("version") String version)  throws Exception;
    @PUT
    @Consumes()
    @Path("system/index")
    public void restoreCodeList(@Context HttpServletRequest request)  throws Exception;

	//code
	@PUT
	@Path("code")
	public void updateCode(@Context HttpServletRequest request, Code code)  throws Exception;
	

	//keyword
	@POST
	@Consumes()
	@Path("keyword/{keyword}")
	public void newKeyword(@Context HttpServletRequest request, @PathParam("keyword") String keyword)  throws Exception;

	//relationship
	@POST
	@Path("relationship")
	public void newRelationship(@Context HttpServletRequest request, CodeRelationship relation)  throws Exception;
	@POST
	@Path("relationships")
	public void newRelationship(@Context HttpServletRequest request, Collection<CodeRelationship> relation)  throws Exception;
	//conversion
	@POST
	@Path("conversion")
	public void newConversion(@Context HttpServletRequest request, CodeConversion conversion)  throws Exception;
	@POST
	@Path("conversions")
	public void newConversion(@Context HttpServletRequest request, Collection<CodeConversion> conversion)  throws Exception;
	@PUT
	@Path("conversion")
	public void updateConversion(@Context HttpServletRequest request, CodeConversion conversion)  throws Exception;
	//propaedeutic
	@POST
	@Path("propaedeutic")
	public void newPropaedeutic(@Context HttpServletRequest request, CodePropaedeutic propaedeutic)  throws Exception;
	@POST
	@Path("propaedeutics")
	public void newPropaedeutic(@Context HttpServletRequest request, Collection<CodePropaedeutic> propaedeutic)  throws Exception;



}
