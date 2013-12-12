package org.fao.fenix.msd.services.spi;

import org.jboss.resteasy.annotations.GZIP;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("msd/cl")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface LoadCodeList {

	@GET
    @GZIP
	@Path("system/{system}/{version}")
	public Response getCodeList(@Context HttpServletRequest request, @PathParam("system") String system, @PathParam("version") String version, @QueryParam("all") @DefaultValue("true") Boolean all);
	@GET
    @GZIP
	@Path("system")
	public Response getCodeList(@Context HttpServletRequest request, @QueryParam("all") @DefaultValue("false") Boolean all);
	//Returns all the codes at level 0 as property of an empty "root" Code object 
	@GET
    @GZIP
	@Path("code/{system}/{version}")
	public Response getCodesLevel(@Context HttpServletRequest request, @PathParam("system") String system, @PathParam("version") String version, @QueryParam("level") @DefaultValue("1") int level);
	//Returns the code, the children property is filled with the children up to the level passed as param
	@GET
    @GZIP
	@Path("code/{system}/{version}/{code}")
	public Response getCode(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, @PathParam("code") String code, @QueryParam("levels") Integer levels);
	@POST
    @GZIP
	@Path("codes/{system}/{version}")
	public Response getCodes(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, Collection<String> codes, @QueryParam("levels") @DefaultValue("0") Integer levels);
		
	//Returns a Map of code-CodeObject
	@GET
    @GZIP
	@Path("codesMap/{system}/{version}")
	public Response getCodesMap(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, @QueryParam("levels") Integer levels);  
	@GET
    @GZIP
	@Path("codesMap/{system}/{version}/{code}")
	public Response getCodesMap(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, @PathParam("code") String code, @QueryParam("levels") Integer levels);
	@POST
    @GZIP
	@Path("codesMap/{system}/{version}")
	public Response getCodesListMap(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, @QueryParam("levels") Integer levels,  Collection<String> codes);
	
	@GET
    @GZIP
	@Path("keywords")
	public Response getKeywords(@Context HttpServletRequest request);
	
	//relationships
	@GET
    @GZIP
	@Path("relationships/fromCL")
	public Response getRelationshipsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom);
	@GET
    @GZIP
	@Path("relationships/fromCLtoCL")
	public Response getRelationshipsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	@GET
    @GZIP
	@Path("relationships/fromC")
	public Response getRelationshipsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom);
	@GET
    @GZIP
	@Path("relationships/fromCtoC")
	public Response getRelationshipsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo);
	@GET
    @GZIP
	@Path("relationships/fromCtoCL")
	public Response getRelationshipsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	@GET
    @GZIP
	@Path("relationships/fromCLT")
	public Response getRelationshipsFromCLT(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("type") String typeCode);
	@GET
    @GZIP
	@Path("relationships/fromCT")
	public Response getRelationshipsFromCT(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("type") String typeCode);
	//conversions
	@GET
    @GZIP
	@Path("conversions/fromCL")
	public Response getConversionsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom);
	@GET
    @GZIP
	@Path("conversions/fromCLtoCL")
	public Response getConversionsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	@GET
    @GZIP
	@Path("conversions/fromC")
	public Response getConversionsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom);
	@GET
    @GZIP
	@Path("conversions/fromCtoC")
	public Response getConversionsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo);
	@GET
    @GZIP
	@Path("conversions/fromCtoCL")
	public Response getConversionsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	
	//propaedeutics
	@GET
    @GZIP
	@Path("propaedeutics/fromCL")
	public Response getPropaedeuticsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom);
	@GET
    @GZIP
	@Path("propaedeutics/fromCLtoCL")
	public Response getPropaedeuticsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
	@GET
    @GZIP
	@Path("propaedeutics/fromC")
	public Response getPropaedeuticsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom);
	@GET
    @GZIP
	@Path("propaedeutics/fromCtoC")
	public Response getPropaedeuticsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo);
	@GET
    @GZIP
	@Path("propaedeutics/fromCtoCL")
	public Response getPropaedeuticsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo);
}
