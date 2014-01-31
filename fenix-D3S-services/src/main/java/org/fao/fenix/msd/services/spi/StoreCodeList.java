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

@Consumes(MediaType.APPLICATION_JSON)
public interface StoreCodeList {

	//code list
	@POST
	@Path("system")
	public Response newCodeList(@Context HttpServletRequest request, CodeSystem cl);
	@PUT
	@Path("system")
	public Response updateCodeList(@Context HttpServletRequest request, CodeSystem cl);
	@PUT
	@Path("system/append")
	public Response appendCodeList(@Context HttpServletRequest request, CodeSystem cl);
    @PUT
    @Consumes()
    @Path("system/index/{system}/{version}")
    public Response restoreCodeList(@Context HttpServletRequest request, @PathParam("system") String system, @PathParam("version") String version);
    @PUT
    @Consumes()
    @Path("system/index")
    public Response restoreCodeList(@Context HttpServletRequest request);

	//code
	@PUT
	@Path("code")
	public Response updateCode(@Context HttpServletRequest request, Code code);
	

	//keyword
	@POST
	@Consumes()
	@Path("keyword/{keyword}")
	public Response newKeyword(@Context HttpServletRequest request, @PathParam("keyword") String keyword);

	//relationship
	@POST
	@Path("relationship")
	public Response newRelationship(@Context HttpServletRequest request, CodeRelationship relation);
	@POST
	@Path("relationships")
	public Response newRelationship(@Context HttpServletRequest request, Collection<CodeRelationship> relation);
	//conversion
	@POST
	@Path("conversion")
	public Response newConversion(@Context HttpServletRequest request, CodeConversion conversion);
	@POST
	@Path("conversions")
	public Response newConversion(@Context HttpServletRequest request, Collection<CodeConversion> conversion);
	@PUT
	@Path("conversion")
	public Response updateConversion(@Context HttpServletRequest request, CodeConversion conversion);
	//propaedeutic
	@POST
	@Path("propaedeutic")
	public Response newPropaedeutic(@Context HttpServletRequest request, CodePropaedeutic propaedeutic);
	@POST
	@Path("propaedeutics")
	public Response newPropaedeutic(@Context HttpServletRequest request, Collection<CodePropaedeutic> propaedeutic);



}
