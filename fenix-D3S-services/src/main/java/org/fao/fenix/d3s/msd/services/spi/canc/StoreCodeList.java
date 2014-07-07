package org.fao.fenix.d3s.msd.services.spi.canc;

import java.util.Collection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodePropaedeutic;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.Code;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodeConversion;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodeRelationship;

@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreCodeList {

	//code list
	@POST
	@Path("system")
    @Consumes({MediaType.APPLICATION_JSON, "application/csv"})
	public String newCodeList(CodeSystem cl) throws Exception;
	@PUT
	@Path("system")
    @Consumes({MediaType.APPLICATION_JSON, "application/csv"})
	public Integer updateCodeList(CodeSystem cl, @QueryParam("all") @DefaultValue("false") boolean all)  throws Exception;
	@PUT
	@Path("system/append")
    @Consumes({MediaType.APPLICATION_JSON, "application/csv"})
	public Integer appendCodeList(CodeSystem cl, @QueryParam("all") @DefaultValue("true") boolean all)  throws Exception;

	//code
	@PUT
	@Path("code")
	public void updateCode(Code code)  throws Exception;
	@PUT
	@Path("codes")
	public void updateCodes(CodeSystem cl)  throws Exception;
	@PUT
	@Path("codes/append")
	public void appendCodes(CodeSystem cl)  throws Exception;

    //Index
    @PUT
    @Path("system/index/{system}/{version}")
    public void rebuildIndex(@PathParam("system") String system, @PathParam("version") String version)  throws Exception;



	//keyword
	@POST
	@Consumes()
	@Path("keyword/{keyword}")
	public void newKeyword(@PathParam("keyword") String keyword)  throws Exception;

	//relationship
	@POST
	@Path("relationship")
	public void newRelationship(CodeRelationship relation)  throws Exception;
	@POST
	@Path("relationships")
	public void newRelationship(Collection<CodeRelationship> relation)  throws Exception;
	//conversion
	@POST
	@Path("conversion")
	public void newConversion(CodeConversion conversion)  throws Exception;
	@POST
	@Path("conversions")
	public void newConversion(Collection<CodeConversion> conversion)  throws Exception;
	@PUT
	@Path("conversion")
	public void updateConversion(CodeConversion conversion)  throws Exception;
	//propaedeutic
	@POST
	@Path("propaedeutic")
	public void newPropaedeutic(CodePropaedeutic propaedeutic)  throws Exception;
	@POST
	@Path("propaedeutics")
	public void newPropaedeutic(Collection<CodePropaedeutic> propaedeutic)  throws Exception;



}
