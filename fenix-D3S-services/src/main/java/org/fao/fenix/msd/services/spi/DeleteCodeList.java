package org.fao.fenix.msd.services.spi;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@Produces
@Consumes
public interface DeleteCodeList {

	@DELETE
	@Path("system/{system}/{version}")
	public void deleteCodeList(@Context HttpServletRequest request, @PathParam("system") String system, @PathParam("version") String version) throws Exception;
	
	@DELETE
	@Path("keyword/{keyword}")
	public void deleteKeyword(@Context HttpServletRequest request, @PathParam("keyword") String keyword) throws Exception;
	//relationships
	@DELETE
	@Path("relationships/fromCtoC")
	public void deleteRelationshipsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@DELETE
	@Path("relationships/fromCL")
	public void deleteRelationshipsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@DELETE
	@Path("relationships/fromCLtoCL")
	public void deleteRelationshipsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@DELETE
	@Path("relationships/fromC")
	public void deleteRelationshipsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@DELETE
	@Path("relationships/fromCtoCL")
	public void deleteRelationshipsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	//conversions
	@DELETE
	@Path("conversions/fromCtoC")
	public void deleteConversionsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@DELETE
	@Path("conversions/fromCL")
	public void deleteConversionsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@DELETE
	@Path("conversions/fromCLtoCL")
	public void deleteConversionsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@DELETE
	@Path("conversions/fromC")
	public void deleteConversionsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@DELETE
	@Path("conversions/fromCtoCL")
	public void deleteConversionsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	//propaedeutics
	@DELETE
	@Path("propaedeutics/fromCtoC")
	public void deletePropaedeuticsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@DELETE
	@Path("propaedeutics/fromCL")
	public void deletePropaedeuticsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@DELETE
	@Path("propaedeutics/fromCLtoCL")
	public void deletePropaedeuticsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@DELETE
	@Path("propaedeutics/fromC")
	public void deletePropaedeuticsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@DELETE
	@Path("propaedeutics/fromCtoCL")
	public void deletePropaedeuticsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	
}
