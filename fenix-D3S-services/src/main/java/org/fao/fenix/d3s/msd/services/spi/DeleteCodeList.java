package org.fao.fenix.d3s.msd.services.spi;

import javax.ws.rs.*;

@Produces
@Consumes
public interface DeleteCodeList {

	@DELETE
	@Path("system/{system}/{version}")
	public void deleteCodeList(@PathParam("system") String system, @PathParam("version") String version) throws Exception;
	
	@DELETE
	@Path("keyword/{keyword}")
	public void deleteKeyword(@PathParam("keyword") String keyword) throws Exception;
	//relationships
	@DELETE
	@Path("relationships/fromCtoC")
	public void deleteRelationshipsFromCtoC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@DELETE
	@Path("relationships/fromCL")
	public void deleteRelationshipsFromCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@DELETE
	@Path("relationships/fromCLtoCL")
	public void deleteRelationshipsFromCLToCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@DELETE
	@Path("relationships/fromC")
	public void deleteRelationshipsFromC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@DELETE
	@Path("relationships/fromCtoCL")
	public void deleteRelationshipsFromCtoCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	//conversions
	@DELETE
	@Path("conversions/fromCtoC")
	public void deleteConversionsFromCtoC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@DELETE
	@Path("conversions/fromCL")
	public void deleteConversionsFromCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@DELETE
	@Path("conversions/fromCLtoCL")
	public void deleteConversionsFromCLToCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@DELETE
	@Path("conversions/fromC")
	public void deleteConversionsFromC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@DELETE
	@Path("conversions/fromCtoCL")
	public void deleteConversionsFromCtoCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	//propaedeutics
	@DELETE
	@Path("propaedeutics/fromCtoC")
	public void deletePropaedeuticsFromCtoC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@DELETE
	@Path("propaedeutics/fromCL")
	public void deletePropaedeuticsFromCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@DELETE
	@Path("propaedeutics/fromCLtoCL")
	public void deletePropaedeuticsFromCLToCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@DELETE
	@Path("propaedeutics/fromC")
	public void deletePropaedeuticsFromC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@DELETE
	@Path("propaedeutics/fromCtoCL")
	public void deletePropaedeuticsFromCtoCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	
}
