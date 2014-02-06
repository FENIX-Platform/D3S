package org.fao.fenix.msd.services.spi;

import java.util.Collection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
	public void newCodeList(CodeSystem cl) throws Exception;
	@PUT
	@Path("system")
	public void updateCodeList(CodeSystem cl)  throws Exception;
	@PUT
	@Path("system/append")
	public void appendCodeList(CodeSystem cl)  throws Exception;
    @PUT
    @Consumes()
    @Path("system/index/{system}/{version}")
    public void restoreCodeList(@PathParam("system") String system, @PathParam("version") String version)  throws Exception;
    @PUT
    @Consumes()
    @Path("system/index")
    public void restoreCodeList()  throws Exception;

	//code
	@PUT
	@Path("code")
	public void updateCode(Code code)  throws Exception;
	

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
