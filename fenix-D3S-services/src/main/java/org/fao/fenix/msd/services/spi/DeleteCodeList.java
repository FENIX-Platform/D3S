package org.fao.fenix.msd.services.spi;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("msd/cl")
public interface DeleteCodeList {

	@DELETE
	@Path("system/{system}/{version}")
	public Response deleteCodeList(@Context HttpServletRequest request, @PathParam("system") String system, @PathParam("version") String version);
	
	@DELETE
	@Path("keyword/{keyword}")
	public Response deleteKeyword(@Context HttpServletRequest request, @PathParam("keyword") String keyword);
	//relationships
	@DELETE
	@Path("relationships/fromCtoC")
	public Response deleteRelationshipsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo);
	@DELETE
	@Path("relationships/fromCL")
	public Response deleteRelationshipsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom);
	@DELETE
	@Path("relationships/fromCLtoCL")
	public Response deleteRelationshipsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	@DELETE
	@Path("relationships/fromC")
	public Response deleteRelationshipsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom);
	@DELETE
	@Path("relationships/fromCtoCL")
	public Response deleteRelationshipsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	//conversions
	@DELETE
	@Path("conversions/fromCtoC")
	public Response deleteConversionsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo);
	@DELETE
	@Path("conversions/fromCL")
	public Response deleteConversionsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom);
	@DELETE
	@Path("conversions/fromCLtoCL")
	public Response deleteConversionsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	@DELETE
	@Path("conversions/fromC")
	public Response deleteConversionsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom);
	@DELETE
	@Path("conversions/fromCtoCL")
	public Response deleteConversionsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	//propaedeutics
	@DELETE
	@Path("propaedeutics/fromCtoC")
	public Response deletePropaedeuticsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo);
	@DELETE
	@Path("propaedeutics/fromCL")
	public Response deletePropaedeuticsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom);
	@DELETE
	@Path("propaedeutics/fromCLtoCL")
	public Response deletePropaedeuticsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	@DELETE
	@Path("propaedeutics/fromC")
	public Response deletePropaedeuticsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom);
	@DELETE
	@Path("propaedeutics/fromCtoCL")
	public Response deletePropaedeuticsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	
}
